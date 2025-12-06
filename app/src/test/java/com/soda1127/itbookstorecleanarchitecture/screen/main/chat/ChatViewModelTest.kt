package com.soda1127.itbookstorecleanarchitecture.screen.main.chat

import com.soda1127.itbookstorecleanarchitecture.data.ai.GeminiService
import com.soda1127.itbookstorecleanarchitecture.data.ai.TestGeminiServiceImpl
import com.soda1127.itbookstorecleanarchitecture.data.ai.TestGeminiServiceImpl.Companion.TEST_MESSAGE
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.TestBookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailState
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

@ExperimentalCoroutinesApi
class ChatViewModelTest: JUnit5Test() {

    private lateinit var sut: ChatViewModel

    private lateinit var bookStoreRepository: BookStoreRepository

    private lateinit var geminiService: GeminiService

    @BeforeEach
    override fun setup() {
        super.setup()
        bookStoreRepository = TestBookStoreRepository()
        geminiService = mockk<GeminiService>(relaxed = true) // MockK 사용

        sut = ChatViewModel(
            geminiService = geminiService,
            bookStoreRepository = bookStoreRepository
        )
    }

    @Test
    fun `test initial state`() = runTest(UnconfinedTestDispatcher()) {
        assert(sut.chatStateFlow.firstOrNull()?.messages?.isEmpty() == true)
        assert(sut.chatStateFlow.firstOrNull()?.isLoading == false)
    }

    @Test
    fun `test sendMessage calls extractBookKeyword when IT book request`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testMessage = "안드로이드 개발 책 추천해주세요"
        
        // Mock 설정
        coEvery { geminiService.checkUserWantToFindTheITBooks(testMessage) } returns true
        coEvery { geminiService.extractBookKeyword(testMessage) } returns "android"
        coEvery { geminiService.generateBookSummary(any(), any()) } returns "테스트 요약"
        coEvery { bookStoreRepository.searchBooksByKeyword(any(), any()) } returns flow {
            emit(Triple(listOf(), null, null))
        }

        // When
        sut.sendMessage(testMessage)

        // Then
        coVerify { 
            geminiService.checkUserWantToFindTheITBooks(testMessage)
            geminiService.extractBookKeyword(testMessage)
        }
    }

    @Test
    fun `test sendMessage calls sendMessage when not IT book request`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testMessage = "안녕하세요"
        
        // Mock 설정
        coEvery { geminiService.checkUserWantToFindTheITBooks(testMessage) } returns false
        coEvery { geminiService.sendMessage(testMessage) } returns "안녕하세요! IT 도서 추천을 도와드릴게요."

        // When
        sut.sendMessage(testMessage)

        // Then
        coVerify { 
            geminiService.checkUserWantToFindTheITBooks(testMessage)
            geminiService.sendMessage(testMessage)
        }
    }

    @Test
    fun `test sendMessage adds user message to state`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testMessage = "안녕하세요"
        
        // Mock 설정
        coEvery { geminiService.checkUserWantToFindTheITBooks(testMessage) } returns false
        coEvery { geminiService.sendMessage(testMessage) } returns "안녕하세요! IT 도서 추천을 도와드릴게요."

        // When
        sut.sendMessage(testMessage)

        // Then
        val finalState = sut.chatStateFlow.first()
        assert(finalState.messages.isNotEmpty())
        assert(finalState.messages.first().content == testMessage)
        assert(finalState.messages.first().isFromUser == true)
    }

    @Test
    fun `test sendMessage adds bot response to state`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testMessage = TEST_MESSAGE
        val botResponse = "안녕하세요! IT 도서 추천을 도와드릴게요."
        
        // Mock 설정
        coEvery { geminiService.checkUserWantToFindTheITBooks(testMessage) } returns true
        coEvery { geminiService.sendMessage(testMessage) } returns botResponse

        // Then
        coVerify { geminiService.extractBookKeyword(testMessage) }

        sut.sendMessage(testMessage)

        /*// When
        sut.sendMessage(testMessage)

        // Then
        val finalState = sut.chatStateFlow.first()
        assert(finalState.messages.size == 2) // 사용자 메시지 + 봇 응답
        assert(finalState.messages.last().content == botResponse)
        assert(finalState.messages.last().isFromUser == false)*/
    }

   /* @Test
    fun `test clearChat resets state and calls clearHistory`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testMessage = "안녕하세요"
        coEvery { geminiService.checkUserWantToFindTheITBooks(testMessage) } returns false
        coEvery { geminiService.sendMessage(testMessage) } returns "안녕하세요!"
        
        sut.sendMessage(testMessage)
        
        // When
        sut.clearChat()

        // Then
        val finalState = sut.chatStateFlow.first()
        assert(finalState.messages.isEmpty())
        assert(finalState.isLoading == false)
        
        verify { geminiService.clearHistory() }
    }

    @Test
    fun `test sendMessage with empty content does nothing`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val emptyMessage = ""

        // When
        sut.sendMessage(emptyMessage)

        // Then
        val finalState = sut.chatStateFlow.first()
        assert(finalState.messages.isEmpty())
        
        coVerify(exactly = 0) { 
            geminiService.checkUserWantToFindTheITBooks(any())
            geminiService.sendMessage(any())
        }
    }

    @Test
    fun `test sendMessage with blank content does nothing`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val blankMessage = "   "

        // When
        sut.sendMessage(blankMessage)

        // Then
        val finalState = sut.chatStateFlow.first()
        assert(finalState.messages.isEmpty())
        
        coVerify(exactly = 0) { 
            geminiService.checkUserWantToFindTheITBooks(any())
            geminiService.sendMessage(any())
        }
    }*/
}
