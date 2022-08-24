package com.woowahan.banchan.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanDetailModel
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.usecase.banchan.FetchBanchanDetailUseCase
import com.woowahan.domain.usecase.recentviewed.InsertRecentViewedItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val fetchBanchanDetailUseCase: FetchBanchanDetailUseCase,
    private val insertRecentViewedItemUseCase: InsertRecentViewedItemUseCase
) : ViewModel() {
    private var hash = ""
    private var title = ""
    val quantity = ObservableField(1)

    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _refreshDataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshDataLoading = _refreshDataLoading.asStateFlow()

    private val _detail: MutableStateFlow<BanchanDetailModel> =
        MutableStateFlow(BanchanDetailModel.empty())
    val detail = _detail.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    fun insertRecentViewedItem(banchan: BanchanModel) {
        viewModelScope.launch {
            insertRecentViewedItemUseCase(banchan = banchan, Calendar.getInstance().time)
                .flowOn(Dispatchers.IO)
                .collect()
        }
    }

    fun fetchBanchanDetail() {
        if (_dataLoading.value) {
            _refreshDataLoading.value = false
            return
        }
        viewModelScope.launch {
            _dataLoading.value = true
            fetchBanchanDetailUseCase(hash, title)
                .flowOn(Dispatchers.Default)
                .collect { res ->
                    res.onSuccess {
                        _detail.value = it
                    }.onFailure {
                        it.printStackTrace()
                        it.message?.let { message ->
                            _eventFlow.emit(UiEvent.ShowToast(message))
                        }
                    }.also {
                        _dataLoading.value = false
                        if (_refreshDataLoading.value)
                            _refreshDataLoading.value = false
                    }
                }
        }
    }

    val minusClicked: () -> Unit = {
        val currentQuantity = quantity.get()
        currentQuantity?.let {
            if (currentQuantity != 1) {
                quantity.set(currentQuantity - 1)
            }
        }
    }

    val plusClicked: () -> Unit = {
        val currentQuantity = quantity.get()
        currentQuantity?.let {
            quantity.set(currentQuantity + 1)
        }
    }

    fun initDate(hash: String?, title: String?) {
        this.hash = hash ?: ""
        this.title = title ?: ""
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowDialog(val dialogBuilder: DialogUtil.DialogCustomBuilder) : UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet) : UiEvent()
    }
}