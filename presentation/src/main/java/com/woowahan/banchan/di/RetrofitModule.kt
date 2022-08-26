package com.woowahan.banchan.di

import android.content.Context
import com.woowahan.banchan.network.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideNetworkConnectionInterceptor(
        @ApplicationContext context: Context
    ): NetworkConnectionInterceptor = NetworkConnectionInterceptor(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        addInterceptor(networkConnectionInterceptor)
    }.build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder().apply {
        baseUrl("https://api.codesquad.kr/onban/")
        client(okHttpClient)
        addConverterFactory(GsonConverterFactory.create())
    }.build()


}