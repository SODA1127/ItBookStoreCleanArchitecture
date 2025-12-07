package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookInfoEntity
import kotlinx.coroutines.flow.Flow

interface BookStoreRepository {

    fun observeBookmarkStatus(): Flow<Pair<Boolean, String>>

    suspend fun getNewBooks(): Flow<List<BookEntity>>

    suspend fun getBookInfo(isbn13: String): Flow<BookInfoEntity?>

    suspend fun getBooksInWishList(): Flow<List<BookEntity>>

    suspend fun getBookInWishList(isbn13: String): Flow<BookEntity?>

    suspend fun addBookInWishList(bookEntity: BookEntity)

    suspend fun removeBookInWishList(isbn13: String)

    suspend fun searchBooksByKeyword(keyword: String, page: String? = null): Flow<Triple<List<BookEntity>, String?, Int?>>

}
