package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BaseBanchan
import com.woowahan.domain.usecase.banchan.FetchMainDishBanchanUseCase
import com.woowahan.domain.usecase.cart.InsertCartItemUseCase
import com.woowahan.domain.usecase.cart.RemoveCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainDishBanchanViewModel @Inject constructor(
    private val fetchMainDishBanchanUseCase: FetchMainDishBanchanUseCase,
    override val insertCartItemUseCase: InsertCartItemUseCase,
    override val removeCartItemUseCase: RemoveCartItemUseCase
) : BaseCartUpdateViewModel() {
    override val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _banchans: MutableStateFlow<List<BanchanModel>> = MutableStateFlow(emptyList())
    val banchans = _banchans.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    val gridSpanCount = 2
    private val linearSpanCount = 1
    var spanCount = 2
        private set
    var gridViewModel: Boolean = true
        private set

    var filter = BanchanModel.FilterType.Default
        private set
    private lateinit var defaultBanchans: List<BanchanModel>

    val itemClickListener: (BanchanModel) -> Unit = { banchan ->
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowDetailView(banchan))
        }
    }

    init {
        fetchMainDishBanchans()
    }

    private fun fetchMainDishBanchans() {
        refreshJob()
        prevJob = viewModelScope.launch {
            _dataLoading.value = true
            fetchMainDishBanchanUseCase.invoke()
                .flowOn(Dispatchers.Default)
                .collect { res ->
                    res.onSuccess {
                        defaultBanchans = it
                        _banchans.value = it.filterType(filter, defaultBanchans)
                        hideErrorView()
                    }.onFailure {
                        it.printStackTrace()
                        showErrorView(it, ErrorViewButtonType.Retry){
                            fetchMainDishBanchans()
                        }
                    }.also {
                        _dataLoading.value = false
                    }
                }
        }
    }

    val viewModeToggleEvent: (Boolean) -> (Unit) = {
        gridViewModel = it
        spanCount = when(it){
            true -> gridSpanCount
            else -> linearSpanCount
        }
        viewModelScope.launch { _eventFlow.emit(UiEvent.ChangeViewMode(it)) }
    }

    val clickInsertCartButton: (BanchanModel, Boolean) -> (Unit) = { banchan, isCartItem ->
        viewModelScope.launch {
            when (isCartItem) {
                true -> removeItemFromCart(banchan)
                else -> {
                    val dialog = CartItemInsertBottomSheet.get(banchan) { item, count ->
                        insertItemsToCart(item, count)
                    }
                    _eventFlow.emit(UiEvent.ShowCartBottomSheet(dialog))
                }
            }
        }
    }

    override val insertCartResultEvent: (BaseBanchan, Boolean) -> Unit
        get() = { _, _ ->
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowDialog(
                    getCartItemUpdateDialog("선택한 상품이 장바구니에 담겼습니다") {
                        viewModelScope.launch {
                            _eventFlow.emit(UiEvent.ShowCartView)
                        }
                    }
                ))
            }
        }

    override val insertCartThrowableEvent: (Throwable) -> Unit
        get() = {
            viewModelScope.launch {
                it.printStackTrace()
                it.message?.let { message ->
                    _eventFlow.emit(UiEvent.ShowSnackBar(message))
                }
            }
        }

    override val removeCartResultEvent: (BaseBanchan, Boolean) -> Unit
        get() = { _, _ ->
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowDialog(
                    getCartItemUpdateDialog("선택한 상품이 장바구니에서 제거되었습니다") {
                        viewModelScope.launch {
                            _eventFlow.emit(UiEvent.ShowCartView)
                        }
                    }
                ))
            }
        }
    override val removeCartThrowableEvent: (Throwable) -> Unit
        get() = {
            viewModelScope.launch {
                it.printStackTrace()
                it.message?.let { message ->
                    _eventFlow.emit(UiEvent.ShowToast(message))
                }
            }
        }

    val filterItemSelect: (Int) -> Unit = {
        filter = BanchanModel.FilterType.find(it) ?: BanchanModel.FilterType.Default
        _banchans.value = _banchans.value.filterType(filter, defaultBanchans)
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowDialog(val dialogBuilder: DialogUtil.DialogCustomBuilder) : UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet) : UiEvent()
        object ShowCartView : UiEvent()
        data class ShowDetailView(val banchanModel: BanchanModel) : UiEvent()
        data class ChangeViewMode(val isGridMode: Boolean) : UiEvent()
    }
}