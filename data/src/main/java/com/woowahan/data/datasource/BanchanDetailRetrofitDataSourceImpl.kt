package com.woowahan.data.datasource

import com.woowahan.data.apiservice.BanchanDetailApiService
import com.woowahan.data.entity.BanchanDetailEntity
import com.woowahan.data.util.RetrofitResponseConvertUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BanchanDetailRetrofitDataSourceImpl @Inject constructor(
    private val banchanDetailApiService: BanchanDetailApiService
): BanchanDetailDataSource {

    override suspend fun fetchBanchanDetail(banchanHash: String): Flow<BanchanDetailEntity> = flow {
        emit(RetrofitResponseConvertUtil.getData(banchanDetailApiService.fetchBanchanDetail(banchanHash)))
    }
}