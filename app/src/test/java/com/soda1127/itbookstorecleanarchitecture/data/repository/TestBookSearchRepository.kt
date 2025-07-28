package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.google.gson.Gson
import com.soda1127.itbookstorecleanarchitecture.data.json.BookInfoResponseJson
import com.soda1127.itbookstorecleanarchitecture.data.json.BookSearchResponseJsonFirstPage
import com.soda1127.itbookstorecleanarchitecture.data.json.NewBooksResponseJson
import com.soda1127.itbookstorecleanarchitecture.data.response.BookSearchResultResponse
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookInfoEntity
import com.soda1127.itbookstorecleanarchitecture.data.response.BookInfoResponse
import com.soda1127.itbookstorecleanarchitecture.data.response.BookStoreNewResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TestBookStoreRepository : BookStoreRepository {

    companion object {
        const val TEST_ISBN13 = "1001622115721"

        const val TEST_KEYWORD = "kotlin"
    }

    private val booksInWishList = mutableListOf<BookEntity>()

    override suspend fun getNewBooks(): Flow<List<BookEntity>> = withContext(Dispatchers.Main) {
        flow<List<BookEntity>> {
            delay(100)
            emit(Gson().fromJson(NewBooksResponseJson, BookStoreNewResponse::class.java).books)
        }
    }

    override suspend fun getBookInfo(isbn13: String): Flow<BookInfoEntity?> = withContext(Dispatchers.Main) {
        flow {
            delay(100)
            if (isbn13 == TEST_ISBN13) {
                emit(Gson().fromJson(BookInfoResponseJson, BookInfoResponse::class.java).toEntity())
            } else {
                emit(null)
            }
        }
    }

    override suspend fun getBooksInWishList(): Flow<List<BookEntity>> = withContext(Dispatchers.Main) {
        flow {
            delay(100)
            emit(booksInWishList)
        }
    }

    override suspend fun getBookInWishList(isbn13: String): Flow<BookEntity?> = withContext(Dispatchers.Main) {
        flow {
            delay(100)
            emit(booksInWishList.find { it.isbn13 == isbn13 })
        }
    }

    override suspend fun addBookInWishList(bookEntity: BookEntity) {
        delay(100)
        booksInWishList.add(bookEntity)
    }

    override suspend fun removeBookInWishList(isbn13: String) = withContext(Dispatchers.Main) {
        delay(100)
        booksInWishList.remove(booksInWishList.find { it.isbn13 == isbn13 })
        return@withContext
    }

    override suspend fun searchBooksByKeyword(keyword: String, page: String?): Flow<Triple<List<BookEntity>, String?, Int?>> = withContext(Dispatchers.Main) {
        val response = when {
            keyword == TEST_KEYWORD && page == null -> {
                Gson().fromJson(BookSearchResponseJsonFirstPage, BookSearchResultResponse::class.java)
            }
            else -> null
        }
        flow {
            delay(100)
            if (response != null) {
                emit(Triple(response.books, response.page, response.total.toInt()))
            } else {
                emit(Triple(listOf(), null, null))
            }
        }
    }

}
