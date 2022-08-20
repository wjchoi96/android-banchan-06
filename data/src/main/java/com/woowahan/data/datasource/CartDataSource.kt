package com.woowahan.data.datasource

import com.woowahan.data.entity.dto.CartEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow

interface CartDataSource {
    fun getCartSizeFlow(): Flow<Int>

    suspend fun insertCartItem(hash: String, title: String, count: Int)

    suspend fun removeCartItem(vararg hashes: String): Flow<Int>

    suspend fun updateCartItemCount(hash: String, count: Int): Flow<Int>

    suspend fun updateCartItemSelect(isSelect: Boolean, vararg hashes: String): Flow<Int>

    suspend fun fetchCartItems(): Flow<List<CartEntity>>
}