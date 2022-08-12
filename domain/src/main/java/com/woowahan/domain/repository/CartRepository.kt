package com.woowahan.domain.repository

import com.woowahan.domain.model.BanchanModel

interface CartRepository {
    suspend fun insertCartItem(banchan: BanchanModel, count: Int): Result<Boolean>

    suspend fun removeCartItem(hash: String): Result<Boolean>

    suspend fun removeCartItems(hashes: List<String>): Result<Boolean>

    suspend fun updateCartItem(hash: String, count: Int): Result<Boolean>

    suspend fun fetchCartItems(): Result<Map<String, Pair<BanchanModel, Int>>>
}