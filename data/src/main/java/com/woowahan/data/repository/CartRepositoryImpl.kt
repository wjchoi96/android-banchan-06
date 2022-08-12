package com.woowahan.data.repository

import com.woowahan.data.datasource.CartDataSource
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDataSource: CartDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
): CartRepository {

    override suspend fun insertCartItem(
        banchan: BanchanModel,
        count: Int
    ): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.insertCartItem(banchan, count) != null
            }
        }
    }

    override suspend fun removeCartItem(hash: String): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.removeCartItem(hash) != null
            }
        }
    }

    override suspend fun removeCartItems(hashes: List<String>): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                var res = true
                cartDataSource.removeCartItems(hashes).forEach {
                    if(it == null) res = false
                }
                res
            }
        }
    }

    override suspend fun updateCartItem(hash: String, count: Int): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.updateCartItem(hash, count) != null
            }
        }
    }

    override suspend fun fetchCartItems(): Result<Map<String, Pair<BanchanModel, Int>>> {
        return withContext(coroutineDispatcher){
            kotlin.runCatching {
                cartDataSource.fetchCartItems()
            }
        }
    }
}