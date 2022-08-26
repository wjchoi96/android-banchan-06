package com.woowahan.data.datasource

import com.woowahan.data.entity.BanchanDetailEntity

interface BanchanDetailCacheDataSource {
    fun hasItem(key: String): Boolean

    fun saveItem(item: BanchanDetailEntity)

    fun getItem(key: String): BanchanDetailEntity
}