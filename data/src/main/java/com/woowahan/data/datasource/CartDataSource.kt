package com.woowahan.data.datasource

import com.woowahan.data.entity.dto.CartEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow

interface CartDataSource {
    fun getCartSizeFlow(): Flow<Int>

    suspend fun insertCartItem(hash: String, title: String, count: Int)

    suspend fun removeCartItem(vararg hashes: String): Int

    suspend fun updateCartItemCount(hash: String, count: Int): Int

    suspend fun updateCartItemSelect(isSelect: Boolean, vararg hashes: String): Int

    suspend fun fetchCartItems(): List<CartEntity>

    suspend fun fetchCartItemsFlow(): Flow<List<CartEntity>>
}