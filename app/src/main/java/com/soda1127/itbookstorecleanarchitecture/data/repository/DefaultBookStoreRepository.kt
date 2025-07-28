package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.soda1127.itbookstorecleanarchitecture.data.db.dao.BookInWishListDao
import com.soda1127.itbookstorecleanarchitecture.data.di.api.BooksApiService
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookInfoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBookStoreRepository @Inject constructor(
    private val booksApiService: BooksApiService,
    private val bookInWishListDao: BookInWishListDao
): BookStoreRepository {

    override suspend fun getNewBooks(): Flow<List<BookEntity>> = withContext(Dispatchers.IO) {
        flow<List<BookEntity>> {
            val booksResponse = booksApiService.getNewBooks()
            if (booksResponse.isSuccessful) {
                emit(booksResponse.body()?.books ?: listOf())
            } else {
                emit(listOf())
            }

        }
    }

    override suspend fun getBookInfo(isbn13: String): Flow<BookInfoEntity?> = withContext(Dispatchers.IO) {
        flow {
            val bookInfoResponse = booksApiService.getBookInfo(isbn13)
            if (bookInfoResponse.isSuccessful) {
                emit(bookInfoResponse.body()?.toEntity())
            } else {
                emit(null)
            }
        }
    }

    override suspend fun getBooksInWishList(): Flow<List<BookEntity>> = withContext(Dispatchers.IO) {
        flow {
            emit(bookInWishListDao.getAll())
        }
    }

    override suspend fun getBookInWishList(isbn13: String): Flow<BookEntity?> = withContext(Dispatchers.IO) {
        flow {
            emit(bookInWishListDao.get(isbn13))
        }
    }

    override suspend fun addBookInWishList(bookEntity: BookEntity) = withContext(Dispatchers.IO) {
        bookInWishListDao.insert(bookEntity)
    }

    override suspend fun removeBookInWishList(isbn13: String) = withContext(Dispatchers.IO) {
        bookInWishListDao.delete(isbn13)
    }

    override suspend fun searchBooksByKeyword(keyword: String, page: String?): Flow<Triple<List<BookEntity>, String?, Int?>> = withContext(Dispatchers.IO) {
        val response = when (page) {
            null -> {
                booksApiService.searchBooksByKeyword(keyword)
            }
            else -> {
                booksApiService.searchBooksByKeywordWithPage(keyword, page)
            }
        }
        flow<Triple<List<BookEntity>, String?, Int?>> {
            if (response.isSuccessful) {
                val body = response.body()
                emit(Triple(body?.books ?: listOf(), body?.page, body?.total?.toInt()))
            } else {
                emit(Triple(listOf(), null, null))
            }
        }
    }

}
