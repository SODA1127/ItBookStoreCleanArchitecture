package com.soda1127.itbookstorecleanarchitecture.screen.main.newtab

import com.google.gson.Gson
import com.soda1127.itbookstorecleanarchitecture.data.json.NewBooksResponseJson
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.response.BookStoreNewResponse
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import dev.olog.flow.test.observer.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.util.stream.Stream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal class NewTabViewModelTest : JUnit5Test() {

    private lateinit var sut: BookNewTabViewModel

    @MockK
    private lateinit var bookStoreRepository: BookStoreRepository

    @BeforeEach
    override fun setup() {
        super.setup()
        every { bookStoreRepository.observeBookmarkStatus() } returns flow {}
        coEvery { bookStoreRepository.getBooksInWishList() } returns flowOf(emptyList())
        sut = BookNewTabViewModel(bookStoreRepository)
    }

    private fun mockGetNewBooksSucceed() {
        coEvery { bookStoreRepository.getNewBooks() } returns flow {
            delay(100)
            emit(
                Gson().fromJson(NewBooksResponseJson, BookStoreNewResponse::class.java).books
            )
        }
    }

    private fun mockGetNewBooksFailed(): Exception {
        coEvery { bookStoreRepository.getNewBooks() } coAnswers {
            delay(100)
            throw exception
        }
        return exception
    }

    @Test
    fun `fetch Book List succeed`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        mockGetNewBooksSucceed()

        val expectedStateList = listOf(
            NewTabState.Uninitialized, NewTabState.Loading, NewTabState.Success(
                Gson().fromJson(
                    NewBooksResponseJson, BookStoreNewResponse::class.java
                ).books.map { book ->
                    BookModel(
                        id = book.isbn13,
                        title = book.title,
                        subtitle = book.subtitle,
                        isbn13 = book.isbn13,
                        price = book.price,
                        image = book.image,
                        url = book.url,
                        isLiked = false
                    )
                })
        )

        // Then
        sut.stateFlow.test(TestScope()) { assertValues(expectedStateList) }

        // When
        sut.fetchData()
    }

    @Test
    fun `fetch Book List failed`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val exception = mockGetNewBooksFailed()

        val expectedStateList = listOf(
            NewTabState.Uninitialized, NewTabState.Loading, NewTabState.Error(exception)
        )

        // Then
        sut.stateFlow.test(TestScope()) { assertValues(expectedStateList) }

        // When
        sut.fetchData()
    }

    @ParameterizedTest
    @MethodSource("parameterizedTestData")
    fun `fetch Book List`(testType: TestType, expectedStateList: List<NewTabState>) = runTest(UnconfinedTestDispatcher()) {
        triageTestCaseBy(testType)

        sut.stateFlow.test(TestScope()) { assertValues(expectedStateList) }

        sut.fetchData()
    }

    private fun triageTestCaseBy(testType: TestType) {
        when (testType) {
            TestType.SUCCEED -> mockGetNewBooksSucceed()
            TestType.FAILED -> mockGetNewBooksFailed()
        }
    }

    enum class TestType {
        SUCCEED, FAILED
    }

    companion object {

        val exception = Exception("Network Error")

        @JvmStatic
        fun parameterizedTestData(): Stream<Arguments?>? {
            val suceedCase = listOf(
                NewTabState.Uninitialized, NewTabState.Loading, NewTabState.Success(
                    Gson().fromJson(
                        NewBooksResponseJson, BookStoreNewResponse::class.java
                    ).books.map { book ->
                        BookModel(
                            id = book.isbn13,
                            title = book.title,
                            subtitle = book.subtitle,
                            isbn13 = book.isbn13,
                            price = book.price,
                            image = book.image,
                            url = book.url,
                            isLiked = false
                        )
                    })
            )
            val failedCase = listOf(
                NewTabState.Uninitialized, NewTabState.Loading, NewTabState.Error(e = exception)
            )
            return Stream.of(
                Arguments.of(TestType.SUCCEED, suceedCase),
                Arguments.of(TestType.FAILED, failedCase),
            )
        }
    }
}
