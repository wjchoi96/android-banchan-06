package com.woowahan.domain.usecase.recentviewed

import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.RecentViewedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.*

class InsertRecentViewedItemUseCase(
    private val recentViewedRepository: RecentViewedRepository,
) {
    suspend operator fun invoke(
        hash: String,
        title: String,
        time: Date
    ): Flow<DomainEvent<Boolean>> = flow<DomainEvent<Boolean>> {
        recentViewedRepository.insertRecentViewedItem(hash, title, time)
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}