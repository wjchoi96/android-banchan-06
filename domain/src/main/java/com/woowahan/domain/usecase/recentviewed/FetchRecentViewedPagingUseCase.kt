package com.woowahan.domain.usecase.recentviewed

import androidx.paging.PagingData
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchRecentViewedPagingUseCase(
    private val recentViewedRepository: RecentViewedRepository
) {
    operator fun invoke(): Flow<DomainEvent<PagingData<RecentViewedItemModel>>> =
        flow<DomainEvent<PagingData<RecentViewedItemModel>>> {
            recentViewedRepository.fetchRecentViewedPaging()
                .collect {
                    emit(DomainEvent.success(it))
                }
        }.catch {
            emit(DomainEvent.failure(it))
        }
}