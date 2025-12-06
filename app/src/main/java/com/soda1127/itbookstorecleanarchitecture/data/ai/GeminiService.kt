package com.soda1127.itbookstorecleanarchitecture.data.ai

import com.google.firebase.ai.type.GenerateContentResponse
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookInfoEntity
import kotlinx.coroutines.flow.Flow

interface GeminiService {

    suspend fun sendMessage(message: String): String

    fun clearHistory()

    fun generateSummary(bookInfoEntity: BookInfoEntity): Flow<GenerateContentResponse>

    suspend fun generateBookSummary(bookTitle: String, bookUrl: String): String

    fun generateRatingSummary(bookInfoEntity: BookInfoEntity): Flow<GenerateContentResponse>

    suspend fun generateRatingSummaryStr(bookInfoEntity: BookInfoEntity): String

    suspend fun extractBookKeyword(userMessage: String): String

    suspend fun checkUserWantToFindTheITBooks(userMessage: String): Boolean

}
