package com.soda1127.itbookstorecleanarchitecture.screen.main.bookmark

import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.screen.base.State


sealed class BookmarkState: State {

    object Uninitialized: BookmarkState()

    object Loading: BookmarkState()

    data class Success(
        val modelList: List<BookModel>
    ): BookmarkState()

}
