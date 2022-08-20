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
        @IODispatcher dispatcher: CoroutineDispatcher
    ): BanchanRepository = BanchanRepositoryImpl(banchansDataSource, dispatcher)

    @Provides
    fun provideBanchanDetailRepository(
        banchansDetailDataSource: BanchanDetailDataSource,
        @IODispatcher dispatcher: CoroutineDispatcher
    ): BanchanDetailRepository = BanchanDetailRepositoryImpl(banchansDetailDataSource, dispatcher)

    @Provides
    fun provideCartRepository(
        cartDataSource: CartDataSource,
        banchanDetailDataSource: BanchanDetailDataSource,
        @IODispatcher dispatcher: CoroutineDispatcher
    ): CartRepository = CartRepositoryImpl(cartDataSource, banchanDetailDataSource, dispatcher)


    @Provides
    fun provideRecentViewedRepository(
        recentViewedDataSource: RecentViewedDataSource,
        banchanDetailDataSource: BanchanDetailDataSource,
        @IODispatcher coroutineDispatcher: CoroutineDispatcher
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