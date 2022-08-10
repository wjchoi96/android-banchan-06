package com.woowahan.banchan.di

import com.woowahan.data.datasource.BanchansDataSource
import com.woowahan.data.repository.BanchanRepositoryImpl
import com.woowahan.domain.repository.BanchanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideBanchanRepository(
        banchansDataSource: BanchansDataSource,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): BanchanRepository = BanchanRepositoryImpl(banchansDataSource, dispatcher)

}