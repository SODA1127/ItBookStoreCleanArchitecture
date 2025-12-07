package com.soda1127.itbookstorecleanarchitecture.screen.detail

import androidx.lifecycle.SavedStateHandle
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookInfoEntity
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookMemoEntity
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookMemoRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.TestBookStoreRepository.Companion.TEST_ISBN13
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BookDetailViewModelTest : JUnit5Test() {

    @MockK
    private lateinit var bookStoreRepository: BookStoreRepository

    @MockK
    private lateinit var bookMemoRepository: BookMemoRepository

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: BookDetailViewModel

    @BeforeEach
    override fun setup() {
        super.setup()
        // Given
        every { savedStateHandle.get<String>("isbn13") } returns TEST_ISBN13
    }

    @Test
    fun `fetchData combines streams correctly`() = runTest {
        val bookInfo =
            BookInfoEntity(
                title = "Title",
                subtitle = "Subtitle",
                isbn13 = TEST_ISBN13,
                price = "$10",
                image = "img",
                url = "url",
                authors = "Authors",
                publisher = "Publisher",
                language = "English",
                isbn10 = "1234567890",
                pages = 100,
                year = 2024,
                rating = 4.5f,
                desc = "Description",
                pdf = null
            )
        val wishListBook = bookInfo.toBookEntity()
        val bookMemo = BookMemoEntity(TEST_ISBN13, "My Memo")

        coEvery { bookStoreRepository.getBookInWishList(TEST_ISBN13) } returns flowOf(wishListBook)
        coEvery { bookStoreRepository.getBookInfo(TEST_ISBN13) } returns flowOf(bookInfo)
        coEvery { bookMemoRepository.getBookMemo(TEST_ISBN13) } returns flowOf(bookMemo)

        // Initialize ViewModel after mocks are ready (since fetchData might be called or we call it
        // manually)
        viewModel = BookDetailViewModel(bookStoreRepository, bookMemoRepository, savedStateHandle)

        // When
        viewModel.fetchData()

        // Then
        val state = viewModel.stateFlow.value
        assertTrue(state is BookDetailState.Success)
        val successState = state as BookDetailState.Success

        assertEquals(bookInfo, successState.bookInfoEntity)
        assertEquals(true, successState.isLiked)
        assertEquals("My Memo", successState.memo)
    }

    @Test
    fun `toggleLikeButton adds to wishlist if not liked`() = runTest {
        // Setup initial success state with isLiked = false
        val bookInfo =
            BookInfoEntity(
                title = "Title",
                subtitle = "Subtitle",
                isbn13 = TEST_ISBN13,
                price = "$10",
                image = "img",
                url = "url",
                authors = "Authors",
                publisher = "Publisher",
                language = "English",
                isbn10 = "1234567890",
                pages = 100,
                year = 2024,
                rating = 4.5f,
                desc = "Description",
                pdf = null
            )

        // Mocking fetchData prerequisites to get into Success state
        coEvery { bookStoreRepository.getBookInWishList(TEST_ISBN13) } returns flowOf(null) // Not in wishlist
        coEvery { bookStoreRepository.getBookInfo(TEST_ISBN13) } returns flowOf(bookInfo)
        coEvery { bookMemoRepository.getBookMemo(TEST_ISBN13) } returns flowOf(null)

        viewModel = BookDetailViewModel(bookStoreRepository, bookMemoRepository, savedStateHandle)
        viewModel.fetchData() // Populate state

        // Mock add action
        coEvery { bookStoreRepository.addBookInWishList(any()) } returns Unit

        // When
        viewModel.toggleLikeButton()

        // Then
        coVerify(timeout = 1000) { bookStoreRepository.addBookInWishList(any()) }
        val state = viewModel.stateFlow.value as BookDetailState.Success
        assertEquals(true, state.isLiked)
    }

    @Test
    fun `toggleLikeButton removes from wishlist if liked`() = runTest {
        val bookInfo =
            BookInfoEntity(
                title = "Title",
                subtitle = "Subtitle",
                isbn13 = TEST_ISBN13,
                price = "$10",
                image = "img",
                url = "url",
                authors = "Authors",
                publisher = "Publisher",
                language = "English",
                isbn10 = "1234567890",
                pages = 100,
                year = 2024,
                rating = 4.5f,
                desc = "Description",
                pdf = null
            )
        val wishListBook = bookInfo.toBookEntity()

        // Mocking fetchData prerequisites to get into Success state (Already Liked)
        coEvery { bookStoreRepository.getBookInWishList(TEST_ISBN13) } returns flowOf(wishListBook)
        coEvery { bookStoreRepository.getBookInfo(TEST_ISBN13) } returns flowOf(bookInfo)
        coEvery { bookMemoRepository.getBookMemo(TEST_ISBN13) } returns flowOf(null)

        viewModel = BookDetailViewModel(bookStoreRepository, bookMemoRepository, savedStateHandle)
        viewModel.fetchData()

        // Mock remove action
        coEvery { bookStoreRepository.removeBookInWishList(TEST_ISBN13) } returns Unit

        // When
        viewModel.toggleLikeButton()

        // Then
        coVerify(timeout = 1000) { bookStoreRepository.removeBookInWishList(TEST_ISBN13) }
        val state = viewModel.stateFlow.value as BookDetailState.Success
        assertEquals(false, state.isLiked)
    }

    @Test
    fun `saveMemo saves to repository and updates state`() = runTest {
        // Mocks for initialization
        coEvery { bookStoreRepository.getBookInWishList(TEST_ISBN13) } returns flowOf(null)
        coEvery { bookStoreRepository.getBookInfo(TEST_ISBN13) } returns
            flowOf(
                BookInfoEntity(
                    isbn13 = TEST_ISBN13,
                    title = "T",
                    subtitle = "",
                    price = "",
                    image = "",
                    url = "",
                    authors = "",
                    publisher = "",
                    language = "",
                    isbn10 = "",
                    pages = 0,
                    year = 0,
                    rating = 0f,
                    desc = "",
                    pdf = null
                )
            )
        coEvery { bookMemoRepository.getBookMemo(TEST_ISBN13) } returns flowOf(null)

        viewModel = BookDetailViewModel(bookStoreRepository, bookMemoRepository, savedStateHandle)
        viewModel.fetchData()

        val memoSlot = slot<BookMemoEntity>()
        coEvery { bookMemoRepository.saveBookMemo(capture(memoSlot)) } returns Unit

        // When
        val memoText = "New Memo"
        viewModel.saveMemo(memoText)

        // Then
        coVerify(timeout = 1000) { bookMemoRepository.saveBookMemo(any()) }
        assertEquals(TEST_ISBN13, memoSlot.captured.isbn13)
        assertEquals(memoText, memoSlot.captured.memo)

        val state = viewModel.stateFlow.value
        assertEquals(BookDetailState.SaveMemo, state)
    }
}
