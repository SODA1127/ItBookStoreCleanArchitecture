package com.soda1127.itbookstorecleanarchitecture.data.di

import android.content.Context
import androidx.room.Room
import com.soda1127.itbookstorecleanarchitecture.data.db.BookStoreDatabase
import com.soda1127.itbookstorecleanarchitecture.data.db.dao.BookInWishListDao
import com.soda1127.itbookstorecleanarchitecture.data.db.dao.BookMemoDao
import com.soda1127.itbookstorecleanarchitecture.data.db.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDB(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, BookStoreDatabase::class.java, BookStoreDatabase.DB_NAME).build()

    @Provides
    @Singleton
    fun provideBookInWishListDao(
        database: BookStoreDatabase
    ): BookInWishListDao = database.BookInWishListDao()

    @Provides
    @Singleton
    fun provideBookMemoDao(
        database: BookStoreDatabase
    ): BookMemoDao = database.BookMemoDao()

    @Provides
    @Singleton
    fun provideSearchHistoryDao(
        database: BookStoreDatabase
    ): SearchHistoryDao = database.SearchHistoryDao()

}
