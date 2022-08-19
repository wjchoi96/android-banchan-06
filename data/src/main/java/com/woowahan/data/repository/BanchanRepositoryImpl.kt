package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchansDataSource
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.repository.BanchanRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BanchanRepositoryImpl @Inject constructor(
    private val remoteDataSource: BanchansDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
): BanchanRepository {

    override suspend fun fetchBestBanchan(): Flow<Result<List<BestBanchanModel>>> {
        return flow {
            emit(
                kotlin.runCatching {
                    remoteDataSource.fetchBestBanchans().body.map { it.toDomain() }
                }
            )
        }.flowOn(coroutineDispatcher)
    }

    override suspend fun fetchMainDishBanchan(): Flow<List<BanchanModel>> = flow {
        remoteDataSource.fetchMainDishBanchans()
            .collect{
                emit(it.body.map { it.toDomain() })
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchSoupDishBanchan(): Flow<Result<List<BanchanModel>>> {
        return flow {
            emit(
                kotlin.runCatching {
                    remoteDataSource.fetchSoupDishBanchans().body.map { it.toDomain() }
                }
            )
        }.flowOn(coroutineDispatcher)
    }

    override suspend fun fetchSideDishBanchan(): Flow<Result<List<BanchanModel>>> {
        return flow {
            emit(
                kotlin.runCatching {
                    remoteDataSource.fetchSideDishBanchans().body.map { it.toDomain() }
                }
            )
        }.flowOn(coroutineDispatcher)
    }
}