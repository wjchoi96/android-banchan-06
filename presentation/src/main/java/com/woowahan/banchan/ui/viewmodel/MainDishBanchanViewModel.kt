package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.banchan.util.FilterBanchanListUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.usecase.FetchMainDishBanchanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainDishBanchanViewModel @Inject constructor(
    private val fetchMainDishBanchanUseCase: FetchMainDishBanchanUseCase
) : ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _refreshDataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshDataLoading = _refreshDataLoading.asStateFlow()

    private val _gridViewMode: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val gridViewMode = _gridViewMode.asStateFlow()

    private val _banchans: MutableStateFlow<List<BanchanModel>> = MutableStateFlow(emptyList())
    val banchans = _banchans.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UIEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _showCartBottomSheet: MutableLiveData<CartItemInsertBottomSheet> = MutableLiveData()
    val showCartBottomSheet: LiveData<CartItemInsertBottomSheet> = _showCartBottomSheet


    private lateinit var defaultBanchans: List<BanchanModel>

    fun fetchMainDishBanchans() {
        if (_dataLoading.value) {
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
                    it.message?.let { message ->
                        _eventFlow.emit(UIEvent.ShowToast(message))
                    }
                }.also {
                    _dataLoading.value = false
                    if (_refreshDataLoading.value)
                        _refreshDataLoading.value = false
                }
        }
    }

    private fun filterBanchan(filterType: BanchanModel.FilterType) {
        viewModelScope.launch {
            if (filterType ==
                BanchanModel.FilterType.Default
            ) {
                _banchans.value = defaultBanchans
            } else {
                kotlin.runCatching {
                    _banchans.value = FilterBanchanListUtil.filter(defaultBanchans, filterType)
                }.onFailure {
                    it.printStackTrace()
                    it.message?.let { message ->
                        _eventFlow.emit(UIEvent.ShowToast(message))
                    }
                }
            }
        }
    }

    val viewModeToggleEvent: (Boolean) -> (Unit) = {
        _gridViewMode.value = it
    }

    val clickInsertCartButton: (BanchanModel)->(Unit) = {
        val dialog = CartItemInsertBottomSheet(it){ item, count ->
            insertItemsToCart(item, count)
        }
        _showCartBottomSheet.value = dialog
    }
    private fun insertItemsToCart(banchanModel: BanchanModel, count: Int){

    }


    val filterItemSelect: (Int) -> Unit = {
        when (it) {
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

    sealed class UIEvent {
        data class ShowToast(val message: String) : UIEvent()
        data class ShowSnackBar(val message: String) : UIEvent()
    }
}