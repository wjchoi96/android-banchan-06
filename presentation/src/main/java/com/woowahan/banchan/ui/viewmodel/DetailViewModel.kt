package com.woowahan.banchan.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanDetailModel
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BaseBanchan
import com.woowahan.domain.usecase.banchan.FetchBanchanDetailUseCase
import com.woowahan.domain.usecase.cart.GetCartItemsSizeFlowUseCase
import com.woowahan.domain.usecase.cart.InsertCartItemUseCase
import com.woowahan.domain.usecase.order.GetDeliveryOrderCountUseCase
import com.woowahan.domain.usecase.recentviewed.InsertRecentViewedItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val fetchBanchanDetailUseCase: FetchBanchanDetailUseCase,
    private val insertRecentViewedItemUseCase: InsertRecentViewedItemUseCase,
    private val insertCartItemUseCase: InsertCartItemUseCase,
    private val getCartItemsSizeFlowUseCase: GetCartItemsSizeFlowUseCase,
    private val getDeliveryOrderCountUseCase: GetDeliveryOrderCountUseCase
) : ViewModel() {
    private var hash = ""
    private var title = ""
    val quantity = ObservableField(1)

    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _refreshDataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshDataLoading = _refreshDataLoading.asStateFlow()

    private val _detail: MutableStateFlow<BanchanDetailModel> =
        MutableStateFlow(BanchanDetailModel.empty())
    val detail = _detail.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _cartItemSize: MutableStateFlow<Int> = MutableStateFlow(0)
    val cartItemSize = _cartItemSize.asStateFlow()

    private val _deliveryItemSize: MutableStateFlow<Int> = MutableStateFlow(0)
    val deliveryItemSize = _deliveryItemSize.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                getCartItemsSizeFlowUseCase()
                    .flowOn(Dispatchers.Default)
                    .collect { flow ->
                        Timber.d("getCartItemsSizeFlowUseCase on viewModel")
                        flow.onSuccess {
                            _cartItemSize.emit(it)
                        }.onFailureWithData { it, data ->
                            it.printStackTrace()
                            Timber.d("catch debug onFailure $it")
                            data?.let {
                                _cartItemSize.emit(it)
                            }
                        }
                    }
            }

            launch {
                getDeliveryOrderCountUseCase()
                    .flowOn(Dispatchers.Default)
                    .collect { flow ->
                        Timber.d("getDeliveryOrderCount on viewModel")
                        flow.onSuccess { count ->
                            _deliveryItemSize.emit(count)
                        }.onFailureWithData { it, data ->
                            it.printStackTrace()
                            Timber.d("catch debug onFailure $it")
                            data?.let {
                                _deliveryItemSize.emit(it)
                            }
                        }
                    }
            }
        }
    }

    fun insertRecentViewedItem(hash: String, title: String) {
        viewModelScope.launch {
            insertRecentViewedItemUseCase(hash, title, Calendar.getInstance().time)
                .flowOn(Dispatchers.IO)
                .collect()
        }
    }

    fun fetchBanchanDetail() {
        if (_dataLoading.value) {
            _refreshDataLoading.value = false
            return
        }
        viewModelScope.launch {
            _dataLoading.value = true
            fetchBanchanDetailUseCase(hash, title)
                .flowOn(Dispatchers.Default)
                .collect { res ->
                    res.onSuccess {
                        _detail.value = it
                    }.onFailure {
                        it.printStackTrace()
                        it.message?.let { message ->
                            _eventFlow.emit(UiEvent.ShowToast(message))
                        }
                    }.also {
                        _dataLoading.value = false
                        if (_refreshDataLoading.value)
                            _refreshDataLoading.value = false
                    }
                }
        }
    }

    fun insertItemsToCart(banchanDetail: BanchanDetailModel) {
        viewModelScope.launch {
            _dataLoading.emit(true)
            insertCartItemUseCase.invoke(banchanDetail.hash, banchanDetail.title, quantity.get()!!)
                .flowOn(Dispatchers.Default)
                .collect { event ->
                    event.onSuccess {
                        insertCartResultEvent(banchanDetail, it)
                    }.onFailure {
                        insertCartThrowableEvent(it)
                    }.also {
                        _dataLoading.emit(false)
                    }
                }
        }
    }

    private val insertCartThrowableEvent: (Throwable) -> Unit
        get() = {
            viewModelScope.launch {
                it.printStackTrace()
                it.message?.let { message ->
                    _eventFlow.emit(UiEvent.ShowSnackBar(message))
                }
            }
        }

    val minusClicked: () -> Unit = {
        val currentQuantity = quantity.get()
        currentQuantity?.let {
            if (currentQuantity != 1) {
                quantity.set(currentQuantity - 1)
            }
        }
    }

    val plusClicked: () -> Unit = {
        val currentQuantity = quantity.get()
        currentQuantity?.let {
            quantity.set(currentQuantity + 1)
        }
    }

    private val insertCartResultEvent: (BanchanDetailModel, Boolean) -> Unit
        get() = { _, _ ->
            viewModelScope.launch {
                _eventFlow.emit(
                    UiEvent.ShowDialog(
                        getCartItemUpdateDialog("선택한 상품이 장바구니에 담겼습니다") {
                            viewModelScope.launch {
                                _eventFlow.emit(UiEvent.ShowCartView)
                            }
                        }
                    ))
            }
        }

    private fun getCartItemUpdateDialog(
        content: String,
        showCartEvent: () -> Unit
    ): DialogUtil.DialogCustomBuilder {
        return DialogUtil.DialogCustomBuilder(
            content,
            "계속 쇼핑하기" to {},
            "장바구니 확인" to showCartEvent
        )
    }


    fun initDate(hash: String?, title: String?) {
        this.hash = hash ?: ""
        this.title = title ?: ""
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowDialog(val dialogBuilder: DialogUtil.DialogCustomBuilder) : UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet) : UiEvent()
        object ShowCartView : UiEvent()
    }
}