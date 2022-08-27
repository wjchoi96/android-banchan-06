package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.woowahan.domain.model.ThrowableUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseErrorViewModel: ViewModel() {
    private val _errorViewVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errorViewVisible = _errorViewVisible.asStateFlow()

    private val _errorViewTitle: MutableStateFlow<String> = MutableStateFlow("")
    val errorViewTitle = _errorViewTitle.asStateFlow()

    private val _errorBtnTitle: MutableStateFlow<String> = MutableStateFlow("")
    val errorBtnTitle = _errorBtnTitle.asStateFlow()

    private val _errorBtnVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errorBtnVisible = _errorBtnVisible.asStateFlow()

    private val _errorViewEvent: MutableStateFlow<()->Unit> = MutableStateFlow {}
    val errorViewEvent = _errorViewEvent.asStateFlow()

    protected var prevJob: Job? = null

    protected fun refreshJob(){
        if(prevJob != null) {
            prevJob?.cancel()
            prevJob = null
        }
    }

    protected fun hideErrorView(){
        _errorViewVisible.value = false
    }

    protected fun showErrorView(throwable: Throwable, btnType: ErrorViewButtonType?, event: ()->Unit){
        _errorViewVisible.value = true
        ThrowableUtil.throwableToMessage(throwable)?.let { _errorViewTitle.value = it }
        btnType?.title?.let { _errorBtnTitle.value = it }
        _errorBtnVisible.value = btnType != null && btnType != ErrorViewButtonType.None
        _errorViewEvent.value = event
    }

    protected fun showCustomButtonErrorView(message: String?, btnTitle: String?, event: ()->Unit){
        _errorViewVisible.value = true
        message?.let { _errorViewTitle.value = it }
        btnTitle?.let { _errorBtnTitle.value = it }
        _errorBtnVisible.value = btnTitle != null
        _errorViewEvent.value = event
    }


    enum class ErrorViewButtonType(val title: String) {
        Retry("재시도"),
        Confirm("확인"),
        Cancel("취소"),
        None("None")
    }


}