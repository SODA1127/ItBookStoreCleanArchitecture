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

    suspend fun extractBookKeyword(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            val prompt = "사용자가 IT 도서를 추천받고 싶어합니다. 다음 메시지에서 IT 도서 검색에 적합한 영어 키워드 하나만 추출해주세요. 추가 설명 없이 영어 키워드만 반환하세요: $userMessage"
            val response = generativeModel.generateContent(prompt)
            response.text?.trim() ?: "programming"
        } catch (e: Exception) {
            "programming" // 기본값
        }
    }
}
