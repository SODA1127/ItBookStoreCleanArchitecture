package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.soda1127.itbookstorecleanarchitecture.data.db.dao.SearchHistoryDao
import com.soda1127.itbookstorecleanarchitecture.data.entity.SearchHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBookSearchRepository @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
): BookSearchRepository {

    override suspend fun saveSearchHistory(searchHistoryEntity: SearchHistoryEntity) = withContext(Dispatchers.IO) {
        searchHistoryDao.insert(searchHistoryEntity)
    }

    override suspend fun getAllSearchHistories(): Flow<List<SearchHistoryEntity>> = withContext(Dispatchers.IO) {
        flow {
            emit(searchHistoryDao.getAll())
        }
    }

    override suspend fun getSearchHistory(keyword: String): Flow<SearchHistoryEntity?> = withContext(Dispatchers.IO) {
        flow {
            emit(searchHistoryDao.get(keyword))
        }
    }

    override suspend fun deleteSearchHistory(keyword: String) = withContext(Dispatchers.IO) {
        searchHistoryDao.delete(keyword)
    }
}
