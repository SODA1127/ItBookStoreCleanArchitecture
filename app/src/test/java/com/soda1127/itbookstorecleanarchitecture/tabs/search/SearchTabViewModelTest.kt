package com.soda1127.itbookstorecleanarchitecture.tabs.search

import com.google.gson.Gson
import com.soda1127.itbookstorecleanarchitecture.data.response.BookSearchResultResponse
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.data.response.BookStoreNewResponse
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookSearchRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.TestBookSearchRepository
import com.soda1127.itbookstorecleanarchitecture.response.NEW_BOOKS_RESPONSE
import com.soda1127.itbookstorecleanarchitecture.response.SEARCH_RESULT_ANDROID
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import dev.olog.flow.test.observer.test
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@Suppress("NonAsciiCharacters")
@ExperimentalCoroutinesApi
class SearchTabViewModelTest: JUnit5Test() {

    private lateinit var sut: SearchTabViewModel

    @MockK
    lateinit var searchRepository: BookSearchRepository

    private fun makeBooks() = (0 until 10).map { int ->
        BookEntity(
            isbn13 = int.toString(),
            title = "title $int",
            subtitle = "subtitle $int",
            price = int.toString(),
            image = "",
            url = "",
        )
    }

    private fun createNewResponse(bookList: List<BookEntity>) = BookSearchResultResponse(
        error = "error",
        total = "total",
        page = "0",
        books = bookList
    )

    @BeforeEach
    override fun setup() {
        super.setup()
        sut = SearchTabViewModel(searchRepository)
    }

    @Test
    fun `검색 리스트를 가져올 때 성공적으로 응답을 가져옵니다`() = runTest(UnconfinedTestDispatcher()) {
        // Arrange
        coEvery { searchRepository.searchBooksByKeyword(SEARCH_KEYWORD) } coAnswers {
            delay(100) // network time
            makeBooks()
        }
        val bookList = makeBooks()

        // Assert
        sut.stateFlow.test(TestScope()) {
            assertValues(
                SearchTabState.Uninitialized,
                SearchTabState.Loading,
                SearchTabState.Result(bookList)
            )
        }

        // Act
        sut.searchBooksByKeyword(SEARCH_KEYWORD)
    }

    @Test
    fun check_result_success() = runTest(UnconfinedTestDispatcher()) {
        searchRepository = TestBookSearchRepository()
        sut = SearchTabViewModel(searchRepository)

        sut.stateFlow.test(TestScope()) {
            assertValues(
                SearchTabState.Uninitialized,
                SearchTabState.Loading,
                SearchTabState.Result(listOf())
            )
        }

        sut.searchBooksByKeyword(SEARCH_KEYWORD)
    }

    @Test
    fun `책 검색 결과를 가져올 때 응답을 가져오는데 실패합니다`() = runTest(UnconfinedTestDispatcher()) {
        // Arrange
        val exception = Exception("This error should be occurred in this case")
        coEvery { searchRepository.searchBooksByKeyword(SEARCH_KEYWORD) } coAnswers {
            delay(100) // network time
            throw exception
        }
        sut = SearchTabViewModel(searchRepository)

        // Assert
        sut.stateFlow.test(TestScope()) {
            assertValues(
                SearchTabState.Uninitialized,
                SearchTabState.Loading,
                SearchTabState.Error(exception),
            )
        }

        // Act
        sut.searchBooksByKeyword(SEARCH_KEYWORD)
    }

    private fun parseSearchResult(): List<BookEntity> {
        val response = Gson().fromJson(SEARCH_RESULT_ANDROID, BookSearchResultResponse::class.java)
        return response.books
    }

    @Test
    fun `검색 결과를 가져올 때 성공적으로 응답을 파싱하여 가져옵니다`() = runTest(UnconfinedTestDispatcher()) {
        // Arrange
        val searchResult = parseSearchResult()
        coEvery { searchRepository.searchBooksByKeyword(SEARCH_KEYWORD) } coAnswers {
            delay(100) // network time
            searchResult
        }

        // Assert
        sut.stateFlow.test(TestScope()) {
            assertValues(
                SearchTabState.Uninitialized,
                SearchTabState.Loading,
                SearchTabState.Result(searchResult),
            )
        }

        // Act
        sut.searchBooksByKeyword(SEARCH_KEYWORD)
    }

    @Test
    fun `새 책 리스트를 가져올 때 성공적으로 응답을 가져옵니다`() = runTest(UnconfinedTestDispatcher()) {
        // Arrange
        val bookList = makeBooks()
        coEvery { searchRepository.getNewBooks() } coAnswers {
            delay(100)
            bookList
        }

        // Assert
        sut.stateFlow.test(TestScope()) {
            assertValues(
                SearchTabState.Uninitialized,
                SearchTabState.Loading,
                SearchTabState.Result(bookList)
            )
        }

        // Act
        sut.getNewBooks()
    }

    @Test
    fun `새 책 리스트를 가져올 때 응답을 가져오는데 실패합니다`() = runTest(UnconfinedTestDispatcher()) {
        // Arrange
        val exception = Exception("This error should be occurred in this case")
        coEvery { searchRepository.getNewBooks() } coAnswers {
            delay(100) // network time
            throw exception
        }
        sut = SearchTabViewModel(searchRepository)

        // Assert
        sut.stateFlow.test(TestScope()) {
            assertValues(
                SearchTabState.Uninitialized,
                SearchTabState.Loading,
                SearchTabState.Error(exception),
            )
        }

        // Act
        sut.getNewBooks()

    }

    private fun parseNewBooks(): List<BookEntity> {
        val response = Gson().fromJson(NEW_BOOKS_RESPONSE, BookStoreNewResponse::class.java)
        return response.books
    }

    @Test
    fun `새 책 리스트를 가져올 때 성공적으로 응답을 파싱하여 가져옵니다`() = runTest(UnconfinedTestDispatcher()) {
        // Arrange
        val newBooksResult = parseNewBooks()

        coEvery { searchRepository.getNewBooks() } coAnswers {
            delay(100) // network time
            newBooksResult
        }

        // Assert
        sut.stateFlow.test(TestScope()) {
            assertValues(
                SearchTabState.Uninitialized,
                SearchTabState.Loading,
                SearchTabState.Result(newBooksResult),
            )
        }

        // Act
        sut.getNewBooks()
    }

    @ParameterizedTest
    @MethodSource("parameters")
    fun `a + b = c`(a: Int, b: Int, c: Int) {
        assert(a + b == c)
    }

    companion object {

        @JvmStatic
        fun parameters(): Stream<Arguments> = Stream.of(
            Arguments.of(1, 2, 3),
            Arguments.of(10, 20, 30),
        )

        const val SEARCH_KEYWORD = "android"

    }

}
