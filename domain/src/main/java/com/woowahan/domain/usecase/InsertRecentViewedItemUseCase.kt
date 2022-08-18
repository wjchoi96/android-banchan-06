package com.woowahan.domain.usecase

import com.woowahan.domain.BanchanDateConverter
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.RecentViewedRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

class InsertRecentViewedItemUseCase (
    private val recentViewedRepository: RecentViewedRepository,
) {
    suspend operator fun invoke(banchan: BanchanModel, time: Date): Flow<Result<Boolean>> {
        val timeStr= BanchanDateConverter.convert(time)
        return recentViewedRepository.insertRecentViewedItem(banchan, timeStr)
    }
}