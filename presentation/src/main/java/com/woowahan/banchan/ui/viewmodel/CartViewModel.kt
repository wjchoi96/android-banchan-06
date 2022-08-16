package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.ui.dialog.CartItemInsertBottomSheet
import com.woowahan.domain.model.CartListModel
import com.woowahan.domain.model.CartModel
import com.woowahan.domain.usecase.FetchCartItemsUseCase
import com.woowahan.domain.usecase.RemoveCartItemUseCase
import com.woowahan.domain.usecase.RemoveCartItemsUseCase
import com.woowahan.domain.usecase.UpdateCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val fetchCartItemsUseCase: FetchCartItemsUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val removeCartItemsUseCase: RemoveCartItemsUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
) : ViewModel() {
    private val _dataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dataLoading = _dataLoading.asStateFlow()

    private val _refreshDataLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshDataLoading = _refreshDataLoading.asStateFlow()

    private val _cartItems: MutableStateFlow<List<CartListModel>> = MutableStateFlow(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _eventFlow: MutableSharedFlow<UiEvent> =
        MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val selectedItems = HashMap<String, CartModel>()

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

    fun removeCartItems(items: List<CartModel>) {
        viewModelScope.launch {
            _dataLoading.value = true
            removeCartItemsUseCase(items.map { it.hash })
                .onSuccess { isSuccess ->
                    if (isSuccess) {
                        selectedItems.clear()
                    } else {
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

    fun removeCartItem(hash: String) {
        viewModelScope.launch {
            _dataLoading.value = true
            removeCartItemUseCase(hash)
                .onSuccess { isSuccess ->
                    if (isSuccess) {
                        selectedItems.remove(hash)
                    } else {
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

    fun updateCartItem(hash: String, count: Int) {
        viewModelScope.launch {
            _dataLoading.value = true
            updateCartItemUseCase(hash, count)
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

    val selectAllItems: (Boolean) -> Unit = { isSelected ->
        if (isSelected) {
            Timber.d("All selected")
            _cartItems.value.filterIsInstance<CartListModel.Content>().forEach { cartContent ->
                selectedItems[cartContent.cart.hash] = cartContent.cart
            }
        } else {
            Timber.d("All unselected")
            selectedItems.clear()
        }
    }

    val deleteAllSelectedItems: () -> Unit = {
        removeCartItems(selectedItems.values.toList())
    }

    val selectItem: (CartModel) -> Unit = { cartModel ->
        _cartItems.value.filterIsInstance<CartListModel.Content>().map {
            if (it.cart.isSameHash(cartModel)) {
                Timber.d("${cartModel.hash} ${cartModel.isSelected}")
                CartListModel.Content(it.cart.copy(isSelected = cartModel.isSelected))
            } else {
                it
            }
        }
    }

    val deleteItem: (CartModel) -> Unit = { deleteItem ->
        removeCartItem(deleteItem.hash)
    }

    val minusClicked: (CartModel) -> Unit = { minusItem ->
        if (minusItem.count != 1) {
            updateCartItem(minusItem.hash, minusItem.count - 1)
        }
    }

    val plusClicked: (CartModel) -> Unit = { plusItem ->
        updateCartItem(plusItem.hash, plusItem.count + 1)
    }

    val orderItems: (List<CartModel>) -> Unit = { orderItems ->

    }

    fun onRefresh() {
        _refreshDataLoading.value = true
        fetchCartItems()
        viewModelScope
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackBar(val message: String) : UiEvent()
        data class ShowCartBottomSheet(val bottomSheet: CartItemInsertBottomSheet) : UiEvent()
    }
}