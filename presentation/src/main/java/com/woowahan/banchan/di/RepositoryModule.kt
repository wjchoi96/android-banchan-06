package com.woowahan.banchan.di

import com.woowahan.data.datasource.BanchansDataSource
import com.woowahan.data.datasource.CartDataSource
import com.woowahan.data.repository.BanchanRepositoryImpl
import com.woowahan.data.repository.CartRepositoryImpl
import com.woowahan.domain.repository.BanchanRepository
import com.woowahan.domain.repository.CartRepository
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
    fun provideCartRepository(
        cartDataSource: CartDataSource,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CartRepository = CartRepositoryImpl(cartDataSource, dispatcher)

}