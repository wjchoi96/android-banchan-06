package com.woowahan.data.dao

import com.woowahan.data.entity.dto.CartDto
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.CartTableEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SpyCartDaoImpl(
    private val carts: MutableList<CartTableEntity>,
    private val banchans: MutableList<BanchanItemTableEntity>
): CartDao {

    var insertCalledCount: Int = 0
        private set

    var updateCountCalledCount: Int = 0
        private set

    var updateSelectCalledCount: Int = 0
        private set

    override fun insertCartItem(cart: CartTableEntity) {
        insertCalledCount++
    }

    override fun updateCartItemCount(hash: String, count: Int): Int {
        return ++updateCountCalledCount
    }

    override fun updateCartItemSelect(isSelect: Boolean, vararg hash: String): Int {
        return ++updateSelectCalledCount
    }

    override fun removeCartItem(vararg hash: String): Int {
        var count = 0
        hash.forEach { key ->
            val res = carts.removeIf { it.hash == key }
            if(res) count++
        }
        return count
    }

    override fun fetchCartItemsCount(): Flow<Int> = flow {
        emit(carts.size)
    }

    override fun fetchCartItems(): Flow<List<CartDto>> = flow {
        emit(carts.mapIndexed { index, cartTableEntity ->
            CartDto(cartTableEntity, banchans[index])
        })
    }
}