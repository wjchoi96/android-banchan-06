package com.woowahan.data.repository

import androidx.paging.PagingData
import androidx.paging.map
import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.data.datasource.RecentViewedDataSource
import com.woowahan.data.entity.BanchanDetailEntity
import com.woowahan.domain.extension.priceStrToLong
import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import com.woowahan.domain.util.BanchanDateConvertUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class RecentViewedRepositoryImpl @Inject constructor(
    private val recentViewedDataSource: RecentViewedDataSource,
    private val banchanDetailDataSource: BanchanDetailDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
) : RecentViewedRepository {

    private val cacheMap = mutableMapOf<String, BanchanDetailEntity>()

    override suspend fun insertRecentViewedItem(
        hash: String,
        title: String,
        time: Date
    ): Flow<Boolean> = flow {
        recentViewedDataSource.insertRecentViewed(
            hash,
            title,
            BanchanDateConvertUtil.convert(time)
        )
        emit(true)
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchRecentViewedItems(fetchItemsCnt: Int?): Flow<List<RecentViewedItemModel>> =
        flow {
            recentViewedDataSource.fetchRecentViewedFlow(fetchItemsCnt)
                .collect { list ->
                    coroutineScope {
                        list.map {
                            async {
                                when (cacheMap.containsKey(it.hash)) {
                                    true -> {
                                        cacheMap[it.hash]!!
                                    }
                                    else -> {
                                        println("fetchRecentViewed async run => ${it.hash}")
                                        banchanDetailDataSource.fetchBanchanDetail(it.hash).first()
                                            .also {
                                                cacheMap[it.hash] = it
                                            }
                                    }
                                }
                            }
                        }.awaitAll()

                        val res = list.map {
                            cacheMap[it.hash]!!.run {
                                RecentViewedItemModel(
                                    id = it.id,
                                    hash = it.hash,
                                    title = it.title,
                                    imageUrl = this.data.thumbImages.first(),
                                    price = this.data.prices.first().priceStrToLong(),
                                    salePrice = (if (this.data.prices.size > 1) this.data.prices[1] else "0").priceStrToLong(),
                                    time = BanchanDateConvertUtil.convert(it.time),
                                    description = cacheMap[it.hash]!!.data.productDescription
                                )
                            }
                        }
                        emit(res)
                    }
                }
        }.flowOn(coroutineDispatcher)

    override suspend fun fetchRecentViewedPaging(): Flow<PagingData<RecentViewedItemModel>> = flow {
        recentViewedDataSource.fetchRecentViewedPaging()
            .map { pagingData ->
                pagingData.map { item ->
                    coroutineScope {
                        withContext(Dispatchers.Default) {
                            if (cacheMap.containsKey(item.hash)) {
                                cacheMap[item.hash]
                            } else {
                                banchanDetailDataSource.fetchBanchanDetail(item.hash).first()
                                    .also {
                                        cacheMap[item.hash] = it
                                    }
                            }
                        }

                        cacheMap[item.hash]!!.run {
                            RecentViewedItemModel(
                                id = item.id,
                                hash = item.hash,
                                title = item.title,
                                imageUrl = this.data.thumbImages.first(),
                                price = this.data.prices.first().priceStrToLong(),
                                salePrice = (if (this.data.prices.size > 1) this.data.prices[1] else "0").priceStrToLong(),
                                time = BanchanDateConvertUtil.convert(item.time),
                                description = cacheMap[item.hash]!!.data.productDescription
                            )
                        }
                    }
                }
            }.collect {
                emit(it)
            }

    }
}