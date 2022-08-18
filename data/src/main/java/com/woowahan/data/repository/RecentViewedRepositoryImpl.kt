package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.data.datasource.RecentViewedDataSource
import com.woowahan.domain.extension.priceStrToLong
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import com.woowahan.domain.util.BanchanDateConvertUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class RecentViewedRepositoryImpl @Inject constructor(
    private val recentViewedDataSource: RecentViewedDataSource,
    private val banchanDetailDataSource: BanchanDetailDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
) : RecentViewedRepository {
    override suspend fun insertRecentViewedItem(
        banchan: BanchanModel,
        time: Date
    ): Flow<Result<Boolean>> {
        return flow<Result<Boolean>> {
            withContext(coroutineDispatcher) {
                kotlin.runCatching {
                    recentViewedDataSource.insertRecentViewed(banchan, BanchanDateConvertUtil.convert(time))
                    true
                }
            }
        }.flowOn(coroutineDispatcher)
    }

    override suspend fun fetchRecentViewedItems(): Flow<Result<List<RecentViewedItemModel>>> {
        return recentViewedDataSource.fetchRecentViewedFlow()
            .map { list ->
                kotlin.runCatching {
                    coroutineScope {
                        val detailMap = list.map {
                            async {
                                println("fetchRecentViewed async run => ${it.hash}")
                                banchanDetailDataSource.fetchBanchanDetail(it.hash)
                            }
                        }.awaitAll().associateBy { item -> item.hash }
                        println("fetchRecentViewed async list finish")

                        val res = list.map {
                            val detail = detailMap[it.hash]!!

                            RecentViewedItemModel(
                                hash = it.hash,
                                title = it.title,
                                imageUrl = detail.data.thumbImages.first(),
                                n_price = detail.data.prices.first().priceStrToLong(),
                                s_price = (if (detail.data.prices.size > 1) detail.data.prices[1] else "0").priceStrToLong(),
                                time = BanchanDateConvertUtil.convert(it.time)
                            )
                        }
                        println("fetchRecentViewed res => $res")
                        res
                    }
                }
            }.flowOn(coroutineDispatcher)
    }
}