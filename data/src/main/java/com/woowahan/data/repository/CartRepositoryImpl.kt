package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.data.datasource.CartDataSource
import com.woowahan.data.entity.BanchanDetailEntity
import com.woowahan.domain.extension.priceStrToLong
import com.woowahan.domain.model.BanchanDetailModel
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BaseBanchan
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

    private val cacheMap = mutableMapOf<String, BanchanDetailEntity>()

    override fun getCartSizeFlow(): Flow<Int> {
        return cartDataSource.getCartSizeFlow()
    }

    override suspend fun insertCartItem(
        banchan: BaseBanchan,
        count: Int
    ): Result<Boolean> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                cartDataSource.insertCartItem(banchan.hash, banchan.title, count)
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

    override suspend fun fetchCartItems(): Flow<List<CartModel>> {
        return cartDataSource.fetchCartItemsFlow()
            .map { list ->
                coroutineScope {
                    list.map {
                        async {
                            if(!cacheMap.containsKey(it.hash)) {
                                println("fetchCartItems async run => ${it.hash}")
                                banchanDetailDataSource.fetchBanchanDetail(it.hash).also {
                                    cacheMap[it.hash] = it
                                }
                            }else{
                                cacheMap[it.hash]!!
                            }
                        }
                    }.awaitAll()
                    println("fetchCartItems async list finish")

                    val res = list.map {
                        val detail = cacheMap[it.hash]!!

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