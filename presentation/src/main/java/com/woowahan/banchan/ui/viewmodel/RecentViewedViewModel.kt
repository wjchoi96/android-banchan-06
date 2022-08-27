package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BaseBanchan
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.usecase.cart.InsertCartItemUseCase
import com.woowahan.domain.usecase.cart.RemoveCartItemUseCase
import com.woowahan.domain.usecase.recentviewed.FetchRecentViewedPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentViewedViewModel @Inject constructor(
    private val fetchRecentViewedPagingUseCase: FetchRecentViewedPagingUseCase,
    override val insertCartItemUseCase: InsertCartItemUseCase,
    override val removeCartItemUseCase: RemoveCartItemUseCase,
) : BaseCartUpdateViewModel() {
    override val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _banchans: MutableStateFlow<List<RecentViewedItemModel>> =
        MutableStateFlow(emptyList())
    val banchans = _banchans.asStateFlow()

    val recentPaging = fetchRecentViewedPagingUseCase()
        .flowOn(Dispatchers.Default)
        .filterIsInstance<DomainEvent.Success<PagingData<RecentViewedItemModel>>>()
        .map { it.data }
        .onEach { _dataLoading.value = false }
        .cachedIn(viewModelScope)

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        fetchRecentViewedBanchans()
    }

    private fun fetchRecentViewedBanchans() {
        _dataLoading.value = true
        refreshJob()
        prevJob = viewModelScope.launch {
            fetchRecentViewedPagingUseCase()
                .flowOn(Dispatchers.Default)
                .filterIsInstance<DomainEvent.Failure<PagingData<RecentViewedItemModel>>>()
                .collect {
                    it.throwable.printStackTrace()
                    it.throwable.message?.let { message ->
                        _eventFlow.emit(UiEvent.ShowToast(message))
                        showErrorView(message, "재시도") {
                            fetchRecentViewedBanchans()
                        }
                    }
                }
        }
    }

    val clickInsertCartButton: (RecentViewedItemModel, Boolean) -> (Unit) = { banchan, isCartItem ->
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

    val itemClickListener: (String, String) -> Unit = { hash, title ->
        viewModelScope.launch {
            val banchan = BanchanModel.empty().copy(hash, title)
            _eventFlow.emit(UiEvent.ShowDetailView(banchan))
        }
    }

    override val insertCartResultEvent: (BaseBanchan, Boolean) -> Unit
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
                _eventFlow.emit(
                    UiEvent.ShowDialog(
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

    fun showEmptyView(){
        showErrorView("최근 본 목록이 없습니다", null) {}
    }
    fun hideEmptyView(){
        hideErrorView()
    }


    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowDialog(val dialogBuilder: DialogUtil.DialogCustomBuilder) : UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet) : UiEvent()
        object ShowCartView : UiEvent()
        data class ShowDetailView(val banchan: BaseBanchan) : UiEvent()
    }
}