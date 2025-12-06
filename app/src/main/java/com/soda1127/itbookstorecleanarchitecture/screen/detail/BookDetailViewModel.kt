package com.soda1127.itbookstorecleanarchitecture.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookMemoEntity
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookMemoRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseViewModel
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
) : BaseViewModel<BookDetailState, BookDetailEvent>() {

    override fun getInitialState(): BookDetailState = BookDetailState.Uninitialized

    private val isbn13 by lazy { savedStateHandle.get<String>("isbn13") }

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
            withState<BookDetailState.Success> { state ->
                if (state.isLiked) {
                    bookStoreRepository.removeBookInWishList(state.bookInfoEntity.isbn13)
                } else {
                    bookStoreRepository.addBookInWishList(state.bookInfoEntity.toBookEntity())
                }
                setState(
                    state.copy(
                        isLiked = state.isLiked.not()
                    )
                )
                sendEvent(
                    BookDetailEvent.ShowToast(
                        message =
                            if (state.isLiked) "위시리스트에서 제거되었습니다."
                            else "위시리스트에 추가되었습니다."
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setState(
                BookDetailState.Error.Default(e)
            )
        }
    }

    fun saveMemo(memo: String) = viewModelScope.launch {
        val isbn13 = isbn13 ?: run {
            sendEvent(
                BookDetailEvent.ShowToast(message = "저장할 수 없습니다")
            )
            return@launch
        }
        bookMemoRepository.saveBookMemo(
            BookMemoEntity(
                isbn13,
                memo
            )
        )
        sendEvent(
            BookDetailEvent.ShowToast(message = "메모가 저장되었습니다.")
        )
        setState(
            BookDetailState.SaveMemo
        )
    }

}
