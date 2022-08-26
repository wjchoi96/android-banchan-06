package com.woowahan.data.datasource

import com.woowahan.data.entity.BanchanDetailEntity
import javax.inject.Inject

class BanchanDetailCacheDataSourceImpl @Inject constructor(): BanchanDetailCacheDataSource{

    private val cacheMap = mutableMapOf<String, BanchanDetailEntity>()

    override fun hasItem(key: String): Boolean = cacheMap.containsKey(key)

    override fun saveItem(item: BanchanDetailEntity) {
        cacheMap[item.hash] = item
    }

    override fun getItem(key: String): BanchanDetailEntity = cacheMap[key]!!
}