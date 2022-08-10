package com.woowahan.data.datasource

import com.woowahan.data.entity.BanchanDetailEntity

interface BanchanDetailDataSource {
    suspend fun fetchBanchanDetail(banchanHash: String): BanchanDetailEntity
}