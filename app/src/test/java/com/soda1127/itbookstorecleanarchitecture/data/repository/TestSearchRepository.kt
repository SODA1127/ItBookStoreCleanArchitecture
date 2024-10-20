package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.google.gson.Gson
import com.soda1127.itbookstorecleanarchitecture.data.reponse.BookSearchResultResponse
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.data.reponse.BookStoreNewResponse
import com.soda1127.itbookstorecleanarchitecture.response.NEW_BOOKS_RESPONSE
import com.soda1127.itbookstorecleanarchitecture.response.SEARCH_RESULT_ANDROID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TestSearchRepository: SearchRepository {

    override suspend fun getNewBooks(): List<BookEntity> = withContext(Dispatchers.IO) {
        val response = Gson().fromJson(NEW_BOOKS_RESPONSE, BookStoreNewResponse::class.java)
        return@withContext response.books
    }

    override suspend fun searchBooksByKeyword(keyword: String): List<BookEntity> = withContext(Dispatchers.IO) {
        val response = Gson().fromJson(SEARCH_RESULT_ANDROID, BookSearchResultResponse::class.java)
        return@withContext listOf()// response.books
    }

}
