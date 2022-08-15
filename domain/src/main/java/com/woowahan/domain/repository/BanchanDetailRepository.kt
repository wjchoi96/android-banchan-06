package com.woowahan.domain.repository

import com.woowahan.domain.model.BanchanDetailModel

interface BanchanDetailRepository {
    suspend fun fetchBanchanDetail(hash: String, title: String): Result<BanchanDetailModel>
}