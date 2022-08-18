package com.woowahan.domain.usecase.recentviewed

import com.woowahan.domain.util.BanchanDateConvertUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.RecentViewedRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

class InsertRecentViewedItemUseCase (
    private val recentViewedRepository: RecentViewedRepository,
) {
    suspend operator fun invoke(banchan: BanchanModel, time: Date): Flow<Result<Boolean>> {
        return recentViewedRepository.insertRecentViewedItem(banchan, time)
    }
}