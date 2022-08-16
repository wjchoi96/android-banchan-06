package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.data.datasource.CartDataSource
import com.woowahan.domain.extension.priceStrToLong
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.CartModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDataSource: CartDataSource,
    private val banchanDetailDataSource: BanchanDetailDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
) : CartRepository {

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

    override suspend fun updateCartItemCount(hash: String, count: Int): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.updateCartItemCount(hash, count) != 0
            }
        }
    }

    override suspend fun updateCartItemSelect(hash: String, isSelect: Boolean): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.updateCartItemSelect(hash, isSelect) != 0
            }
        }
    }

    override suspend fun fetchCartItemsKey(): Result<Set<String>> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.fetchCartItems().groupBy { it.hash }.keys
            }
        }
    }

    override suspend fun fetchCartItems(): Flow<Result<List<CartModel>>> {
        return cartDataSource.fetchCartItemsFlow()
            .flowOn(coroutineDispatcher)
            .map { list ->
                kotlin.runCatching {
                    coroutineScope {
                        val detailMap = list.map {
                            async {
                                println("fetchCartItems async run => ${it.hash}")
                                banchanDetailDataSource.fetchBanchanDetail(it.hash)
                            }
                        }.awaitAll().associateBy { item -> item.hash }
                        println("fetchCartItems async list finish")

                        val res = list.map {
                            val detail = detailMap[it.hash]!!

                            CartModel(
                                it.hash,
                                it.count,
                                CartModel.ViewType.Content,
                                it.title,
                                detail.data.thumbImages.first(),
                                detail.data.prices.last().priceStrToLong()
                            )
                        }
                        println("fetchCartItems res => $res")
                        res
                    }
                }
            }
    }

}