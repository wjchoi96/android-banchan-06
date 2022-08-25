package com.woowahan.data.datasource

import com.woowahan.data.entity.BanchanDetailEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SpyBanchanDetailDataSourceImpl(
    private val banchanDetailEntity: BanchanDetailEntity
): BanchanDetailDataSource {
    var fetchMethodCallCount = 0
        private set

    override suspend fun fetchBanchanDetail(banchanHash: String): Flow<BanchanDetailEntity> = flow {
        fetchMethodCallCount++
        emit(banchanDetailEntity)
    }
}