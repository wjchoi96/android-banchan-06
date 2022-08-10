package com.woowahan.domain.repository

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BestBanchanModel

interface BanchanRepository {

    suspend fun fetchBestBanchan(): Result<List<BestBanchanModel>>

    suspend fun fetchMainDishBanchan(): Result<List<BanchanModel>>

    suspend fun fetchSoupDishBanchan(): Result<List<BanchanModel>>

    suspend fun fetchSideDishBanchan(): Result<List<BanchanModel>>

}