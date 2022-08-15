package com.woowahan.banchan.di

import com.woowahan.data.dao.CartDao
import com.woowahan.data.database.BanchanRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideCartDao(db: BanchanRoomDatabase): CartDao = db.cartDao()
}