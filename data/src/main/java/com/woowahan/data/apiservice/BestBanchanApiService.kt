package com.woowahan.data.apiservice

import com.woowahan.data.entity.BestBanchanEntity
import retrofit2.Response
import retrofit2.http.GET

interface BestBanchanApiService {
    @GET("best")
    suspend fun fetchBestBanchans(): Response<BestBanchanEntity>
}