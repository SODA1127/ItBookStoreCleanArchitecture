package com.soda1127.itbookstorecleanarchitecture.data.di

import com.soda1127.itbookstorecleanarchitecture.data.repository.SearchRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.SearchRepositoryImpl
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
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository

}
