package com.soda1127.itbookstorecleanarchitecture.screen.main.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.data.remote.GeminiService
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
    private val geminiService: GeminiService
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
                // Gemini API 호출
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
}

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long
)
