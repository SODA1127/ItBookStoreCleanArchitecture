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
            val prompt = "사용자가 IT 도서를 추천받고 싶어합니다. 다음 메시지에서 IT 도서 검색에 적합한 영어 키워드 하나만 추출해주세요. 추가 설명 없이 영어 한단어만 반환하세요: $userMessage"
            val response = generativeModel.generateContent(prompt)
            response.text?.trim() ?: "programming"
        } catch (e: Exception) {
            "programming" // 기본값
        }
    }

    suspend fun generateBookSummary(bookTitle: String, bookUrl: String): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                다음 IT 도서에 대한 간단한 요약을 한국어로 작성해주세요:
                제목: $bookTitle
                URL: $bookUrl
                
                요구사항:
                1. 2문장이내로 간결하게 요약
                2. 이 책이 어떤 독자에게 유용한지 언급
                3. 주요 학습 내용이나 특징을 포함
                4. 마케팅적 표현보다는 객관적인 설명
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            response.text?.trim() ?: "이 책에 대한 요약을 생성할 수 없습니다."
        } catch (e: Exception) {
            "이 책에 대한 요약을 생성할 수 없습니다."
        }
    }

    suspend fun checkUserWantToFindTheITBooks(userMessage: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                사용자의 메세지 ${userMessage}를 분석해주세요.
                이 메세지의 의도가 IT 도서 추천을 받는 것이라면 'true'를,
                다른 도서 추천, 또는 다른 주제라면 'false'를 한단어로 반환해주세요
            """.trimIndent()
            val response = generativeModel.generateContent(prompt)
            return@withContext response.text?.trim().toBoolean()
        } catch (e: Exception) {
            return@withContext false
        }
    }

}
