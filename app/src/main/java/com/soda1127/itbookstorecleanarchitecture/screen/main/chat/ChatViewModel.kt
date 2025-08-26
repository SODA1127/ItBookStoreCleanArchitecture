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
class ChatViewModel @Inject constructor(
    private val geminiService: GeminiService,
    private val bookStoreRepository: BookStoreRepository
) : BaseViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

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
            _chatState.value = _chatState.value.copy(
                messages = _chatState.value.messages + userMessage,
                isLoading = true
            )

            try {
                // 책 추천 요청인지 확인
                val isBookRecommendationRequest = isBookRecommendationRequest(content)
                
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

                    _chatState.value = _chatState.value.copy(
                        messages = _chatState.value.messages + botMessage,
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

                    _chatState.value = _chatState.value.copy(
                        messages = _chatState.value.messages + botMessage,
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

                _chatState.value = _chatState.value.copy(
                    messages = _chatState.value.messages + errorMessage,
                    isLoading = false
                )
            }
        }
    }

    fun clearChat() {
        _chatState.value = ChatState()
        geminiService.clearHistory()
    }

    private fun isBookRecommendationRequest(content: String): Boolean {
        val bookRecommendationKeywords = listOf(
            "책 추천", "추천해", "추천해줘", "추천해주세요", "추천받고 싶어", "추천받고싶어",
            "책을 추천", "책을 추천해", "책을 추천해줘", "책을 추천해주세요",
            "추천 책", "추천도서", "추천 서적", "추천 문헌",
            "개발 책", "프로그래밍 책", "코딩 책", "IT 책",
            "학습", "공부", "배우고 싶어", "배우고싶어"
        )
        
        return bookRecommendationKeywords.any { keyword ->
            content.contains(keyword, ignoreCase = true)
        }
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
                Pair("🔍 **'$keyword' 관련 추천 도서**\n\n이 책들이 도움이 될 것 같아요! 더 자세한 정보가 필요하시면 말씀해 주세요.", books)
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
