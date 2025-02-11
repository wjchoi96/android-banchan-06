package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanDetailModel
import com.woowahan.domain.usecase.banchan.FetchBanchanDetailUseCase
import com.woowahan.domain.usecase.cart.GetCartItemsSizeFlowUseCase
import com.woowahan.domain.usecase.cart.InsertCartItemUseCase
import com.woowahan.domain.usecase.order.GetDeliveryOrderCountUseCase
import com.woowahan.domain.usecase.recentviewed.InsertRecentViewedItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
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

    private val _quantity: MutableStateFlow<Int> = MutableStateFlow(1)
    val quantity = _quantity.asStateFlow()

    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

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
                .launchIn(this + Dispatchers.IO)
        }
    }

    fun fetchBanchanDetail() {
        if (_dataLoading.value) {
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
                            _eventFlow.emit(UiEvent.FinishView(message))
                        }
                    }.also {
                        _dataLoading.value = false
                    }
                }
        }
    }

    fun insertItemsToCart(banchanDetail: BanchanDetailModel) {
        viewModelScope.launch {
            _dataLoading.emit(true)
            insertCartItemUseCase.invoke(banchanDetail.hash, banchanDetail.title, quantity.value)
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
        viewModelScope.launch {
            val currentQuantity = quantity.value
            if (currentQuantity != 1) {
                _quantity.emit(currentQuantity - 1)
            }
        }
    }

    val plusClicked: () -> Unit = {
        viewModelScope.launch {
            val currentQuantity = quantity.value
            if (currentQuantity != 999) {
                _quantity.emit(currentQuantity + 1)
            }
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
        data class FinishView(val message: String? = null) : UiEvent()
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowDialog(val dialogBuilder: DialogUtil.DialogCustomBuilder) : UiEvent()
        object ShowCartView : UiEvent()
    }
}