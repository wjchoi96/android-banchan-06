package com.woowahan.data.apiservice

import com.woowahan.data.entity.BanchanDetailEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BanchanDetailApiService {
    @GET("/detail/{hash}")
    fun fetchBanchanDetail(
        @Path("hash")banchanHash: String
    ): Response<BanchanDetailEntity>
}