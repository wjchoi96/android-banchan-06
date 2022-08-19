package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.usecase.cart.GetCartItemsSizeFlowUseCase
import com.woowahan.domain.usecase.order.GetDeliveryOrderCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val getCartItemsSizeFlowUseCase: GetCartItemsSizeFlowUseCase,
    private val getDeliveryOrderCountUseCase: GetDeliveryOrderCountUseCase
): ViewModel() {

    private val _cartItemSize: MutableStateFlow<Int> = MutableStateFlow(0)
    val cartItemSize = _cartItemSize.asStateFlow()

    private val _deliveryItemSize: MutableStateFlow<Int> = MutableStateFlow(0)
    val deliveryItemSize = _deliveryItemSize.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                getCartItemsSizeFlowUseCase().collect {
                    _cartItemSize.emit(it)
                }
            }

            launch {
                getDeliveryOrderCountUseCase().collect {
                    it.onSuccess { count ->
                        _deliveryItemSize.emit(count)
                    }
                }
            }
        }
    }

}