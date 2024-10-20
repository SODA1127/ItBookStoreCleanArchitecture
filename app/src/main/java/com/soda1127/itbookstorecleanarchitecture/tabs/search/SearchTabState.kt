package com.soda1127.itbookstorecleanarchitecture.tabs.search

import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity

sealed class SearchTabState {

    object Uninitialized: SearchTabState()

    object Loading: SearchTabState()

    data class Result(
        val bookEntities: List<BookEntity>
    ): SearchTabState()

    data class Error(val exception: Exception): SearchTabState()

}
