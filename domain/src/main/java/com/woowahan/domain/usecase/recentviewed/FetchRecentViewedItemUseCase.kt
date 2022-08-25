package com.woowahan.domain.usecase.recentviewed

import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import com.woowahan.domain.usecase.cart.FetchCartItemsKeyUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class FetchRecentViewedItemUseCase(
    private val recentViewedRepository: RecentViewedRepository,
    private val fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
) {
    suspend operator fun invoke(fetchItemsCnt: Int? = null): Flow<DomainEvent<List<RecentViewedItemModel>>> = flow<DomainEvent<List<RecentViewedItemModel>>> {
        recentViewedRepository.fetchRecentViewedItems(fetchItemsCnt).combine(fetchCartItemsKeyUseCase()) { list, key ->
            val cart = key.getOrThrow()
            list.map { item ->
                if (cart.contains(item.hash))
                    item.copy(isCartItem = true)
                else
                    item
            }
        }.collect {
            emit(DomainEvent.success(it))
        }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}