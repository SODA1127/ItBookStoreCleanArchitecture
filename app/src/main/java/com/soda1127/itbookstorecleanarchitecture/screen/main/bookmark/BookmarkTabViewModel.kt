package com.soda1127.itbookstorecleanarchitecture.screen.main.bookmark

import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.model.CellType
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseViewModel
import com.soda1127.itbookstorecleanarchitecture.screen.base.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkTabViewModel @Inject constructor(
    private val bookStoreRepository: BookStoreRepository
) : BaseViewModel<BookmarkState, Event>() {

    override fun getInitialState(): BookmarkState = BookmarkState.Uninitialized

    init {
        viewModelScope.launch {
            bookStoreRepository.observeBookmarkStatus().collect { (isLiked, isbn) ->
                withState<BookmarkState.Success> { state ->
                    val updatedList = state.modelList.map { bookModel ->
                        if (bookModel.isbn13 == isbn) {
                            bookModel.copy(isLiked = isLiked)
                        } else {
                            bookModel
                        }
                    }.filter { it.isLiked == true }
                    setState(
                        state.copy(
                            modelList = updatedList
                        )
                    )
                }
            }
        }
    }

    override fun fetchData(): Job = viewModelScope.launch {
        setState(
            BookmarkState.Loading
        )
        bookStoreRepository.getBooksInWishList().collect { bookList ->
            setState(
                BookmarkState.Success(
                    bookList.map { book ->
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
    }

    fun toggleLikeButton(bookModel: BookModel) = viewModelScope.launch {
        try {
            withState<BookmarkState.Success> { state ->
                if (bookModel.isLiked == true) {
                    bookStoreRepository.removeBookInWishList(bookModel.isbn13)
                } else {
                    bookStoreRepository.addBookInWishList(bookModel.toEntity())
                }
                setState(
                    state.copy(
                        modelList = state.modelList.toMutableList().apply {
                            set(
                                this.indexOf(bookModel),
                                bookModel.copy(
                                    isLiked = bookModel.isLiked?.not()
                                )
                            )
                        }.toList()
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
