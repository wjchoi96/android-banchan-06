package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.usecase.GetCartItemsSizeFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val getCartItemsSizeFlowUseCase: GetCartItemsSizeFlowUseCase
): ViewModel() {

    private val _cartItemSize: MutableStateFlow<Int> = MutableStateFlow(0)
    val cartItemSize = _cartItemSize.asStateFlow()

    init {
        viewModelScope.launch {
            getCartItemsSizeFlowUseCase().collect {
                _cartItemSize.emit(it)
            }
        }
    }

}