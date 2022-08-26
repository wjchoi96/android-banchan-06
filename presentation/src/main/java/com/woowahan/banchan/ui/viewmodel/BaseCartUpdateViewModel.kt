package com.woowahan.banchan.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BaseBanchan
import com.woowahan.domain.usecase.cart.InsertCartItemUseCase
import com.woowahan.domain.usecase.cart.RemoveCartItemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


abstract class BaseCartUpdateViewModel: BaseErrorViewModel() {

    protected abstract val removeCartItemUseCase: RemoveCartItemUseCase
    protected abstract val insertCartItemUseCase: InsertCartItemUseCase

    protected abstract val _dataLoading: MutableStateFlow<Boolean>

    protected abstract val insertCartResultEvent: (BaseBanchan, Boolean)->Unit
    protected abstract val insertCartThrowableEvent: (Throwable)->Unit
    protected abstract val removeCartResultEvent: (BaseBanchan, Boolean)->Unit
    protected abstract val removeCartThrowableEvent: (Throwable)->Unit


    protected fun removeItemFromCart(banchan: BaseBanchan) {
        viewModelScope.launch {
            _dataLoading.emit(true)
            removeCartItemUseCase.invoke(banchan.hash)
                .flowOn(Dispatchers.Default)
                .collect { event ->
                    event.onSuccess {
                        removeCartResultEvent(banchan, it)
                    }.onFailure {
                        removeCartThrowableEvent(it)
                    }.also {
                        _dataLoading.emit(false)
                    }
                }
        }
    }

    protected fun insertItemsToCart(banchan: BaseBanchan, count: Int) {
        viewModelScope.launch {
            _dataLoading.emit(true)
            insertCartItemUseCase.invoke(banchan.hash, banchan.title, count)
                .flowOn(Dispatchers.Default)
                .collect { event ->
                    event.onSuccess {
                        insertCartResultEvent(banchan, it)
                    }.onFailure {
                        insertCartThrowableEvent(it)
                    }.also {
                        _dataLoading.emit(false)
                    }
                }
        }
    }

    protected fun getCartItemUpdateDialog(
        content: String,
        showCartEvent: ()->Unit
    ): DialogUtil.DialogCustomBuilder{
        return DialogUtil.DialogCustomBuilder(
            content,
            "계속 쇼핑하기" to {},
            "장바구니 확인" to showCartEvent
        )
    }

    protected fun List<BanchanModel>.filterType(filterType: BanchanModel.FilterType): List<BanchanModel> {
        return listOf(
            BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
            BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
        ) + this
            .filter { it.viewType == BanchanModel.ViewType.Item }
            .sortedBy {
                when (filterType) {
                    BanchanModel.FilterType.PriceHigher -> if (it.salePercent == 0) -it.price else -it.salePrice
                    BanchanModel.FilterType.PriceLower -> if (it.salePercent == 0) it.price else it.salePrice
                    BanchanModel.FilterType.SalePercentHigher -> -it.salePercent.toLong()
                    else -> throw Throwable("Unknown filter item")
                }
            }
    }

}