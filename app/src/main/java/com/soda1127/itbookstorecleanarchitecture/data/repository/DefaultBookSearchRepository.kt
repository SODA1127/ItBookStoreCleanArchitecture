package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.soda1127.itbookstorecleanarchitecture.data.db.dao.SearchHistoryDao
import com.soda1127.itbookstorecleanarchitecture.data.entity.SearchHistoryEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBookSearchRepository @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val searchHistoryDao: SearchHistoryDao
): BookSearchRepository {

    override suspend fun saveSearchHistory(searchHistoryEntity: SearchHistoryEntity) = withContext(ioDispatcher) {
        searchHistoryDao.insert(searchHistoryEntity)
    }

    override suspend fun getAllSearchHistories(): Flow<List<SearchHistoryEntity>> = withContext(ioDispatcher) {
        flow {
            emit(searchHistoryDao.getAll())
        }
    }

    override suspend fun getSearchHistory(keyword: String): Flow<SearchHistoryEntity?> = withContext(ioDispatcher) {
        flow {
            emit(searchHistoryDao.get(keyword))
        }
    }

    override suspend fun deleteSearchHistory(keyword: String) = withContext(ioDispatcher) {
        searchHistoryDao.delete(keyword)
    }
}
