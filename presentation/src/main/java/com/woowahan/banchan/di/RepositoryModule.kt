package com.woowahan.banchan.di

import com.woowahan.data.datasource.BanchanDetailDataSource
import com.woowahan.data.datasource.BanchansDataSource
import com.woowahan.data.datasource.CartDataSource
import com.woowahan.data.datasource.RecentViewedDataSource
import com.woowahan.data.repository.BanchanDetailRepositoryImpl
import com.woowahan.data.repository.BanchanRepositoryImpl
import com.woowahan.data.repository.CartRepositoryImpl
import com.woowahan.data.repository.RecentViewedRepositoryImpl
import com.woowahan.domain.repository.BanchanDetailRepository
import com.woowahan.domain.repository.BanchanRepository
import com.woowahan.domain.repository.CartRepository
import com.woowahan.domain.repository.RecentViewedRepository
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
        coroutineDispatcher: CoroutineDispatcher
    ): RecentViewedRepository = RecentViewedRepositoryImpl(
        recentViewedDataSource,
        banchanDetailDataSource,
        coroutineDispatcher
    )

}