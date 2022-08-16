package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.domain.model.BanchanDetailModel
import com.woowahan.domain.repository.BanchanDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BanchanDetailRepositoryImpl @Inject constructor(
    private val banchanDetailDataSource: BanchanDetailDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
): BanchanDetailRepository {

    override suspend fun fetchBanchanDetail(hash: String, title: String): Flow<Result<BanchanDetailModel>> {
        return flow {
            emit(
                kotlin.runCatching {
                    banchanDetailDataSource.fetchBanchanDetail(hash).toDomain(title)
                }
            )
        }.flowOn(coroutineDispatcher)
    }
}