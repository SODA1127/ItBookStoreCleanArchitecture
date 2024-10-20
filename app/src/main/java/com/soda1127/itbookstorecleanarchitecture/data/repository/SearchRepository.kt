package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity

interface SearchRepository {

    suspend fun getNewBooks(): List<BookEntity>

    suspend fun searchBooksByKeyword(keyword: String): List<BookEntity>

}
