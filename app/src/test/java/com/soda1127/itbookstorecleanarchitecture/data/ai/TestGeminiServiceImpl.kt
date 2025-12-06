package com.soda1127.itbookstorecleanarchitecture.data.ai

import com.google.firebase.ai.type.GenerateContentResponse
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookInfoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TestGeminiServiceImpl : GeminiService {

    companion object {
        const val TEST_MESSAGE = "안녕하세요! IT 도서 추천을 받고 싶습니다."
        const val TEST_BOOK_TITLE = "Test Book Title"
        const val TEST_BOOK_URL = "https://test-book-url.com"
        const val TEST_KEYWORD = "android"
        const val TEST_USER_MESSAGE = "안드로이드 개발 관련 책을 추천해주세요"
    }

    private val chatHistory = mutableListOf<String>()

    override suspend fun sendMessage(message: String): String = withContext(Dispatchers.Main) {
        delay(100) // 실제 API 호출 시뮬레이션
        chatHistory.add(message)

        when {
            message.contains("안녕") -> "안녕하세요! IT 도서 추천을 도와드릴게요. 어떤 분야의 책을 찾고 계신가요?"
            message.contains("추천") -> "IT 도서 추천을 도와드리겠습니다. 구체적으로 어떤 분야의 책을 원하시나요? (예: 안드로이드, iOS, 웹개발, 데이터베이스 등)"
            message.contains("감사") -> "도움이 되어서 기쁩니다! 더 궁금한 점이 있으시면 언제든 말씀해 주세요."
            else -> "죄송합니다. IT 도서 관련 질문을 해주시면 더 정확한 답변을 드릴 수 있습니다."
        }
    }

    override fun clearHistory() {
        chatHistory.clear()
    }

    override fun generateSummary(bookInfoEntity: BookInfoEntity): Flow<GenerateContentResponse> = flow {
        TODO()
    }

    override suspend fun generateBookSummary(bookTitle: String, bookUrl: String): String = withContext(Dispatchers.Main) {
        delay(100) // 실제 API 호출 시뮬레이션

        when {
            bookTitle.contains("Android") || bookTitle.contains("안드로이드") ->
                "이 책은 안드로이드 개발의 핵심 개념과 실무 적용 방법을 체계적으로 학습할 수 있는 책입니다. 초보자부터 중급 개발자까지 폭넓게 활용할 수 있습니다."

            bookTitle.contains("Kotlin") || bookTitle.contains("코틀린") ->
                "코틀린 언어의 기본 문법부터 고급 기능까지 다루는 종합적인 가이드입니다. 안드로이드 개발에 코틀린을 적용하고 싶은 개발자들에게 추천합니다."

            bookTitle.contains("Java") ->
                "자바 프로그래밍의 기초부터 객체지향 설계까지 포괄적으로 다루는 책입니다. 안드로이드 개발의 기반이 되는 자바 언어를 체계적으로 학습할 수 있습니다."

            bookTitle.contains("Web") || bookTitle.contains("웹") ->
                "웹 개발의 전반적인 내용을 다루는 실용적인 가이드입니다. 프론트엔드부터 백엔드까지 웹 개발의 모든 영역을 학습할 수 있습니다."

            else -> "이 책은 IT 분야의 중요한 도서로, 실무에 바로 적용할 수 있는 실용적인 정보를 제공합니다. 다양한 독자층에게 유용한 가이드가 될 것입니다."
        }
    }

    override fun generateRatingSummary(bookInfoEntity: BookInfoEntity): Flow<GenerateContentResponse> = flow {
        TODO()
    }

    override suspend fun generateRatingSummaryStr(bookInfoEntity: BookInfoEntity): String = withContext(Dispatchers.Main) {
        delay(100) // 실제 API 호출 시뮬레이션
        "이 책은 독자들로부터 높은 평가를 받고 있으며, 특히 실무에 바로 적용할 수 있는 유용한 정보와 명확한 설명으로 많은 사랑을 받고 있습니다."
    }

    override suspend fun extractBookKeyword(userMessage: String): String = withContext(Dispatchers.Main) {
        delay(100) // 실제 API 호출 시뮬레이션

        when {
            userMessage.contains("안드로이드") || userMessage.contains("Android") -> "android"
            userMessage.contains("iOS") || userMessage.contains("아이폰") -> "ios"
            userMessage.contains("웹") || userMessage.contains("Web") -> "web"
            userMessage.contains("코틀린") || userMessage.contains("Kotlin") -> "kotlin"
            userMessage.contains("자바") || userMessage.contains("Java") -> "java"
            userMessage.contains("파이썬") || userMessage.contains("Python") -> "python"
            userMessage.contains("데이터베이스") || userMessage.contains("Database") -> "database"
            userMessage.contains("알고리즘") || userMessage.contains("Algorithm") -> "algorithm"
            userMessage.contains("머신러닝") || userMessage.contains("Machine Learning") -> "machine-learning"
            userMessage.contains("블록체인") || userMessage.contains("Blockchain") -> "blockchain"
            else -> "programming" // 기본값
        }
    }

    override suspend fun checkUserWantToFindTheITBooks(userMessage: String): Boolean = withContext(Dispatchers.Main) {
        delay(100) // 실제 API 호출 시뮬레이션

        val itBookKeywords = listOf(
            "책", "추천", "도서", "개발", "프로그래밍", "코딩", "IT", "소프트웨어",
            "안드로이드", "iOS", "웹", "앱", "데이터베이스", "알고리즘", "자바", "코틀린",
            "파이썬", "자바스크립트", "리액트", "스프링", "머신러닝", "블록체인"
        )

        itBookKeywords.any { keyword -> userMessage.contains(keyword, ignoreCase = true) }
    }

}
