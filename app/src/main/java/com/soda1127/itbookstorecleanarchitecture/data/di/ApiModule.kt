package com.soda1127.itbookstorecleanarchitecture.data.di

import com.soda1127.itbookstorecleanarchitecture.data.BookStoreApi
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
    ): BookStoreApi = apiClient.provideBookStoreApi()

}
