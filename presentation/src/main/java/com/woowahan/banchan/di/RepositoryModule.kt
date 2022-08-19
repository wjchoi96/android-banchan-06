package com.woowahan.banchan.di

import com.woowahan.data.datasource.*
import com.woowahan.data.repository.*
import com.woowahan.domain.repository.*
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

    @Provides
    fun provideBanchanDetailRepository(
        banchansDetailDataSource: BanchanDetailDataSource,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): BanchanDetailRepository = BanchanDetailRepositoryImpl(banchansDetailDataSource, dispatcher)

    @Provides
    fun provideCartRepository(
        cartDataSource: CartDataSource,
        banchanDetailDataSource: BanchanDetailDataSource,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CartRepository = CartRepositoryImpl(cartDataSource, banchanDetailDataSource, dispatcher)


    @Provides
    fun provideRecentViewedRepository(
        recentViewedDataSource: RecentViewedDataSource,
        banchanDetailDataSource: BanchanDetailDataSource,
        @DefaultDispatcher coroutineDispatcher: CoroutineDispatcher
    ): RecentViewedRepository = RecentViewedRepositoryImpl(
        recentViewedDataSource,
        banchanDetailDataSource,
        coroutineDispatcher
    )

    @Provides
    fun provideOrderRepository(
        orderDataSource: OrderDataSource,
        @IODispatcher coroutineDispatcher: CoroutineDispatcher
    ): OrderRepository = OrderRepositoryImpl(
        orderDataSource, coroutineDispatcher
    )

}