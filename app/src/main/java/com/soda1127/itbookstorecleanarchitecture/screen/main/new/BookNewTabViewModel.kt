package com.soda1127.itbookstorecleanarchitecture.screen.main.new

import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseViewModel
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookNewTabViewModel @Inject constructor(
    private val bookStoreRepository: BookStoreRepository
) : BaseViewModel() {

    private val _newTabStateFlow = MutableStateFlow<NewTabState>(NewTabState.Uninitialized)
    val newTabStateFlow: StateFlow<NewTabState> = _newTabStateFlow

    override fun fetchData(): Job = viewModelScope.launch {
        setState(
            NewTabState.Loading
        )
        try {
            bookStoreRepository.getNewBooks().collect { bookList ->
                setState(
                    NewTabState.Success(
                        bookList.map { book ->
                            BookModel(
                                id = book.isbn13,
                                title = book.title,
                                subtitle = book.subtitle,
                                isbn13 = book.isbn13,
                                price = book.price,
                                image = book.image,
                                url = book.url
                            )
                        },
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setState(
                NewTabState.Error(e)
            )
        }
    }

    private fun setState(state: NewTabState) {
        _newTabStateFlow.value = state
    }

}
