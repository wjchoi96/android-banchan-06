package com.woowahan.banchan.di

import com.woowahan.domain.repository.*
import com.woowahan.domain.usecase.banchan.*
import com.woowahan.domain.usecase.cart.*
import com.woowahan.domain.usecase.order.*
import com.woowahan.domain.usecase.recentviewed.FetchRecentViewedItemUseCase
import com.woowahan.domain.usecase.recentviewed.FetchRecentViewedPagingUseCase
import com.woowahan.domain.usecase.recentviewed.InsertRecentViewedItemUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /**
     * Banchan
     */
    @Provides
    fun provideFetchBestDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) = FetchBestBanchanUseCase(repo, fetchCartItemsKeyUseCase)

    @Provides
    fun provideFetchMainDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) = FetchMainDishBanchanUseCase(repo, fetchCartItemsKeyUseCase)

    @Provides
    fun provideFetchSoupDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) = FetchSoupDishBanchanUseCase(repo, fetchCartItemsKeyUseCase)

    @Provides
    fun provideFetchSideDishBanchanUseCase(
        repo: BanchanRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) = FetchSideDishBanchanUseCase(repo, fetchCartItemsKeyUseCase)

    /**
     * Cart
     */
    @Provides
    fun provideGetCartSizeUseCase(repo: CartRepository) =
        GetCartItemsSizeFlowUseCase(repo)

    @Provides
    fun provideFetchCartItemsUseCase(
        repo: CartRepository,
        fetchRecentViewedItemUseCase: FetchRecentViewedItemUseCase
    ) = FetchCartItemsUseCase(repo, fetchRecentViewedItemUseCase)

    @Provides
    fun provideFetchCartItemsKeyUseCase(repo: CartRepository) =
        FetchCartItemsKeyUseCase(repo)

    @Provides
    fun provideInsertCartItemsUseCase(repo: CartRepository) =
        InsertCartItemUseCase(repo)

    @Provides
    fun provideRemoveCartItemUseCase(repo: CartRepository) =
        RemoveCartItemUseCase(repo)

    @Provides
    fun provideUpdateCartItemCountUseCase(repo: CartRepository) =
        UpdateCartItemCountUseCase(repo)

    @Provides
    fun provideUpdateCartItemSelectUseCase(repo: CartRepository) =
        UpdateCartItemSelectUseCase(repo)

    /**
     * Recent Viewed
     */
    @Provides
    fun provideFetchAllRecentViewedItemUseCase(
        repo: RecentViewedRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) = FetchRecentViewedItemUseCase(repo, fetchCartItemsKeyUseCase)

    @Provides
    fun provideInsertRecentViewedItemUseCase(repo: RecentViewedRepository) =
        InsertRecentViewedItemUseCase(repo)

    @Provides
    fun provideFetchRecentViewedPagingUseCase(
        repo: RecentViewedRepository
    ) = FetchRecentViewedPagingUseCase(repo)

    /**
     * order
     */
    @Provides
    fun provideFetchOrdersPagingUseCase(repo: OrderRepository) =
        FetchOrderPagingUseCase(repo)

    @Provides
    fun provideFetchOrderUseCase(repo: OrderRepository) =
        FetchOrderUseCase(repo)

    @Provides
    fun provideGetDeliveryOrderCountUseCase(repo: OrderRepository) =
        GetDeliveryOrderCountUseCase(repo)

    @Provides
    fun provideInsertOrderUseCase(repo: OrderRepository) =
        InsertOrderUseCase(repo)

    @Provides
    fun provideUpdateOrderUseCase(repo: OrderRepository) =
        UpdateOrderUseCase(repo)

    /**
     * detail
     */
    @Provides
    fun provideFetchBanchanDetailUseCase(
        repo: BanchanDetailRepository,
        fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
    ) =
        FetchBanchanDetailUseCase(repo, fetchCartItemsKeyUseCase)
}