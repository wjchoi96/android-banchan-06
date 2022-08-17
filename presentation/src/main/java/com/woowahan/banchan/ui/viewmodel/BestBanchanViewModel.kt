package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.extension.getNewListApplyCartState
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.usecase.FetchBestBanchanUseCase
import com.woowahan.domain.usecase.InsertCartItemUseCase
import com.woowahan.domain.usecase.RemoveCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BestBanchanViewModel @Inject constructor(
    private val fetchBestBanchanUseCase: FetchBestBanchanUseCase,
    private val insertCartItemUseCase: InsertCartItemUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
): ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _refreshDataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshDataLoading = _refreshDataLoading.asStateFlow()

    private val _banchans: MutableStateFlow<List<BestBanchanModel>> = MutableStateFlow(emptyList())
    val banchans = _banchans.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    fun fetchBestBanchans() {
        if (_dataLoading.value) {
            _refreshDataLoading.value = false
            return
        }
        viewModelScope.launch {
            _dataLoading.value = true
            fetchBestBanchanUseCase.invoke()
                .flowOn(Dispatchers.Default)
                .collect { res ->
                    res.onSuccess {
                        _banchans.value = it
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

    val clickInsertCartButton: (BanchanModel, Boolean)->(Unit) = { banchan, isCartItem ->
        viewModelScope.launch {
            when(isCartItem){
                true -> removeItemFromCart(banchan)
                else -> {
                    val dialog = CartItemInsertBottomSheet(banchan){ item, count ->
                        insertItemsToCart(item, count)
                    }
                    _eventFlow.emit(UiEvent.ShowCartBottomSheet(dialog))
                }
            }
        }
    }

    val itemClickListener: (BanchanModel) -> Unit = {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowDetailView(it))
        }
    }

    private fun removeItemFromCart(banchanModel: BanchanModel){
        viewModelScope.launch {
            _dataLoading.emit(true)
            removeCartItemUseCase.invoke(banchanModel.hash)
                .onSuccess {
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

    private fun insertItemsToCart(banchanModel: BanchanModel, count: Int){
        viewModelScope.launch {
            _dataLoading.emit(true)
            insertCartItemUseCase.invoke(banchanModel, count)
                .onSuccess {
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

    private fun getCartItemUpdateDialog(content: String): DialogUtil.DialogCustomBuilder{
        return DialogUtil.DialogCustomBuilder(
            content,
            "계속 쇼핑하기" to {},
            "장바구니 확인" to {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowCartView)
                }
            }
        )
    }


    fun onRefresh() {
        _refreshDataLoading.value = true
        fetchBestBanchans()
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowDialog(val dialogBuilder: DialogUtil.DialogCustomBuilder): UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet): UiEvent()
        object ShowCartView: UiEvent()
        data class ShowDetailView(val banchanModel: BanchanModel): UiEvent()
    }
}