package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.model.OrderItemTypeModel
import com.woowahan.domain.usecase.order.FetchOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class OrderItemViewModel @Inject constructor(
    private val fetchOrderUseCase: FetchOrderUseCase
): ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _orderItem: MutableStateFlow<List<OrderItemTypeModel>> = MutableStateFlow(emptyList())
    val orderItem = _orderItem.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _refreshFlow: MutableSharedFlow<Unit> = MutableSharedFlow()

    private var orderId: Long? = null
    fun initData(orderId: Long){
        this.orderId = orderId
    }

    init {
        viewModelScope.launch {
            _refreshFlow
                .debounce(300)
                .collect {
                    Timber.d("refreshFlow on")
                    fetchOrder()
                }
        }
    }

    fun fetchOrder(){
        viewModelScope.launch {
            if(orderId == -1L){
                _eventFlow.emit(UiEvent.FinishView())
                return@launch
            }
            _dataLoading.value = true
            fetchOrderUseCase(orderId!!)
                .flowOn(Dispatchers.Default)
                .collect { event ->
                    event.onSuccess {
                        Timber.d("get order success -> $it")
                        _orderItem.value = it
                    }.onFailureWithData{ it, data ->
                        it.printStackTrace()
                        it.message?.let {
                            _eventFlow.emit(UiEvent.ShowToast(it))
                        }
                        data?.let { _orderItem.value = it }
                    }.also {
                        _dataLoading.value = false
                    }
                }
        }
    }

    fun refreshEvent(){
        viewModelScope.launch {
            _refreshFlow.emit(Unit)
        }
    }

    sealed class UiEvent {
        data class FinishView(val message: String? = null): UiEvent()
        data class ShowToast(val message: String): UiEvent()
        data class ShowSnackBar(val message: String): UiEvent()
    }
}