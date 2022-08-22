package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchansDataSource
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.repository.BanchanRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BanchanRepositoryImpl @Inject constructor(
    private val remoteDataSource: BanchansDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
): BanchanRepository {

    override suspend fun fetchBestBanchan(): Flow<List<BestBanchanModel>> = flow {
        remoteDataSource.fetchBestBanchans()
            .collect {
                emit(it.body.map { item -> item.toDomain() })
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchMainDishBanchan(): Flow<List<BanchanModel>> = flow {
        remoteDataSource.fetchMainDishBanchans()
            .collect{
                emit(it.body.map { item -> item.toDomain() })
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchSoupDishBanchan(): Flow<List<BanchanModel>> = flow {
        remoteDataSource.fetchSoupDishBanchans()
            .collect {
                emit(it.body.map { item -> item.toDomain() })
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchSideDishBanchan(): Flow<List<BanchanModel>> = flow {
        remoteDataSource.fetchSideDishBanchans()
            .collect {
                emit(it.body.map { item -> item.toDomain() })
            }
    }.flowOn(coroutineDispatcher)
}