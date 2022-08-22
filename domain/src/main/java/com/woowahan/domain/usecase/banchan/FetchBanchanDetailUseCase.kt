package com.woowahan.domain.usecase.banchan

import com.woowahan.domain.model.BanchanDetailModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.BanchanDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchBanchanDetailUseCase(
    private val detailRepository: BanchanDetailRepository
) {
    suspend operator fun invoke(
        hash: String,
        title: String
    ): Flow<DomainEvent<BanchanDetailModel>> = flow<DomainEvent<BanchanDetailModel>> {
        detailRepository.fetchBanchanDetail(hash, title)
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}