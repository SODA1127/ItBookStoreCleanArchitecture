package com.soda1127.itbookstorecleanarchitecture.screen.detail

import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.soda1127.itbookstorecleanarchitecture.data.ai.GeminiService
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookMemoEntity
import com.soda1127.itbookstorecleanarchitecture.data.json.BookInfoResponseJson
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookMemoRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.TestBookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.response.BookInfoResponse
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailActivity.Companion.KEY_ISBN13
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import dev.olog.flow.test.observer.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal class BookDetailViewModelTest : JUnit5Test() {

    private lateinit var sut: BookDetailViewModel

    private lateinit var bookStoreRepository: BookStoreRepository

    @MockK
    private lateinit var bookMemoRepository: BookMemoRepository

    @MockK
    private lateinit var geminiService: GeminiService

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private val testMemoText = "testMemoText"
    private val testBookEntity = Gson().fromJson(BookInfoResponseJson, BookInfoResponse::class.java).toEntity()
    private val bookSummaryText = "Sample book summary text."
    private val ratingSummaryText = "Sample rating summary text."

    @BeforeEach
    override fun setup() {
        super.setup()
        bookStoreRepository = TestBookStoreRepository()
        every { savedStateHandle.get<String>(KEY_ISBN13) } returns TestBookStoreRepository.TEST_ISBN13
        sut = BookDetailViewModel(bookStoreRepository, bookMemoRepository, geminiService, savedStateHandle)
    }

    @BeforeEach
    fun `Insert Test Data`() = runTest(UnconfinedTestDispatcher()) {
        bookStoreRepository.getBookInfo(TestBookStoreRepository.TEST_ISBN13).collect { bookInfo ->
            bookInfo?.let {
                bookStoreRepository.addBookInWishList(it.toBookEntity())
            }
        }
    }

    private fun setupMockBookMemoRepositoryReturnsNull() {
        coEvery { bookMemoRepository.getBookMemo(TestBookStoreRepository.TEST_ISBN13) } returns flow {
            emit(null)
        }
    }

    private fun setupMockBookMemoRepositoryReturnsSavedMemo() {
        coEvery { bookMemoRepository.getBookMemo(TestBookStoreRepository.TEST_ISBN13) } returns flow {
            emit(
                BookMemoEntity(
                    isbn13 = TestBookStoreRepository.TEST_ISBN13,
                    memo = testMemoText
                )
            )
        }
    }

    private fun setupMockBookMemoRepositorySavedMemo() {
        coEvery {
            bookMemoRepository.saveBookMemo(
                BookMemoEntity(
                    isbn13 = TestBookStoreRepository.TEST_ISBN13,
                    memo = testMemoText
                )
            )
        } returns Unit
    }

    private fun generateGeneratingSummaryState() = BookDetailState.Success.SummaryState(
        isSummaryGenerating = true,
        isRatingSummaryGenerating = true
    )

    private fun mockGeminiService() {
        coEvery {
            delay(50)
            geminiService.generateBookSummary(any(), any())
        } returns bookSummaryText
        coEvery {
            delay(50)
            geminiService.generateRatingSummaryStr(any())
        } returns ratingSummaryText
    }

    @Test
    fun `Test Fetch Data`() = runTest(UnconfinedTestDispatcher()) {
        setupMockBookMemoRepositoryReturnsNull()
        mockGeminiService()
        val successState = BookDetailState.Success(
            testBookEntity,
            true,
            "",
            generateGeneratingSummaryState()
        )

        sut.bookDetailStateFlow.test(this) {
            assertValues(
                BookDetailState.Uninitialized,
                BookDetailState.Loading,
                successState,
                successState.copy(
                    summaryState = successState.summaryState.copy(
                        bookSummary = "[내용요약]\n${bookSummaryText}",
                        isSummaryGenerating = false,
                        ratingSummary = "[평점요약]\n${ratingSummaryText}",
                        isRatingSummaryGenerating = false,
                    )
                )
            )
        }
        sut.fetchData()
    }

    @Test
    fun `Test Toggle Bookmark`() = runTest(UnconfinedTestDispatcher()) {
        setupMockBookMemoRepositoryReturnsNull()
        mockGeminiService()
        val successState = BookDetailState.Success(
            testBookEntity,
            true,
            "",
            generateGeneratingSummaryState()
        )
        val summaryStateWithGenerated = successState.copy(
            summaryState = successState.summaryState.copy(
                bookSummary = "[내용요약]\n${bookSummaryText}",
                isSummaryGenerating = false,
                ratingSummary = "[평점요약]\n${ratingSummaryText}",
                isRatingSummaryGenerating = false,
            )
        )
        sut.bookDetailStateFlow.test(this) {
            assertValues(
                BookDetailState.Uninitialized,
                BookDetailState.Loading,
                successState,
                summaryStateWithGenerated,
                summaryStateWithGenerated.copy(
                    isLiked = false
                )
            )
        }
        sut.fetchData()
        delay(500)
        sut.toggleLikeButton()
    }

    @Test
    fun `Test Save Memo`() = runTest(UnconfinedTestDispatcher()) {
        setupMockBookMemoRepositoryReturnsNull()
        mockGeminiService()
        setupMockBookMemoRepositorySavedMemo()

        val successState = BookDetailState.Success(
            testBookEntity,
            true,
            "",
            generateGeneratingSummaryState()
        )
        val summaryStateWithGenerated = successState.copy(
            summaryState = successState.summaryState.copy(
                bookSummary = "[내용요약]\n${bookSummaryText}",
                isSummaryGenerating = false,
                ratingSummary = "[평점요약]\n${ratingSummaryText}",
                isRatingSummaryGenerating = false,
            )
        )

        val successSavedState = BookDetailState.Success(
            testBookEntity,
            true,
            testMemoText,
            generateGeneratingSummaryState()
        )
        val summarySavedStateWithGenerated = successSavedState.copy(
            summaryState = successState.summaryState.copy(
                bookSummary = "[내용요약]\n${bookSummaryText}",
                isSummaryGenerating = false,
                ratingSummary = "[평점요약]\n${ratingSummaryText}",
                isRatingSummaryGenerating = false,
            )
        )
        sut.bookDetailStateFlow.test(this) {
            assertValues(
                BookDetailState.Uninitialized,
                BookDetailState.Loading,
                successState,
                summaryStateWithGenerated,
                BookDetailState.SaveMemo,
                BookDetailState.Loading,
                successSavedState,
                summarySavedStateWithGenerated
            )
        }
        sut.fetchData()
        delay(300)

        sut.saveMemo(testMemoText)

        setupMockBookMemoRepositoryReturnsSavedMemo()
        delay(300)
        sut.fetchData()
    }

}
