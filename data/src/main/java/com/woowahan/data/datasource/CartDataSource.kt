package com.woowahan.data.datasource

import com.woowahan.domain.model.BanchanModel

interface CartDataSource {
    suspend fun insertCartItem(banchan: BanchanModel, count: Int): Pair<BanchanModel, Int>??

    suspend fun removeCartItem(hash: String): BanchanModel?

    suspend fun removeCartItems(hashes: List<String>): List<BanchanModel?>

    suspend fun updateCartItem(hash: String, count: Int): Pair<BanchanModel, Int>?
}