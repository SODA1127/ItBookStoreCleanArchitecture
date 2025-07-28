package com.soda1127.itbookstorecleanarchitecture.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object OkHttpClientModule {

    private const val CONNECT_TIMEOUT_MILLIS = 3000L

    @Provides
    fun provideOkhttpBuilder(
        @HttpLoggingInterceptorQualifier httpLoggingInterceptor: Interceptor,
    ) = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
        .build()


}
