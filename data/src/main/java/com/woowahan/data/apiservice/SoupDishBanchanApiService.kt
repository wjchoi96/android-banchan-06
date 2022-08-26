package com.woowahan.data.apiservice

import com.woowahan.data.entity.SoupDishBanchanEntity
import retrofit2.Response
import retrofit2.http.GET

interface SoupDishBanchanApiService {
    @GET("soup")
    suspend fun fetchSoupDishBanchans(): Response<SoupDishBanchanEntity>
}