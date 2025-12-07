package com.soda1127.itbookstorecleanarchitecture.screen.main.newtab

import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.screen.base.State

sealed class NewTabState: State {

    object Uninitialized: NewTabState()

    object Loading: NewTabState()

    data class Success(
        val modelList: List<BookModel>
    ): NewTabState()

    data class Error(
        val e: Throwable
    ): NewTabState()

}
