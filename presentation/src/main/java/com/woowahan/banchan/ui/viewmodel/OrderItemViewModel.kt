package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.OrderItemTypeModel
import com.woowahan.domain.usecase.order.FetchOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OrderItemViewModel @Inject constructor(
    private val fetchOrderUseCase: FetchOrderUseCase
): ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _orderItem = fetchOrder()
    val orderItem: SharedFlow<List<OrderItemTypeModel>>

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _refreshEvent: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)

    private var orderId: Long? = null
    fun initData(orderId: Long){
        this.orderId = orderId
    }

    init {
        orderItem = combine(_refreshEvent, _orderItem) { _, event ->
            var orderList: List<OrderItemTypeModel> = emptyList()
            event.onSuccess {
                orderList = it.toMutableList().apply {
                    this[0] = (this[0] as OrderItemTypeModel.Header).copy(currentDate =  Calendar.getInstance().time)
                }
            }.onFailureWithData{ it, data ->
                it.printStackTrace()
                it.message?.let { _eventFlow.emit(UiEvent.ShowToast(it)) }
                if(data != null)
                    orderList = data
                else
                    _eventFlow.emit(UiEvent.FinishView(it.message))
            }.also {
                _dataLoading.value = false
            }
            return@combine orderList
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        viewModelScope.launch { _refreshEvent.emit(true) }
    }

    private fun fetchOrder(): Flow<DomainEvent<List<OrderItemTypeModel>>> = flow {
        if(orderId == -1L){
            _eventFlow.emit(UiEvent.FinishView("잘못된 주문번호입니다"))
            return@flow
        }
        _dataLoading.value = true
        fetchOrderUseCase(orderId!!)
            .flowOn(Dispatchers.Default)
            .collect {
                emit(it)
            }
    }

    fun refreshEvent(){
        viewModelScope.launch {
            _refreshEvent.emit(true)
        }
    }

    sealed class UiEvent {
        data class FinishView(val message: String? = null): UiEvent()
        data class ShowToast(val message: String): UiEvent()
        data class ShowSnackBar(val message: String): UiEvent()
    }
}