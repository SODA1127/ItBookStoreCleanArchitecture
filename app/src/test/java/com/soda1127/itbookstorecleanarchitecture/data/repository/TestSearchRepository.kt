package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.soda1127.itbookstorecleanarchitecture.data.entity.SearchHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TestBookSearchRepository: BookSearchRepository {

    private val searchHistoryList = mutableListOf<SearchHistoryEntity>()

    override suspend fun saveSearchHistory(searchHistoryEntity: SearchHistoryEntity) = withContext(Dispatchers.Main) {
        delay(100)
        searchHistoryList.add(searchHistoryEntity)
        return@withContext
    }

    override suspend fun getAllSearchHistories(): Flow<List<SearchHistoryEntity>> {
        delay(100)
        return flow {
            emit(searchHistoryList)
        }
    }

    override suspend fun getSearchHistory(keyword: String): Flow<SearchHistoryEntity?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSearchHistory(keyword: String) {
        TODO("Not yet implemented")
    }

}
