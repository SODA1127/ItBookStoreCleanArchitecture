package com.soda1127.itbookstorecleanarchitecture.data.di

import com.soda1127.itbookstorecleanarchitecture.data.ai.GeminiService
import com.soda1127.itbookstorecleanarchitecture.data.ai.GeminiServiceImpl
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookMemoRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.DefaultBookMemoRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.DefaultBookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.DefaultBookSearchRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookSearchRepository(
        defaultSearchRepository: DefaultBookSearchRepository
    ): BookSearchRepository

    @Binds
    @Singleton
    abstract fun bindBookStoreRepository(
        defaultBookStoreRepository: DefaultBookStoreRepository
    ): BookStoreRepository

    @Binds
    @Singleton
    abstract fun bindBookMemoRepository(
        defaultBookMemoRepository: DefaultBookMemoRepository
    ): BookMemoRepository

    @Binds
    @Singleton
    abstract fun bindGeminiService(
        geminiServiceImpl: GeminiServiceImpl
    ): GeminiService

}
