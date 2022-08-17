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

    override suspend fun removeCartItem(vararg hashes: String): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.removeCartItem(*hashes) != 0
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

    override suspend fun updateCartItemSelect(
        isSelect: Boolean,
        vararg hashes: String,
    ): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.updateCartItemSelect(isSelect, *hashes) != 0
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
                                hash = it.hash,
                                count = it.count,
                                title = it.title,
                                imageUrl = detail.data.thumbImages.first(),
                                price = detail.data.prices.last().priceStrToLong(),
                                isSelected = it.isSelect
                            )
                        }
                        println("fetchCartItems res => $res")
                        res
                    }
                }
            }
    }

}