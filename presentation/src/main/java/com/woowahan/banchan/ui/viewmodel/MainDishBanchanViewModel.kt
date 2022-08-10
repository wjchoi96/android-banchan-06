package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.usecase.FetchMainDishBanchanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainDishBanchanViewModel @Inject constructor(
    private val fetchMainDishBanchanUseCase: FetchMainDishBanchanUseCase
) : ViewModel() {
    private val _dataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _refreshDataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val refreshDataLoading: LiveData<Boolean> = _refreshDataLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _banchans: MutableLiveData<List<BanchanModel>> = MutableLiveData()
    val banchans: LiveData<List<BanchanModel>> = _banchans

    fun fetchMainDishBanchans(){
        if(_dataLoading.value == true) {
            _refreshDataLoading.value = false
            return
        }
        viewModelScope.launch {
            _dataLoading.value = true
            fetchMainDishBanchanUseCase.invoke()
                .onSuccess {
                    _banchans.value = it
                }.onFailure {
                    it.printStackTrace()
                    _errorMessage.value = it.message
                }.also {
                    _dataLoading.value = false
                    if(_refreshDataLoading.value == true)
                        _refreshDataLoading.value = false
                }
        }
    }

    fun onRefresh(){
        _refreshDataLoading.value = true
        fetchMainDishBanchans()
    }

}