package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.OrderModel
import com.woowahan.domain.usecase.order.FetchOrderPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val fetchOrderPagingUseCase: FetchOrderPagingUseCase
): ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    val orderPaging = fetchOrderPagingUseCase()
        .flowOn(Dispatchers.Default)
        .filterIsInstance<DomainEvent.Success<PagingData<OrderModel>>>()
        .map { it.data }
        .onEach { _dataLoading.value = false }
        .cachedIn(viewModelScope)

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        _dataLoading.value = true

        viewModelScope.launch {
            fetchOrderPagingUseCase()
                .flowOn(Dispatchers.Default)
                .filterIsInstance<DomainEvent.Failure<PagingData<OrderModel>>>()
                .collect {
                    it.throwable.printStackTrace()
                    it.throwable.message?.let { message -> _eventFlow.emit(UiEvent.ShowToast(message)) }
                }
        }
    }

    val orderDetailNavigateEvent: (OrderModel) -> Unit = {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.NavigateOrderItemView(it.orderId))
        }
    }


    sealed class UiEvent {
        data class ShowToast(val message: String): UiEvent()
        data class ShowSnackBar(val message: String): UiEvent()
        data class NavigateOrderItemView(val orderId: Long): UiEvent()
    }
}