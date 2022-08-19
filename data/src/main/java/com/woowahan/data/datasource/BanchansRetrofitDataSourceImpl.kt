package com.woowahan.data.datasource

import com.woowahan.data.apiservice.BestBanchanApiService
import com.woowahan.data.apiservice.MainDishBanchanApiService
import com.woowahan.data.apiservice.SideDishBanchanApiService
import com.woowahan.data.apiservice.SoupDishBanchanApiService
import com.woowahan.data.entity.*
import com.woowahan.data.util.RetrofitResponseConvertUtil
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class BanchansRetrofitDataSourceImpl @Inject constructor(
    private val bestBanchanApiService: BestBanchanApiService,
    private val mainDishBanchanApiService: MainDishBanchanApiService,
    private val soupDishBanchanApiService: SoupDishBanchanApiService,
    private val sideDishBanchanApiService: SideDishBanchanApiService,
): BanchansDataSource {

    override suspend fun fetchBestBanchans(): BestBanchanEntity {
        val res = bestBanchanApiService.fetchBestBanchans()
        return RetrofitResponseConvertUtil.getData(res, res.body()?.statusCode)
    }

    override suspend fun fetchMainDishBanchans(): Flow<MainDishBanchanEntity> = flow {
        mainDishBanchanApiService.fetchMainDishBanchans().let {
            emit(RetrofitResponseConvertUtil.getData(it, it.body()?.statusCode))
        }
    }

    override suspend fun fetchSoupDishBanchans(): SoupDishBanchanEntity {
        val res = soupDishBanchanApiService.fetchSoupDishBanchans()
        return RetrofitResponseConvertUtil.getData(res, res.body()?.statusCode)
    }

    override suspend fun fetchSideDishBanchans(): SideDishBanchanEntity {
        val res = sideDishBanchanApiService.fetchSideDishBanchans()
        return RetrofitResponseConvertUtil.getData(res, res.body()?.statusCode)
    }
}