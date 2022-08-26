package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchanDetailCacheDataSource
import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.data.datasource.CartDataSource
import com.woowahan.data.entity.BanchanDetailEntity
import com.woowahan.domain.extension.priceStrToLong
import com.woowahan.domain.model.CartModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDataSource: CartDataSource,
    private val banchanDetailDataSource: BanchanDetailDataSource,
    private val banchanDetailCacheDataSource: BanchanDetailCacheDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
) : CartRepository {

    override fun getCartSizeFlow(): Flow<Int> = flow {
        cartDataSource.getCartSizeFlow()
            .collect {
                emit(it)
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun insertCartItem(
        hash: String,
        title: String,
        count: Int
    ): Flow<Boolean> = flow {
        cartDataSource.insertCartItem(hash, title, count)
        emit(true)
    }.flowOn(coroutineDispatcher)

    override suspend fun removeCartItem(vararg hashes: String): Flow<Boolean> = flow {
        cartDataSource.removeCartItem(*hashes)
            .collect {
                emit(it != 0)
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun updateCartItemCount(hash: String, count: Int): Flow<Boolean> = flow {
        cartDataSource.updateCartItemCount(hash, count)
            .collect {
                emit(it != 0)
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun updateCartItemSelect(
        isSelect: Boolean,
        vararg hashes: String,
    ): Flow<Boolean> = flow {
        cartDataSource.updateCartItemSelect(isSelect, *hashes)
            .collect {
                emit(it != 0)
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchCartItemsKey(): Flow<Set<String>> = flow {
        cartDataSource.fetchCartItems()
            .collect {
                emit(it.groupBy { item -> item.hash }.keys)
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchCartItems(): Flow<List<CartModel>> = flow<List<CartModel>> {
        cartDataSource.fetchCartItems()
            .collect { list ->
                coroutineScope {
                    list.map {
                        async {
                            when (banchanDetailCacheDataSource.hasItem(it.hash)) {
                                true -> banchanDetailCacheDataSource.getItem(it.hash)
                                else -> {
                                    println("fetchCartItems async run => ${it.hash}")
                                    banchanDetailDataSource.fetchBanchanDetail(it.hash)
                                        .firstOrNull()?.also {
                                            banchanDetailCacheDataSource.saveItem(it)
                                    }
                                }
                            }
                        }
                    }.awaitAll()

                    val res = list.map {
                        banchanDetailCacheDataSource.getItem(it.hash).run {
                            CartModel(
                                hash = it.hash,
                                count = it.count,
                                title = it.title,
                                imageUrl = this.data.thumbImages.first(),
                                price = this.data.prices.last().priceStrToLong(),
                                isSelected = it.isSelect
                            )
                        }
                    }
                    emit(res)
                }
            }
    }.flowOn(coroutineDispatcher)

}