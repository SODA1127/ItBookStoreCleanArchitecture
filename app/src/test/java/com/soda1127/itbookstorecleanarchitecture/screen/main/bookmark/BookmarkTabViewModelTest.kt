package com.soda1127.itbookstorecleanarchitecture.screen.main.bookmark


import com.soda1127.itbookstorecleanarchitecture.model.CellType
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.TestBookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import dev.olog.flow.test.observer.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal class BookmarkTabViewModelTest : JUnit5Test() {

    private lateinit var bookStoreRepository: BookStoreRepository

    private lateinit var sut: BookmarkTabViewModel

    private val wishList = mutableListOf<BookEntity>()

    private lateinit var bookModel: BookModel

    @BeforeEach
    override fun setup() {
        super.setup()
        bookStoreRepository = TestBookStoreRepository()
        sut = BookmarkTabViewModel(bookStoreRepository)
    }

    @BeforeEach
    fun `Insert Test Data`() = runTest(UnconfinedTestDispatcher()) {
        wishList.clear()
        bookStoreRepository.getBookInfo(TestBookStoreRepository.TEST_ISBN13).collect { bookInfo ->
            bookInfo?.let { book ->
                bookStoreRepository.addBookInWishList(book.toBookEntity())
                wishList.add(book.toBookEntity())
                bookModel = BookModel(
                    id = book.isbn13,
                    type = CellType.BOOKMARK_CELL,
                    title = book.title,
                    subtitle = book.subtitle,
                    isbn13 = book.isbn13,
                    price = book.price,
                    image = book.image,
                    url = book.url,
                    isLiked = true
                )
            }
        }
    }

    @Test
    fun `Test data exist in wish list`() = runTest(UnconfinedTestDispatcher()) {
        sut.bookmarkStateFlow.test(this) {
            assertValues(
                BookmarkState.Uninitialized,
                BookmarkState.Loading,
                BookmarkState.Success(
                    wishList.map { book ->
                        BookModel(
                            id = book.isbn13,
                            type = CellType.BOOKMARK_CELL,
                            title = book.title,
                            subtitle = book.subtitle,
                            isbn13 = book.isbn13,
                            price = book.price,
                            image = book.image,
                            url = book.url,
                            isLiked = true
                        )
                    },
                )
            )
        }
        sut.fetchData()
    }

    @Test
    fun `Test Book Model toggle like button`() = runTest(UnconfinedTestDispatcher()) {
        sut.bookmarkStateFlow.test(this) {
            assertValues(
                BookmarkState.Uninitialized,
                BookmarkState.Loading,
                BookmarkState.Success(listOf(bookModel)),
                BookmarkState.Success(listOf(bookModel.copy(isLiked = false))),
            )
        }
        sut.fetchData()
        delay(1000)
        sut.toggleLikeButton(bookModel)
    }

}
