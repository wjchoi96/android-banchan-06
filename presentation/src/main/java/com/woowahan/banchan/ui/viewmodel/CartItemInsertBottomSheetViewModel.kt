package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.model.BaseBanchan
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CartItemInsertBottomSheetViewModel: ViewModel() {

    var banchan: BaseBanchan? = null
        set(value) {
            field = value
            _cartCost.value = when(banchan?.salePrice){
                0L -> banchan!!.price
                null -> 0L
                else -> banchan!!.salePrice
            }
        }
    var insertListener: ((BaseBanchan, Int) -> (Unit))? = null

    private var _itemCount: MutableStateFlow<Int> = MutableStateFlow(1)
    val itemCount = _itemCount.asStateFlow()

    private val _cartCost: MutableStateFlow<Long> = MutableStateFlow(
        when(banchan?.salePrice){
            0L -> banchan!!.price
            null -> 0L
            else -> banchan!!.salePrice
        }
    )
    var cartCost = _cartCost.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    val countUpListener: (Int) -> (Unit) = {
        Timber.d("count up event[$it]")
        _itemCount.value = it+1
        setCartCost(it+1)
    }

    val countDownListener: (Int) -> (Unit) = {
        Timber.d("count down event[$it]")
        if(_itemCount.value != 1) {
            _itemCount.value = it - 1
            setCartCost(it-1)
        }
    }

    val insertButtonClick: (BaseBanchan, Int) -> (Unit) = { banchan, count ->
        insertListener?.invoke(banchan, count)
        viewModelScope.launch { _eventFlow.emit(UiEvent.Dismiss) }
    }

    val cancelListener: ()->(Unit) = {
        viewModelScope.launch { _eventFlow.emit(UiEvent.Dismiss) }
    }

    private fun setCartCost(count: Int){
        banchan?.let {
            _cartCost.value = when(it.salePrice){
                0L -> it.price * count
                else -> it.salePrice * count
            }
        }
    }

    sealed class UiEvent {
        object Dismiss: UiEvent()
    }
}