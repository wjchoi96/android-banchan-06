package com.woowahan.data.apiservice

import com.woowahan.data.entity.MainDishBanchanEntity
import retrofit2.Response
import retrofit2.http.GET

interface MainDishBanchanApiService {
    @GET("main")
    suspend fun fetchMainDishBanchans(): Response<MainDishBanchanEntity>
}
