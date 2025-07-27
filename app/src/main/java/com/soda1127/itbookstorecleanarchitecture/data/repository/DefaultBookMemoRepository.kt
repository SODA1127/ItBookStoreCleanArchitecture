package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.soda1127.itbookstorecleanarchitecture.data.db.dao.BookMemoDao
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookMemoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBookMemoRepository @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val bookMemoDao: BookMemoDao
): BookMemoRepository {

    override suspend fun getBookMemo(isbn13: String): Flow<BookMemoEntity?> = withContext(ioDispatcher) {
        flow {
            emit(bookMemoDao.get(isbn13))
        }
    }

    override suspend fun saveBookMemo(bookMemoEntity: BookMemoEntity) = withContext(ioDispatcher) {
        bookMemoDao.insert(bookMemoEntity)
    }

}
