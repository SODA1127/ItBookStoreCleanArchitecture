package com.soda1127.itbookstorecleanarchitecture.data.di

import com.soda1127.itbookstorecleanarchitecture.data.di.api.BooksApiService
import com.soda1127.itbookstorecleanarchitecture.data.di.api.ApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideBookStoreApi(
        apiClient: ApiClient
    ): BooksApiService = apiClient.provideBookStoreApi()

}
