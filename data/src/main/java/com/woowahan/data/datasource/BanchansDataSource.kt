package com.woowahan.data.datasource

import com.woowahan.data.entity.BestBanchanEntity
import com.woowahan.data.entity.MainDishBanchanEntity
import com.woowahan.data.entity.SideDishBanchanEntity
import com.woowahan.data.entity.SoupDishBanchanEntity

interface BanchansDataSource {

    suspend fun fetchBestBanchans(): BestBanchanEntity

    suspend fun fetchMainDishBanchans(): MainDishBanchanEntity

    suspend fun fetchSoupDishBanchans(): SoupDishBanchanEntity

    suspend fun fetchSideDishBanchans(): SideDishBanchanEntity
    
}