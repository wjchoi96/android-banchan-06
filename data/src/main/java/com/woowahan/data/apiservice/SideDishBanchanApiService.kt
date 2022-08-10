package com.woowahan.data.apiservice

import com.woowahan.data.entity.SideDishBanchanEntity
import retrofit2.Response
import retrofit2.http.GET

interface SideDishBanchanApiService {
    @GET("side")
    suspend fun fetchSideDishBanchans(): Response<SideDishBanchanEntity>
}