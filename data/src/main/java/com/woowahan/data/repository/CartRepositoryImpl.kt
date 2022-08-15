package com.woowahan.data.repository

import com.woowahan.data.datasource.CartDataSource
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDataSource: CartDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
): CartRepository {

    override fun getCartSizeFlow(): Flow<Int> {
        return cartDataSource.getCartSizeFlow()
    }

    override suspend fun insertCartItem(
        banchan: BanchanModel,
        count: Int
    ): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.insertCartItem(banchan, count)
                true
            }
        }
    }

    override suspend fun removeCartItem(hash: String): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.removeCartItem(hash) != 0
            }
        }
    }

    override suspend fun removeCartItems(hashes: List<String>): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.removeCartItems(hashes) != 0
            }
        }
    }

    override suspend fun updateCartItem(hash: String, count: Int): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.updateCartItem(hash, count) != 0
            }
        }
    }

    override suspend fun fetchCartItems(): Result<Set<String>> {
        return withContext(coroutineDispatcher){
            kotlin.runCatching {
                cartDataSource.fetchCartItems().groupBy { it.hash }.keys
            }
        }
    }
}