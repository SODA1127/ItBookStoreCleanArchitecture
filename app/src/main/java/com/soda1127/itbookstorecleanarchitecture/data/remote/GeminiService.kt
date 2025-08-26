package com.soda1127.itbookstorecleanarchitecture.data.remote

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {

    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")
    private var chat = generativeModel.startChat()

    suspend fun sendMessage(message: String): String = withContext(Dispatchers.IO) {
        try {
            // Firebase AI Logic SDK를 사용하여 채팅 메시지 전송
            val response = chat.sendMessage(message)
            response.text ?: "응답을 생성할 수 없습니다."
        } catch (e: Exception) {
            "오류가 발생했습니다: ${e.message}"
        }
    }

    fun clearHistory() {
        // 새로운 채팅 세션 시작
        chat = generativeModel.startChat()
    }
}
