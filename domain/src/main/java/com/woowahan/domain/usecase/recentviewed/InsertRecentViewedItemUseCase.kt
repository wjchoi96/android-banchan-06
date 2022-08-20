package com.woowahan.domain.usecase.recentviewed

import com.woowahan.domain.util.BanchanDateConvertUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.RecentViewedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.*

class InsertRecentViewedItemUseCase (
    private val recentViewedRepository: RecentViewedRepository,
) {
    suspend operator fun invoke(banchan: BanchanModel, time: Date): Flow<DomainEvent<Boolean>> = flow<DomainEvent<Boolean>> {
        recentViewedRepository.insertRecentViewedItem(banchan, time)
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}