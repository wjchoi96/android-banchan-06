package com.woowahan.domain.repository

import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartSizeFlow(): Flow<Int>

    suspend fun insertCartItem(banchan: BanchanModel, count: Int): Result<Boolean>

    suspend fun removeCartItem(hash: String): Result<Boolean>

    suspend fun removeCartItems(hashes: List<String>): Result<Boolean>

    suspend fun updateCartItem(hash: String, count: Int): Result<Boolean>

    suspend fun fetchCartItemsKey(): Result<Set<String>>
}