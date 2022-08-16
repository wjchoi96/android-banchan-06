package com.woowahan.data.datasource

import com.woowahan.data.entity.dto.CartEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow

interface CartDataSource {
    fun getCartSizeFlow(): Flow<Int>

    suspend fun insertCartItem(banchan: BanchanModel, count: Int)

    suspend fun removeCartItem(hash: String): Int

    suspend fun removeCartItems(hashes: List<String>): Int

    suspend fun updateCartItemCount(hash: String, count: Int): Int

    suspend fun fetchCartItems(): List<CartEntity>

    suspend fun fetchCartItemsFlow(): Flow<List<CartEntity>>
}