package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.soda1127.itbookstorecleanarchitecture.data.BookStoreApi
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val bookStoreApi: BookStoreApi
): SearchRepository {

    override suspend fun getNewBooks(): List<BookEntity> {
        val response = bookStoreApi.getNewBooks()
        if(response.isSuccessful) {
            return response.body()?.books ?: listOf()
        } else throw Exception(response.message())
    }

    override suspend fun searchBooksByKeyword(keyword: String): List<BookEntity> = withContext(Dispatchers.IO) {
        val response = bookStoreApi.searchBooksByKeyword(keyword)
        return@withContext if (response.isSuccessful) {
            response.body()?.books ?: listOf()
        } else {
            throw Exception("This error should be occurred in this case")
        }
    }

}
