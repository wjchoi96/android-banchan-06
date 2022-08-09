package com.woowahan.data.apiservice

import retrofit2.http.GET
import retrofit2.http.Path

interface BanchanDetailApiService {
    @GET("/detail/{hash}")
    fun fetchBanchanDetail(
        @Path("hash")banchanHash: String
    )
}