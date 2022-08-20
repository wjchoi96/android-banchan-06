package com.woowahan.data.datasource

import com.woowahan.data.entity.BanchanDetailEntity
import kotlinx.coroutines.flow.Flow

interface BanchanDetailDataSource {
    suspend fun fetchBanchanDetail(banchanHash: String): Flow<BanchanDetailEntity>
}