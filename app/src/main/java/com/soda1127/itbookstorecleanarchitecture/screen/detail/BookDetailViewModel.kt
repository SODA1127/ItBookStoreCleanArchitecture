package com.soda1127.itbookstorecleanarchitecture.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookMemoEntity
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookMemoRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseViewModel
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailActivity.Companion.KEY_ISBN13
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookStoreRepository: BookStoreRepository,
    private val bookMemoRepository: BookMemoRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _bookDetailStateFlow = MutableStateFlow<BookDetailState>(BookDetailState.Uninitialized)
    val bookDetailStateFlow: StateFlow<BookDetailState> = _bookDetailStateFlow

    private val isbn13 by lazy { savedStateHandle.get<String>(KEY_ISBN13) }

    override fun fetchData(): Job = viewModelScope.launch {
        try {
            setState(
                BookDetailState.Loading
            )
            isbn13?.let { isbn13 ->
                combine(
                    bookStoreRepository.getBookInWishList(isbn13),
                    bookStoreRepository.getBookInfo(isbn13),
                    bookMemoRepository.getBookMemo(isbn13)
                ) { bookInfoEntityInWishList, bookInfoEntity, bookMemoEntity ->
                    Triple(bookInfoEntityInWishList, bookInfoEntity, bookMemoEntity)
                }.collect { (bookInfoEntityInWishList, bookInfoEntity, bookMemoEntity) ->
                    if (bookInfoEntity != null) {
                        setState(
                            BookDetailState.Success(
                                bookInfoEntity,
                                bookInfoEntityInWishList != null,
                                bookMemoEntity?.memo ?: ""
                            )
                        )
                    } else {
                        setState(
                            BookDetailState.Error.NotFound
                        )
                    }
                }
            } ?: kotlin.run {
                setState(
                    BookDetailState.Error.NotFound
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setState(
                BookDetailState.Error.Default(e)
            )
        }
    }

    fun toggleLikeButton() = viewModelScope.launch {
        try {
            when (val data = bookDetailStateFlow.value) {
                is BookDetailState.Success -> {
                    if (data.isLiked) {
                        bookStoreRepository.removeBookInWishList(data.bookInfoEntity.isbn13)
                    } else {
                        bookStoreRepository.addBookInWishList(data.bookInfoEntity.toBookEntity())
                    }
                    setState(
                        data.copy(
                            isLiked = data.isLiked.not()
                        )
                    )
                }

                else -> Unit
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setState(
                BookDetailState.Error.Default(e)
            )
        }
    }

    fun saveMemo(memo: String) = viewModelScope.launch {
        isbn13?.let { isbn13 ->
            bookMemoRepository.saveBookMemo(
                BookMemoEntity(
                    isbn13,
                    memo
                )
            )
            setState(
                BookDetailState.SaveMemo
            )
        } ?: kotlin.run {
            setState(
                BookDetailState.SaveMemo
            )
        }
    }

    private fun setState(state: BookDetailState) {
        _bookDetailStateFlow.value = state
    }

}
