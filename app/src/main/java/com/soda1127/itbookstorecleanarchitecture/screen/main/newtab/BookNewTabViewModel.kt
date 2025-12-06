package com.soda1127.itbookstorecleanarchitecture.screen.main.newtab

import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class BookNewTabViewModel @Inject constructor(
    private val bookStoreRepository: BookStoreRepository
) : BaseViewModel<NewTabState>() {

    override fun getInitialState(): NewTabState = NewTabState.Uninitialized

    override fun fetchData(): Job =
        viewModelScope.launch {
            setState(NewTabState.Loading)
            try {
                // Combine API data with Wishlist data for real-time like status
                combine(
                    bookStoreRepository.getNewBooks(),
                    bookStoreRepository.getBooksInWishList()
                ) { newBooks, wishlist ->
                    newBooks.map { book ->
                        BookModel(
                            id = book.isbn13,
                            title = book.title,
                            subtitle = book.subtitle,
                            isbn13 = book.isbn13,
                            price = book.price,
                            image = book.image,
                            url = book.url,
                            isLiked = wishlist.any { it.isbn13 == book.isbn13 }
                        )
                    }
                }.collect { bookModels ->
                    setState(NewTabState.Success(bookModels))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                setState(NewTabState.Error(e))
            }
        }

    fun toggleLikeButton(book: BookModel) {
        viewModelScope.launch {
            if (book.isLiked == true) {
                bookStoreRepository.removeBookInWishList(book.isbn13)
            } else {
                bookStoreRepository.addBookInWishList(
                    BookEntity(
                        title = book.title,
                        subtitle = book.subtitle,
                        isbn13 = book.isbn13,
                        price = book.price,
                        image = book.image,
                        url = book.url
                    )
                )
            }
        }
    }

}
