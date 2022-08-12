package com.woowahan.banchan.di

import com.woowahan.domain.repository.BanchanRepository
import com.woowahan.domain.repository.CartRepository
import com.woowahan.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideFetchBestDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsUseCase: FetchCartItemsUseCase
    ) = FetchBestBanchanUseCase(repo, fetchCartItemsUseCase)

    @Provides
    fun provideFetchMainDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsUseCase: FetchCartItemsUseCase
    ) = FetchMainDishBanchanUseCase(repo, fetchCartItemsUseCase)

    @Provides
    fun provideFetchSoupDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsUseCase: FetchCartItemsUseCase
    ) = FetchSoupDishBanchanUseCase(repo, fetchCartItemsUseCase)

    @Provides
    fun provideFetchSideDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsUseCase: FetchCartItemsUseCase
    ) = FetchSideDishBanchanUseCase(repo, fetchCartItemsUseCase)

    @Provides
    fun provideGetCartSizeUseCase(repo: CartRepository) =
        GetCartItemsSizeFlowUseCase(repo)

    @Provides
    fun provideFetchCartItemsUseCase(repo: CartRepository) =
        FetchCartItemsUseCase(repo)

    @Provides
    fun provideInsertCartItemsUseCase(repo: CartRepository) =
        InsertCartItemUseCase(repo)

    @Provides
    fun provideRemoveCartItemUseCase(repo: CartRepository) =
        RemoveCartItemUseCase(repo)

    @Provides
    fun provideRemoveCartItemsUseCase(repo: CartRepository) =
        RemoveCartItemsUseCase(repo)

    @Provides
    fun provideUpdateCartItemUseCase(repo: CartRepository) =
        UpdateCartItemUseCase(repo)

}