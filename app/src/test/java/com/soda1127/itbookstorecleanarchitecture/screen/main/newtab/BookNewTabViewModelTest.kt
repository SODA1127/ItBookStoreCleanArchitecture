package com.soda1127.itbookstorecleanarchitecture.screen.main.newtab

import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BookNewTabViewModelTest : JUnit5Test() {

    @MockK
    private lateinit var bookStoreRepository: BookStoreRepository

    private lateinit var viewModel: BookNewTabViewModel

    @BeforeEach
    override fun setup() {
        super.setup()
        viewModel = BookNewTabViewModel(bookStoreRepository)
    }

    @Test
    fun `fetchData combines API result and Wishlist to set isLiked correctly`() = runTest {
        // Given
        val book1 =
            BookEntity(
                title = "Title1",
                subtitle = "Subtitle1",
                isbn13 = "111",
                price = "$10",
                image = "img1",
                url = "url1"
            )
        val book2 =
            BookEntity(
                title = "Title2",
                subtitle = "Subtitle2",
                isbn13 = "222",
                price = "$20",
                image = "img2",
                url = "url2"
            )

        coEvery { bookStoreRepository.getNewBooks() } returns flowOf(listOf(book1, book2))
        coEvery { bookStoreRepository.getBooksInWishList() } returns
            flowOf(listOf(book1)) // Book1 is liked

        // When
        viewModel.fetchData()

        // Then
        // We need to wait for flow emission. Since using UnconfinedTestDispatcher (from
        // JUnit5Test), it should happen immediately.
        val state = viewModel.stateFlow.value
        assertTrue(state is NewTabState.Success)
        val modelList = (state as NewTabState.Success).modelList

        assertEquals(2, modelList.size)
        // Book1 should be liked
        assertEquals("111", modelList[0].isbn13)
        assertEquals(true, modelList[0].isLiked)

        // Book2 should NOT be liked
        assertEquals("222", modelList[1].isbn13)
        assertEquals(false, modelList[1].isLiked)
    }

    @Test
    fun `toggleLikeButton adds to wishlist if not liked`() = runTest {
        // Given
        val bookModel =
            BookModel(
                id = "111",
                title = "Title1",
                subtitle = "Subtitle1",
                isbn13 = "111",
                price = "$10",
                image = "img1",
                url = "url1",
                isLiked = false
            )

        coEvery { bookStoreRepository.addBookInWishList(any()) } returns Unit

        // When
        viewModel.toggleLikeButton(bookModel)

        // Then
        // Allow some time for coroutine to start (even with Unconfined, launch happens)
        coVerify(timeout = 1000) { bookStoreRepository.addBookInWishList(any()) }
    }

    @Test
    fun `toggleLikeButton removes from wishlist if liked`() = runTest {
        // Given
        val bookModel =
            BookModel(
                id = "111",
                title = "Title1",
                subtitle = "Subtitle1",
                isbn13 = "111",
                price = "$10",
                image = "img1",
                url = "url1",
                isLiked = true
            )

        coEvery { bookStoreRepository.removeBookInWishList(any()) } returns Unit

        // When
        viewModel.toggleLikeButton(bookModel)

        // Then
        coVerify(timeout = 1000) { bookStoreRepository.removeBookInWishList("111") }
    }
}
