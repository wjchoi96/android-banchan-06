package com.woowahan.data.datasource

import com.woowahan.data.apiservice.BanchanDetailApiService
import com.woowahan.data.entity.BanchanDetailEntity
import com.woowahan.data.util.RetrofitResponseConvertUtil
import javax.inject.Inject

class BanchanDetailRetrofitDataSourceImpl @Inject constructor(
    private val banchanDetailApiService: BanchanDetailApiService
): BanchanDetailDataSource {

    override suspend fun fetchBanchanDetail(banchanHash: String): BanchanDetailEntity {
         banchanDetailApiService.fetchBanchanDetail(banchanHash).let {
             return RetrofitResponseConvertUtil.getData(it)
         }
    }
}