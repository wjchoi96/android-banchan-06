package com.woowahan.domain.usecase.recentviewed

import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import kotlinx.coroutines.flow.*

class FetchRecentViewedItemUseCase(
    private val recentViewedRepository: RecentViewedRepository,
) {
    suspend operator fun invoke(fetchItemsCnt: Int? = null): Flow<DomainEvent<List<RecentViewedItemModel>>> = flow<DomainEvent<List<RecentViewedItemModel>>> {
        recentViewedRepository.fetchRecentViewedItems(fetchItemsCnt)
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}