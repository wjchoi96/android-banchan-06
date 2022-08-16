package com.woowahan.domain.repository

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BestBanchanModel
import kotlinx.coroutines.flow.Flow

interface BanchanRepository {

    suspend fun fetchBestBanchan(): Flow<Result<List<BestBanchanModel>>>

    suspend fun fetchMainDishBanchan(): Flow<Result<List<BanchanModel>>>

    suspend fun fetchSoupDishBanchan(): Flow<Result<List<BanchanModel>>>

    suspend fun fetchSideDishBanchan(): Flow<Result<List<BanchanModel>>>

}