package com.woowahan.banchan.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.usecase.cart.GetCartItemsSizeFlowUseCase
import com.woowahan.domain.usecase.order.GetDeliveryOrderCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val getCartItemsSizeFlowUseCase: GetCartItemsSizeFlowUseCase,
    private val getDeliveryOrderCountUseCase: GetDeliveryOrderCountUseCase
) : ViewModel() {
    private var _isReady = false
    val isReady: Boolean
        get() = _isReady
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

        Handler(Looper.myLooper()!!).postDelayed({ _isReady = true }, 500)
    }

}