package com.woowahan.banchan.di

import com.woowahan.data.datasource.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun provideBanchanDataSource(impl: BanchansRetrofitDataSourceImpl): BanchansDataSource

    @Binds
    abstract fun provideBanchanDetailDataSource(impl: BanchanDetailRetrofitDataSourceImpl): BanchanDetailDataSource

    @Binds
    abstract fun provideCartDataSource(impl: CartDataSourceImpl): CartDataSource

    @Binds
    abstract fun provideRecentViewedDataSource(impl: RecentViewedDataSourceImpl): RecentViewedDataSource
}