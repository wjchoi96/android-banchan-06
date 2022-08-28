package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchanDetailCacheDataSource
import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.data.datasource.RecentViewedDataSource
import com.woowahan.domain.extension.priceStrToLong
import com.woowahan.domain.model.NoConnectivityIOException
import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import com.woowahan.domain.util.BanchanDateConvertUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class RecentViewedRepositoryImpl @Inject constructor(
    private val recentViewedDataSource: RecentViewedDataSource,
    private val banchanDetailDataSource: BanchanDetailDataSource,
    private val banchanDetailCacheDataSource: BanchanDetailCacheDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
) : RecentViewedRepository {

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
            var throwable: Throwable? = null
            recentViewedDataSource.fetchRecentViewedFlow(fetchItemsCnt)
                .collect { list ->
                    coroutineScope {
                        list.map {
                            async {
                                when (banchanDetailCacheDataSource.hasItem(it.hash)) {
                                    true -> {
                                        banchanDetailCacheDataSource.getItem(it.hash)
                                    }
                                    else -> {
                                        println("fetchRecentViewed async run => ${it.hash}")
                                        banchanDetailDataSource.fetchBanchanDetail(it.hash)
                                            .catch {
                                                throwable = it
                                                throw it
                                            }.collect {
                                                banchanDetailCacheDataSource.saveItem(it)
                                            }
                                    }
                                }
                            }
                        }.awaitAll()  // JobCancellationException 이 발생하면 어떤 에러로 인해 Cancel 되었는지 모르게 조용히 처리된다, try catch 가 없으면 로그에 찍히지도 않는다
                    }
                    if(throwable != null) // 때문에, 에러가 발생하면 해당 에러가 무엇인지 캡쳐해 두었다가, 해당 에러를 직접 throw
                        throw throwable!!

                    val res = list.map {
                        banchanDetailCacheDataSource.getItem(it.hash).run {
                            RecentViewedItemModel(
                                id = it.id,
                                hash = it.hash,
                                title = it.title,
                                imageUrl = this.data.thumbImages.first(),
                                price = this.data.prices.first().priceStrToLong(),
                                salePrice = (if (this.data.prices.size > 1) this.data.prices[1] else "0").priceStrToLong(),
                                time = BanchanDateConvertUtil.convert(it.time),
                                description = banchanDetailCacheDataSource.getItem(it.hash).data.productDescription
                            )
                        }
                    }
                    emit(res)
                }
        }.flowOn(coroutineDispatcher)
}