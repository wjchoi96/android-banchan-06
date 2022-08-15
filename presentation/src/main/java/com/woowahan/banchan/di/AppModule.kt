package com.woowahan.banchan.di

import android.app.Application
import com.woowahan.data.database.BanchanRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
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

    @DefaultDispatcher
    @Provides
    @Singleton
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IODispatcher
    @Provides
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideBanchanBoomRoomDataBase(@ApplicationContext application: Application): BanchanRoomDatabase =
        BanchanRoomDatabase.getDatabase(application)
}

@Qualifier
@Singleton
annotation class DefaultDispatcher

@Qualifier
@Singleton
annotation class IODispatcher

@Qualifier
@Singleton
annotation class MainDispatcher