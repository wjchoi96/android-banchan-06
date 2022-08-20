package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.usecase.cart.GetCartItemsSizeFlowUseCase
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
    private val getCartItemsSizeFlowUseCase: GetCartItemsSizeFlowUseCase
): ViewModel() {

    private val _cartItemSize: MutableStateFlow<Int> = MutableStateFlow(0)
    val cartItemSize = _cartItemSize.asStateFlow()

    init {
        viewModelScope.launch {
            getCartItemsSizeFlowUseCase()
                .flowOn(Dispatchers.Default)
                .collect { flow ->
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
    }

}