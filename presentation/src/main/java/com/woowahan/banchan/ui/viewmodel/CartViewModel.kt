package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.domain.model.CartListItemModel
import com.woowahan.domain.model.CartModel
import com.woowahan.domain.usecase.cart.FetchCartItemsUseCase
import com.woowahan.domain.usecase.cart.RemoveCartItemUseCase
import com.woowahan.domain.usecase.cart.UpdateCartItemCountUseCase
import com.woowahan.domain.usecase.cart.UpdateCartItemSelectUseCase
import com.woowahan.domain.usecase.order.InsertOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val fetchCartItemsUseCase: FetchCartItemsUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val updateCartItemCountUseCase: UpdateCartItemCountUseCase,
    private val updateCartItemSelectUseCase: UpdateCartItemSelectUseCase,
    private val insertOrderUseCase: InsertOrderUseCase
) : ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _refreshDataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshDataLoading = _refreshDataLoading.asStateFlow()

    private val _cartItems: MutableStateFlow<List<CartListItemModel>> = MutableStateFlow(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> =
        MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    fun fetchCartItems() {
        if (_dataLoading.value) {
            _refreshDataLoading.value = false
            return
        }
        viewModelScope.launch {
            _dataLoading.value = true
            fetchCartItemsUseCase().collect {
                it.onSuccess {
                    _cartItems.value = it
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

    private fun removeCartItems(items: List<CartListItemModel.Content>) {
        viewModelScope.launch {
            _dataLoading.value = true
            removeCartItemUseCase(*(items.map { it.cart.hash }).toTypedArray())
                .onSuccess { isSuccess ->
                    if (!isSuccess) {
                        _eventFlow.emit(UiEvent.ShowToast("Can't delete items"))
                    }
                }
                .onFailure {
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

    private suspend inline fun clearCart(items: List<CartModel>, successEvent: (Boolean)->Unit) {
        _dataLoading.value = true
        removeCartItemUseCase(*(items.map { it.hash }).toTypedArray())
            .onSuccess { isSuccess ->
                successEvent(isSuccess)
            }
            .onFailure {
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

    private fun removeCartItem(hash: String) {
        viewModelScope.launch {
            _dataLoading.value = true
            removeCartItemUseCase(hash)
                .onSuccess { isSuccess ->
                    if (!isSuccess) {
                        _eventFlow.emit(UiEvent.ShowToast("Can't delete item"))
                    }
                }
                .onFailure {
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

    private fun updateCartItemCount(hash: String, count: Int) {
        viewModelScope.launch {
            _dataLoading.value = true
            updateCartItemCountUseCase(hash, count)
                .onSuccess { isSuccess ->
                }
                .onFailure {
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

    private fun updateCartItemAllSelect(isSelect: Boolean) {
        viewModelScope.launch {
            _dataLoading.value = true
            updateCartItemSelectUseCase(
                isSelect,
                *(_cartItems.value.filterIsInstance<CartListItemModel.Content>().map { it.cart.hash }).toTypedArray()
            )
                .onSuccess { isSuccess ->
                    if (!isSuccess) {
                        _eventFlow.emit(UiEvent.ShowToast("Can't select all"))
                    }
                }
                .onFailure {
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

    private fun updateCartItemSelect(cartModel: CartModel, isSelect: Boolean) {
        viewModelScope.launch {
            _dataLoading.value = true
            updateCartItemSelectUseCase(isSelect, cartModel.hash)
                .onSuccess { isSuccess ->
                    if (!isSuccess) {
                        _eventFlow.emit(UiEvent.ShowToast("Can't select all"))
                    }
                }
                .onFailure {
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

    val selectAllItems: (Boolean) -> Unit = { isSelected ->
        updateCartItemAllSelect(isSelected)
    }

    val deleteAllSelectedItems: () -> Unit = {
        removeCartItems(
            _cartItems.value.filterIsInstance<CartListItemModel.Content>()
                .filter { it.cart.isSelected })
    }

    val selectItem: (CartModel, Boolean) -> Unit = { cartModel, isSelected ->
        updateCartItemSelect(cartModel, isSelected)
    }

    val deleteItem: (CartModel) -> Unit = { deleteItem ->
        removeCartItem(deleteItem.hash)
    }

    val minusClicked: (CartModel) -> Unit = { minusItem ->
        if (minusItem.count != 1) {
            updateCartItemCount(minusItem.hash, minusItem.count - 1)
        }
    }

    val plusClicked: (CartModel) -> Unit = { plusItem ->
        updateCartItemCount(plusItem.hash, plusItem.count + 1)
    }

    val orderItems: () -> Unit = {
        val orderItems = _cartItems.value
            .filterIsInstance<CartListItemModel.Content>()
            .filter { it.cart.isSelected }
            .map { it.cart }
        viewModelScope.launch {
            _dataLoading.value = true
            insertOrderUseCase(
                time = Calendar.getInstance().time,
                items = orderItems
            ).flowOn(Dispatchers.Default)
                .collect { event ->
                    event.onSuccess {
                        clearCart(orderItems){
                            when(it){
                                true -> _eventFlow.emit(UiEvent.GoToOrderList)
                                else -> _eventFlow.emit(UiEvent.ShowToast("Can't order"))
                            }
                        }
                    }.onFailure {
                        it.message?.let {
                            _eventFlow.emit(UiEvent.ShowToast(it))
                        }
                    }.also {
                        _dataLoading.value = false
                        if (_refreshDataLoading.value)
                            _refreshDataLoading.value = false
                    }
                }
        }
    }

    fun onRefresh() {
        _refreshDataLoading.value = true
        fetchCartItems()
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet) : UiEvent()
        object GoToOrderList : UiEvent()
    }
}