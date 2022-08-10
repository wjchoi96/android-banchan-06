package com.woowahan.data.repository

import com.woowahan.data.datasource.BanchansDataSource
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.repository.BanchanRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BanchanRepositoryImpl @Inject constructor(
    private val remoteDataSource: BanchansDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
): BanchanRepository {

    override suspend fun fetchBestBanchan(): Result<List<BestBanchanModel>> {
        return withContext(coroutineDispatcher) {
            kotlin.runCatching {
                remoteDataSource.fetchBestBanchans().body.map { it.toDomain() }
            }
        }
    }

    override suspend fun fetchMainDishBanchan(): Result<List<BanchanModel>> {
        return withContext(coroutineDispatcher){
            kotlin.runCatching {
                remoteDataSource.fetchMainDishBanchans().body.map { it.toDomain() }
            }
        }
    }

    override suspend fun fetchSoupDishBanchan(): Result<List<BanchanModel>> {
        return withContext(coroutineDispatcher){
            kotlin.runCatching {
                remoteDataSource.fetchSoupDishBanchans().body.map { it.toDomain() }
            }
        }
    }

    override suspend fun fetchSideDishBanchan(): Result<List<BanchanModel>> {
        return withContext(coroutineDispatcher){
            kotlin.runCatching {
                remoteDataSource.fetchSideDishBanchans().body.map { it.toDomain() }
            }
        }
    }
}