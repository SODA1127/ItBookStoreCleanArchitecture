package com.soda1127.itbookstorecleanarchitecture.data.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GsonModule {

    @Provides
    fun provideApiGson() = GsonBuilder()
        .setLenient()
        .create()

}
