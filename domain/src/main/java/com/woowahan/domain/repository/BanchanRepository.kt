package com.woowahan.domain.repository

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BestBanchanModel
import kotlinx.coroutines.flow.Flow

interface BanchanRepository {

    suspend fun fetchBestBanchan(): Flow<List<BestBanchanModel>>

    suspend fun fetchMainDishBanchan(): Flow<List<BanchanModel>>

    suspend fun fetchSoupDishBanchan(): Flow<List<BanchanModel>>

    suspend fun fetchSideDishBanchan(): Flow<List<BanchanModel>>

}