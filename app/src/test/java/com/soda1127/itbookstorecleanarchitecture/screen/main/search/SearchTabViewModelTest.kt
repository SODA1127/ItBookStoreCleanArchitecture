package com.soda1127.itbookstorecleanarchitecture.screen.main.search

import com.google.gson.Gson
import com.soda1127.itbookstorecleanarchitecture.data.json.BookSearchResponseJsonFirstPage
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookSearchRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.TestBookSearchRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.TestBookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.response.BookSearchResultResponse
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import dev.olog.flow.test.observer.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal class SearchTabViewModelTest : JUnit5Test() {

    private lateinit var sut: SearchTabViewModel

    private lateinit var bookStoreRepository: BookStoreRepository

    private lateinit var bookSearchRepository: BookSearchRepository

    private val firstSearchResult =
        Gson().fromJson(BookSearchResponseJsonFirstPage, BookSearchResultResponse::class.java)

    private val searchKeyword = TestBookStoreRepository.TEST_KEYWORD

    private val searchResultModelList = mutableListOf<BookModel>()

    @BeforeEach
    override fun setup() {
        super.setup()
        bookStoreRepository = TestBookStoreRepository()
        bookSearchRepository = TestBookSearchRepository()
        sut = SearchTabViewModel(bookStoreRepository, bookSearchRepository)
    }

    @Test
    fun `Test Search Result`() =
        runTest(UnconfinedTestDispatcher()) {
            sut.stateFlow.test(this) {
                searchResultModelList.addAll(
                    firstSearchResult.books.map { book ->
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
                    }
                )
                assertValues(
                    SearchTabState.Uninitialized,
                    SearchTabState.Loading,
                    SearchTabState.Success.SearchResult(
                        searchResultModelList,
                        searchKeyword,
                        firstSearchResult.page,
                        firstSearchResult.total.toInt()
                    )
                )
            }
            sut.searchByKeyword(searchKeyword)
        }
}
