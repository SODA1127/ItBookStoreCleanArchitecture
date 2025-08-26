package com.soda1127.itbookstorecleanarchitecture.screen.main.chat

import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.data.remote.GeminiService
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class ChatViewModel @Inject constructor(
    private val geminiService: GeminiService,
    private val bookStoreRepository: BookStoreRepository
) : BaseViewModel() {

    private val _chatStateFlow = MutableStateFlow(ChatState())
    open val chatStateFlow: StateFlow<ChatState> = _chatStateFlow.asStateFlow()

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        // 사용자 메시지 추가
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            _chatStateFlow.value = _chatStateFlow.value.copy(
                messages = _chatStateFlow.value.messages + userMessage,
                isLoading = true
            )

            try {
                // 책 추천 요청인지 확인
                val isBookRecommendationRequest = geminiService.checkUserWantToFindTheITBooks(content)
                
                if (isBookRecommendationRequest) {
                    // 책 추천 요청인 경우
                    val keyword = geminiService.extractBookKeyword(content)
                    val (bookRecommendationText, books) = getBookRecommendation(keyword)
                    
                    val botMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = bookRecommendationText,
                        isFromUser = false,
                        timestamp = System.currentTimeMillis(),
                        books = books
                    )

                    _chatStateFlow.value = _chatStateFlow.value.copy(
                        messages = _chatStateFlow.value.messages + botMessage,
                        isLoading = false
                    )
                } else {
                    // 일반 채팅인 경우
                    val response = geminiService.sendMessage(content)
                    
                    val botMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = response,
                        isFromUser = false,
                        timestamp = System.currentTimeMillis()
                    )

                    _chatStateFlow.value = _chatStateFlow.value.copy(
                        messages = _chatStateFlow.value.messages + botMessage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // 에러 처리
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "죄송합니다. 오류가 발생했습니다: ${e.message}",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                )

                _chatStateFlow.value = _chatStateFlow.value.copy(
                    messages = _chatStateFlow.value.messages + errorMessage,
                    isLoading = false
                )
            }
        }
    }

    fun clearChat() {
        _chatStateFlow.value = ChatState()
        geminiService.clearHistory()
    }


    private suspend fun getBookRecommendation(keyword: String): Pair<String, List<BookEntity>> {
        return try {
            var books = listOf<BookEntity>()
            
            bookStoreRepository.searchBooksByKeyword(keyword, null).collect { (bookList, _, _) ->
                if (bookList.isNotEmpty()) {
                    books = bookList.take(5)
                }
            }
            
            if (books.isNotEmpty()) {
                // 각 책의 요약 생성
                val bookSummaries = mutableListOf<String>()
                for (book in books) {
                    val summary = geminiService.generateBookSummary(book.title, book.url)
                    bookSummaries.add("📚 **${book.title}**\n$summary")
                }
                
                val summaryText = bookSummaries.joinToString("\n\n")
                val message = "🔍 **'$keyword' 관련 추천 도서**\n\n$summaryText\n\n이 책들이 도움이 될 것 같아요! 더 자세한 정보가 필요하시면 말씀해 주세요."
                
                Pair(message, books)
            } else {
                Pair("죄송합니다. '$keyword' 관련 도서를 찾을 수 없습니다. 다른 키워드로 다시 시도해 보세요.", emptyList())
            }
        } catch (e: Exception) {
            Pair("죄송합니다. 도서 검색 중 오류가 발생했습니다: ${e.message}", emptyList())
        }
    }
}

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val books: List<BookEntity>? = null
)
