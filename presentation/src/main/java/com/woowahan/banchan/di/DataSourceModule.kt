package com.woowahan.banchan.di

import com.woowahan.data.datasource.BanchansDataSource
import com.woowahan.data.datasource.BanchansRetrofitDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun provideBanchanDataSource(impl: BanchansRetrofitDataSourceImpl): BanchansDataSource
}