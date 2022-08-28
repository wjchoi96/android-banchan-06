package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainDialogViewModel: ViewModel() {

    private val _message: MutableStateFlow<String> = MutableStateFlow("")
    val message = _message.asStateFlow()

    private val _positiveBtn: MutableStateFlow<String> = MutableStateFlow("")
    val positiveBtn = _positiveBtn.asStateFlow()

    private val _negativeBtn: MutableStateFlow<String> = MutableStateFlow("")
    val negativeBtn = _negativeBtn.asStateFlow()

    private val _positiveVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val positiveVisible = _positiveVisible.asStateFlow()

    private val _negativeVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val negativeVisible = _negativeVisible.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    var positiveEvent: (()->Unit)? = null
    var negativeEvent: (()->Unit)? = null

    val bindingPositiveBtnEvent: ()->Unit = {
        positiveEvent?.invoke()
        viewModelScope.launch { _eventFlow.emit(UiEvent.Dismiss) }
    }

    val bindingNegativeBtnEvent: ()->Unit = {
        negativeEvent?.invoke()
        viewModelScope.launch { _eventFlow.emit(UiEvent.Dismiss) }
    }

    fun setMessage(message: String){
        _message.value = message
    }

    fun setPositiveBtn(positive: Pair<String, (()->Unit)?>){
        _positiveVisible.value = true
        _positiveBtn.value = positive.first
        positive.second?.let { positiveEvent = it }
    }

    fun setNegativeBtn(negative: Pair<String, (()->Unit)?>){
        _negativeVisible.value = true
        _negativeBtn.value = negative.first
        negative.second?.let { negativeEvent = it }
    }

    sealed class UiEvent {
        object Dismiss: UiEvent()
    }

}