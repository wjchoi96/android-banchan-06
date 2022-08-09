package com.woowahan.banchan.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * App
 * Retrofit
 * ApiService, Room
 * DataSource
 * Repository
 * UseCase
 *
 * 순으로 종속성 주입
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @ApplicationContext
    @Singleton
    fun provideApplication(application: Application) = application

}