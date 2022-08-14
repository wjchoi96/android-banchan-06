package com.woowahan.data.datasource

import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartDataSourceImpl @Inject constructor(): CartDataSource {
    // key => banchan hash
    // value => BanchanModel[품목], Int[개수]
    private val cart = mutableMapOf<String, Pair<BanchanModel, Int>>()
    private val cartItemCountFlow: MutableStateFlow<Int> = MutableStateFlow(cart.size)

    override fun getCartSizeFlow(): Flow<Int> = cartItemCountFlow.asStateFlow()

    // 단순 추가 -> 추가된 항목 리턴
    override suspend fun insertCartItem(banchan: BanchanModel, count: Int): Pair<BanchanModel, Int>? {
        cart[banchan.hash] = banchan to count
        cartItemCountFlow.emit(cart.size)
        return cart[banchan.hash]
    }

    // 단순 제거 -> 제거된 Item 리턴
    override suspend fun removeCartItem(hash: String): BanchanModel? {
        cart.remove(hash).let {
            cartItemCountFlow.emit(cart.size)
            return it?.first
        }
    }

    // 목록 제거 -> 제거된 Items 리턴
    override suspend fun removeCartItems(hashes: List<String>): List<BanchanModel?> {
        val removeItems = mutableListOf<BanchanModel?>()
        hashes.forEach {
            cart.remove(it).let { removed ->
                removeItems.add(removed?.first)
            }
        }
        cartItemCountFlow.emit(cart.size)
        return removeItems
    }

    // 항목 개수 업데이트 -> 이때 기존에 없는 항목을 업데이트 시도한다면 null 을 리턴받을것
    override suspend fun updateCartItem(hash: String, count: Int): Pair<BanchanModel, Int>? {
        cart[hash]?.let {
            cart[hash] = it.first to count
        }
        return cart[hash]
    }

    override suspend fun fetchCartItems(): Map<String, Pair<BanchanModel, Int>> {
        return cart
    }

}