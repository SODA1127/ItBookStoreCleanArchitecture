package com.soda1127.itbookstorecleanarchitecture.data.repository

import com.soda1127.itbookstorecleanarchitecture.data.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

interface BookSearchRepository {

    suspend fun saveSearchHistory(searchHistoryEntity: SearchHistoryEntity)

    suspend fun getAllSearchHistories(): Flow<List<SearchHistoryEntity>>

    suspend fun getSearchHistory(keyword: String): Flow<SearchHistoryEntity?>

    suspend fun deleteSearchHistory(keyword: String)

}
