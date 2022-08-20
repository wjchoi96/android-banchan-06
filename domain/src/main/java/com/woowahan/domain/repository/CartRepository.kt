package com.woowahan.domain.repository

import com.woowahan.domain.model.BaseBanchan
import com.woowahan.domain.model.CartModel
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartSizeFlow(): Flow<Int>

    suspend fun insertCartItem(banchan: BaseBanchan, count: Int): Flow<Boolean>

    suspend fun removeCartItem(vararg hashes: String): Flow<Boolean>

    suspend fun updateCartItemCount(hash: String, count: Int): Flow<Boolean>

    suspend fun updateCartItemSelect(isSelect: Boolean, vararg hashes: String): Flow<Boolean>

    suspend fun fetchCartItemsKey(): Flow<Set<String>>

    suspend fun fetchCartItems(): Flow<List<CartModel>>
}