package com.soda1127.itbookstorecleanarchitecture.screen.main.newtab

import com.google.gson.Gson
import com.soda1127.itbookstorecleanarchitecture.data.json.NewBooksResponseJson
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.TestBookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.response.BookStoreNewResponse
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import dev.olog.flow.test.observer.test
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal class NewTabViewModelTest: JUnit5Test() {

    private lateinit var sut: BookNewTabViewModel

    @MockK
    private lateinit var bookStoreRepository: BookStoreRepository

    @BeforeEach
    override fun setup() {
        super.setup()
        sut = BookNewTabViewModel(bookStoreRepository)
    }

    private fun mockGetNewBooks() {
        coEvery { bookStoreRepository.getNewBooks() } returns flow {
            delay(100)
            emit(
                Gson().fromJson(
                    NewBooksResponseJson,
                    BookStoreNewResponse::class.java
                ).books
            )
        }
    }

    @Test
    fun `Test fetch Book List`() = runTest(UnconfinedTestDispatcher()) {
        mockGetNewBooks()

        val expectedStateList = listOf(
            NewTabState.Uninitialized,
            NewTabState.Loading,
            NewTabState.Success(
                Gson().fromJson(NewBooksResponseJson, BookStoreNewResponse::class.java).books.map { book ->
                    BookModel(
                        id = book.isbn13,
                        title = book.title,
                        subtitle = book.subtitle,
                        isbn13 = book.isbn13,
                        price = book.price,
                        image = book.image,
                        url = book.url
                    )
                }
            )
        )

        sut.newTabStateFlow.test(TestScope()) {
            assertValues(
                expectedStateList
            )
        }
        sut.fetchData()
    }
    
}
