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
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDataSource: CartDataSource,
    private val banchanDetailDataSource: BanchanDetailDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
) : CartRepository {

    private val cacheMap = mutableMapOf<String, BanchanDetailEntity>()

    override fun getCartSizeFlow(): Flow<Int> = flow {
        cartDataSource.getCartSizeFlow()
            .collect {
                emit(it)
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun insertCartItem(
        banchan: BaseBanchan,
        count: Int
    ): Flow<Boolean> = flow {
        cartDataSource.insertCartItem(banchan.hash, banchan.title, count)
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

    override suspend fun fetchCartItemsKey(): Flow<Set<String>> = flow<Set<String>> {
        cartDataSource.fetchCartItems()
            .collect {
                emit(it.groupBy { item -> item.hash }.keys)
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchCartItems(): Flow<List<CartModel>> {
        return cartDataSource.fetchCartItems()
            .map { list ->
                coroutineScope {
                    list.map {
                        async {
                            if(!cacheMap.containsKey(it.hash)) {
                                println("fetchCartItems async run => ${it.hash}")
                                banchanDetailDataSource.fetchBanchanDetail(it.hash).firstOrNull()?.also {
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