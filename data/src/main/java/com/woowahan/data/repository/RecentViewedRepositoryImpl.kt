package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.data.datasource.RecentViewedDataSource
import com.woowahan.data.entity.BanchanDetailEntity
import com.woowahan.domain.extension.priceStrToLong
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import com.woowahan.domain.util.BanchanDateConvertUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class RecentViewedRepositoryImpl @Inject constructor(
    private val recentViewedDataSource: RecentViewedDataSource,
    private val banchanDetailDataSource: BanchanDetailDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
) : RecentViewedRepository {

    private val cacheMap = mutableMapOf<String, BanchanDetailEntity>()

    override suspend fun insertRecentViewedItem(
        banchan: BanchanModel,
        time: Date
    ): Flow<Boolean> = flow {
        recentViewedDataSource.insertRecentViewed(
            banchan,
            BanchanDateConvertUtil.convert(time)
        )
        emit(true)
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchRecentViewedItems(fetchItemsCnt: Int?): Flow<List<RecentViewedItemModel>> = flow {
        recentViewedDataSource.fetchRecentViewedFlow(fetchItemsCnt)
            .collect { list ->
                coroutineScope {
                    list.map {
                        async {
                            when(cacheMap.containsKey(it.hash)){
                                true -> {
                                    cacheMap[it.hash]!!
                                }
                                else -> {
                                    println("fetchRecentViewed async run => ${it.hash}")
                                    banchanDetailDataSource.fetchBanchanDetail(it.hash).first().also {
                                        cacheMap[it.hash] = it
                                    }
                                }
                            }
                        }
                    }.awaitAll()

                    val res = list.map {
                        cacheMap[it.hash]!!.run {
                            RecentViewedItemModel(
                                hash = it.hash,
                                title = it.title,
                                imageUrl = this.data.thumbImages.first(),
                                price = this.data.prices.first().priceStrToLong(),
                                salePrice = (if (this.data.prices.size > 1) this.data.prices[1] else "0").priceStrToLong(),
                                time = BanchanDateConvertUtil.convert(it.time)
                            )
                        }
                    }
                    emit(res)
                }
            }
    }.flowOn(coroutineDispatcher)
}