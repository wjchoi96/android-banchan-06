package com.woowahan.data.datasource

import com.woowahan.data.entity.BestBanchanEntity
import com.woowahan.data.entity.MainDishBanchanEntity
import com.woowahan.data.entity.SideDishBanchanEntity
import com.woowahan.data.entity.SoupDishBanchanEntity
import kotlinx.coroutines.flow.Flow

interface BanchansDataSource {

    suspend fun fetchBestBanchans(): BestBanchanEntity

    suspend fun fetchMainDishBanchans(): Flow<MainDishBanchanEntity>

    suspend fun fetchSoupDishBanchans(): SoupDishBanchanEntity

    suspend fun fetchSideDishBanchans(): SideDishBanchanEntity
    
}