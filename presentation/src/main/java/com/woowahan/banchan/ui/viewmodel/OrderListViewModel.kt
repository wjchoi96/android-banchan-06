package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.model.OrderModel
import com.woowahan.domain.usecase.order.FetchOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val fetchOrdersUseCase: FetchOrdersUseCase
): ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _orders: MutableStateFlow<List<OrderModel>> = MutableStateFlow(emptyList())
    val orders = _orders.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        fetchOrders()
    }

    private fun fetchOrders(){
        viewModelScope.launch {
            _dataLoading.value = true
            fetchOrdersUseCase()
                .flowOn(Dispatchers.Default)
                .collect { event ->
                    event.onSuccess {
                        _orders.value = it
                    }.onFailure {
                        it.printStackTrace()
                        it.message?.let { message -> _eventFlow.emit(UiEvent.ShowToast(message)) }
                    }.also {
                        _dataLoading.value = false
                    }
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