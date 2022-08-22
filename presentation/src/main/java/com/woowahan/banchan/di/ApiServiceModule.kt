package com.woowahan.banchan.di

import com.woowahan.data.apiservice.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    fun provideBestBanchanApiService(retrofit: Retrofit): BestBanchanApiService =
        retrofit.create(BestBanchanApiService::class.java)

    @Provides
    fun provideMainDishBanchanApiService(retrofit: Retrofit): MainDishBanchanApiService =
        retrofit.create(MainDishBanchanApiService::class.java)

    @Provides
    fun provideSoupDishBanchanApiService(retrofit: Retrofit): SoupDishBanchanApiService =
        retrofit.create(SoupDishBanchanApiService::class.java)

    @Provides
    fun provideSideDishBanchanApiService(retrofit: Retrofit): SideDishBanchanApiService =
        retrofit.create(SideDishBanchanApiService::class.java)

    @Provides
    fun provideBanchanDetailApiService(retrofit: Retrofit): BanchanDetailApiService =
        retrofit.create(BanchanDetailApiService::class.java)
}