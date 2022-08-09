package com.woowahan.data.apiservice

import com.woowahan.data.entity.SideDishBanchanEntity
import retrofit2.http.GET

interface SideBanchanApiService {
    @GET("side")
    suspend fun fetchSideDishBanchans(): SideDishBanchanEntity
}