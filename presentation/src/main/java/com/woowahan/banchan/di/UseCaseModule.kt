package com.woowahan.banchan.di

import com.woowahan.domain.repository.BanchanRepository
import com.woowahan.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideFetchBestDishBanchanUseCase(impl: BanchanRepository) =
        FetchBestBanchanUseCase(impl)

    @Provides
    fun provideFetchMainDishBanchanUseCase(impl: BanchanRepository) =
        FetchMainDishBanchanUseCase(impl)

    @Provides
    fun provideFetchSoupDishBanchanUseCase(impl: BanchanRepository) =
        FetchSoupDishBanchanUseCase(impl)

    @Provides
    fun provideFetchSideDishBanchanUseCase(impl: BanchanRepository) =
        FetchSideDishBanchanUseCase(impl)

}