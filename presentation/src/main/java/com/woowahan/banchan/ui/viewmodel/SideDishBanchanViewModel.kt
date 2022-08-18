package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.extension.filterType
import com.woowahan.banchan.extension.getNewListApplyCartState
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.usecase.banchan.FetchSideDishBanchanUseCase
import com.woowahan.domain.usecase.cart.InsertCartItemUseCase
import com.woowahan.domain.usecase.cart.RemoveCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SideDishBanchanViewModel @Inject constructor(
    private val fetchSideBanchanUseCase: FetchSideDishBanchanUseCase,
    private val insertCartItemUseCase: InsertCartItemUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _refreshDataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshDataLoading = _refreshDataLoading.asStateFlow()

    private val _banchans: MutableStateFlow<List<BanchanModel>> = MutableStateFlow(emptyList())
    val banchans = _banchans.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()


    private lateinit var defaultBanchans: List<BanchanModel>

    val clickInsertCartButton: (BanchanModel, Boolean) -> (Unit) = { banchan, isCartItem ->
        viewModelScope.launch {
            when (isCartItem) {
                true -> removeItemFromCart(banchan)
                else -> {
                    val dialog = CartItemInsertBottomSheet(banchan) { item, count ->
                        insertItemsToCart(item, count)
                    }
                    _eventFlow.emit(UiEvent.ShowCartBottomSheet(dialog))
                }
            }
        }
    }

    fun fetchSoupDishBanchans() {
        if (_dataLoading.value) {
            _refreshDataLoading.value = false
            return
        }
        viewModelScope.launch {
            _dataLoading.value = true
            fetchSideBanchanUseCase.invoke()
                .flowOn(Dispatchers.Default)
                .collect { res ->
                    res.onSuccess {
                        defaultBanchans = it
                        _banchans.value = defaultBanchans
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

    private fun removeItemFromCart(banchanModel: BanchanModel) {
        viewModelScope.launch {
            _dataLoading.emit(true)
            removeCartItemUseCase.invoke(banchanModel.hash)
                .onSuccess {
                    defaultBanchans = defaultBanchans.getNewListApplyCartState(banchanModel, false)
                    _banchans.value = _banchans.value.getNewListApplyCartState(banchanModel, false)
                    _eventFlow.emit(UiEvent.ShowDialog(
                        getCartItemUpdateDialog("선택한 상품이 장바구니에서 제거되었습니다")
                    ))
                }.onFailure {
                    it.printStackTrace()
                    it.message?.let { message ->
                        _eventFlow.emit(UiEvent.ShowToast(message))
                    }
                }.also {
                    _dataLoading.emit(false)
                }
        }
    }

    private fun insertItemsToCart(banchanModel: BanchanModel, count: Int) {
        viewModelScope.launch {
            _dataLoading.emit(true)
            insertCartItemUseCase.invoke(banchanModel, count)
                .onSuccess {
                    defaultBanchans = defaultBanchans.getNewListApplyCartState(banchanModel, true)
                    _banchans.value = _banchans.value.getNewListApplyCartState(banchanModel, true)
                    _eventFlow.emit(UiEvent.ShowDialog(
                        getCartItemUpdateDialog("선택한 상품이 장바구니에 담겼습니다")
                    ))
                }.onFailure {
                    it.printStackTrace()
                    it.message?.let { message ->
                        _eventFlow.emit(UiEvent.ShowSnackBar(message))
                    }
                }.also {
                    _dataLoading.emit(false)
                }
        }
    }

    private fun filterBanchan(filterType: BanchanModel.FilterType) {
        viewModelScope.launch {
            if (filterType ==
                BanchanModel.FilterType.Default
            ) {
                _banchans.value = defaultBanchans
            } else {
                kotlin.runCatching {
                    _banchans.value = defaultBanchans.filterType(filterType)
                }.onFailure {
                    it.printStackTrace()
                    it.message?.let { message ->
                        _eventFlow.emit(UiEvent.ShowToast(message))
                    }
                }
            }
        }
    }

    private fun getCartItemUpdateDialog(content: String): DialogUtil.DialogCustomBuilder{
        return DialogUtil.DialogCustomBuilder(
            content,
            "계속 쇼핑하기" to {},
            "장바구니 확인" to {
                viewModelScope.launch {
                    _eventFlow.emit(SideDishBanchanViewModel.UiEvent.ShowCartView)
                }
            }
        )
    }

    val filterItemSelect: (Int) -> Unit = {
        when (it) {
            BanchanModel.FilterType.Default.value -> {
                filterBanchan(BanchanModel.FilterType.Default)
            }
            BanchanModel.FilterType.PriceHigher.value -> {
                filterBanchan(BanchanModel.FilterType.PriceHigher)
            }
            BanchanModel.FilterType.PriceLower.value -> {
                filterBanchan(BanchanModel.FilterType.PriceLower)
            }
            else -> {
                filterBanchan(BanchanModel.FilterType.SalePercentHigher)
            }
        }
    }

    fun onRefresh() {
        _refreshDataLoading.value = true
        fetchSoupDishBanchans()
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowDialog(val dialogBuilder: DialogUtil.DialogCustomBuilder): UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet): UiEvent()
        object ShowCartView: UiEvent()
    }
}