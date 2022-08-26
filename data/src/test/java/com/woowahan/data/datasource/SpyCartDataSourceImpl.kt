package com.woowahan.data.datasource

import com.woowahan.data.entity.dto.CartEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SpyCartDataSourceImpl(
    private val carts: MutableList<CartEntity>
): CartDataSource {

    var insertCartItemCallCount = 0
        private set

    override fun getCartSizeFlow(): Flow<Int> = flow {
        emit(carts.size)
    }

    override suspend fun insertCartItem(hash: String, title: String, count: Int) {
        insertCartItemCallCount++
    }

    override suspend fun removeCartItem(vararg hashes: String): Flow<Int> = flow {
        emit(1)
    }

    override suspend fun updateCartItemCount(hash: String, count: Int): Flow<Int> = flow {
        emit(1)
    }

    override suspend fun updateCartItemSelect(isSelect: Boolean, vararg hashes: String): Flow<Int> = flow {
        emit(1)
    }

    override suspend fun fetchCartItems(): Flow<List<CartEntity>> = flow {
        emit(carts)
    }
}