package com.woowahan.domain.repository

import com.woowahan.domain.model.BanchanDetailModel
import kotlinx.coroutines.flow.Flow

interface BanchanDetailRepository {
    suspend fun fetchBanchanDetail(hash: String, title: String): Flow<BanchanDetailModel>
}