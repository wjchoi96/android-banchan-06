package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.usecase.FetchMainDishBanchanUseCase
import com.woowahan.domain.usecase.FilterBanchanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainDishBanchanViewModel @Inject constructor(
    private val fetchMainDishBanchanUseCase: FetchMainDishBanchanUseCase,
    private val filterBanchanUseCase: FilterBanchanUseCase
) : ViewModel() {
    private val _dataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _refreshDataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val refreshDataLoading: LiveData<Boolean> = _refreshDataLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private lateinit var defaultBanchans: List<BanchanModel>
    private val _banchans: MutableLiveData<List<BanchanModel>> = MutableLiveData()

    private val _gridViewMode: MutableLiveData<Boolean> = MutableLiveData()
    val gridViewMode: LiveData<Boolean> = _gridViewMode

    val banchans: LiveData<List<BanchanModel>> = _banchans

    fun fetchMainDishBanchans() {
        if (_dataLoading.value == true) {
            _refreshDataLoading.value = false
            return
        }
        viewModelScope.launch {
            _dataLoading.value = true
            fetchMainDishBanchanUseCase.invoke()
                .onSuccess {
                    defaultBanchans = it
                    _banchans.value = defaultBanchans
                }.onFailure {
                    it.printStackTrace()
                    _errorMessage.value = it.message
                }.also {
                    _dataLoading.value = false
                    if (_refreshDataLoading.value == true)
                        _refreshDataLoading.value = false
                }
        }
    }

    private fun filterBanchan(filterType: BanchanModel.FilterType) {
        if (filterType ==
            BanchanModel.FilterType.Default
        ) {
            _banchans.value = defaultBanchans
        } else {
            filterBanchanUseCase(defaultBanchans, filterType)
                .onSuccess {
                    _banchans.value = it
                }
                .onFailure {
                    it.printStackTrace()
                    _errorMessage.value = it.message
                }
        }
    }

    val viewModeToggleEvent: (Boolean)->(Unit) = {
        _gridViewMode.value = it
    }

    fun filterItemSelect(position: Int){
        when (position) {
            BanchanModel.FilterType.Default.value -> {
                filterBanchan(BanchanModel.FilterType.Default)
            }
            BanchanModel.FilterType.PriceHigher.value -> {
                filterBanchan(BanchanModel.FilterType.PriceHigher)
            }
            BanchanModel.FilterType.PriceLower.value -> {
                filterBanchan(BanchanModel.FilterType.PriceLower)
            }
            else -> {
                filterBanchan(BanchanModel.FilterType.SalePercentHigher)
            }
        }
    }

    fun onRefresh() {
        _refreshDataLoading.value = true
        fetchMainDishBanchans()
    }

}