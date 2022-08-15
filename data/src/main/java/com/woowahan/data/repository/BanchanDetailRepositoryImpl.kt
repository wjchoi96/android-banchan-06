package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.domain.model.BanchanDetailModel
import com.woowahan.domain.repository.BanchanDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BanchanDetailRepositoryImpl @Inject constructor(
    private val banchanDetailDataSource: BanchanDetailDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
): BanchanDetailRepository {

    override suspend fun fetchBanchanDetail(hash: String, title: String): Result<BanchanDetailModel> {
        return withContext(coroutineDispatcher){
            kotlin.runCatching {
                banchanDetailDataSource.fetchBanchanDetail(hash).toDomain(title)
            }
        }
    }
}