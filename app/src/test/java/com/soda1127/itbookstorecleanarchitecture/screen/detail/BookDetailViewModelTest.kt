package com.soda1127.itbookstorecleanarchitecture.screen.detail

import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
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
internal class BookDetailViewModelTest: JUnit5Test() {

    private lateinit var sut: BookDetailViewModel

    private lateinit var bookStoreRepository: BookStoreRepository

    @MockK
    private lateinit var bookMemoRepository: BookMemoRepository

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private val testMemoText = "testMemoText"
    private val testBookEntity = Gson().fromJson(BookInfoResponseJson, BookInfoResponse::class.java).toEntity()

    @BeforeEach
    override fun setup() {
        super.setup()
        bookStoreRepository = TestBookStoreRepository()
        every { savedStateHandle.get<String>(KEY_ISBN13) } returns TestBookStoreRepository.TEST_ISBN13
        sut = BookDetailViewModel(bookStoreRepository, bookMemoRepository, savedStateHandle)
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

    @Test
    fun `Test Fetch Data`() = runTest(UnconfinedTestDispatcher()) {
        setupMockBookMemoRepositoryReturnsNull()
        sut.bookDetailStateFlow.test(this) {
            assertValues(
                BookDetailState.Uninitialized,
                BookDetailState.Loading,
                BookDetailState.Success(
                    testBookEntity,
                    true,
                    ""
                )
            )
        }
        sut.fetchData()
    }

    @Test
    fun `Test Toggle Bookmark`() = runTest(UnconfinedTestDispatcher()) {
        setupMockBookMemoRepositoryReturnsNull()
        sut.bookDetailStateFlow.test(this) {
            assertValues(
                BookDetailState.Uninitialized,
                BookDetailState.Loading,
                BookDetailState.Success(
                    testBookEntity,
                    true,
                    ""
                ),
                BookDetailState.Success(
                    testBookEntity,
                    false,
                    ""
                )
            )
        }
        sut.fetchData()
        delay(100)
        sut.toggleLikeButton()
    }

    @Test
    fun `Test Save Memo`() = runTest(UnconfinedTestDispatcher()) {
        setupMockBookMemoRepositoryReturnsNull()
        sut.bookDetailStateFlow.test(this) {
            assertValues(
                BookDetailState.Uninitialized,
                BookDetailState.Loading,
                BookDetailState.Success(
                    testBookEntity,
                    true,
                    ""
                ),
                BookDetailState.SaveMemo,
                BookDetailState.Loading,
                BookDetailState.Success(
                    testBookEntity,
                    true,
                    testMemoText
                )
            )
        }
        sut.fetchData()
        delay(100)

        setupMockBookMemoRepositorySavedMemo()
        sut.saveMemo(testMemoText)

        setupMockBookMemoRepositoryReturnsSavedMemo()
        delay(100)
        sut.fetchData()
    }

}
