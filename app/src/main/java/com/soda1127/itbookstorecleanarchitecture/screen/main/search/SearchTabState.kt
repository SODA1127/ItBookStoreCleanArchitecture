package com.soda1127.itbookstorecleanarchitecture.screen.main.search

import com.soda1127.itbookstorecleanarchitecture.model.Model
import com.soda1127.itbookstorecleanarchitecture.model.search.SearchHistoryModel
import com.soda1127.itbookstorecleanarchitecture.screen.base.State

sealed class SearchTabState: State {

    object Uninitialized: SearchTabState()

    object Loading: SearchTabState()

    sealed class Success: SearchTabState() {

        data class SearchHistory(
            val modelList: List<SearchHistoryModel>
        ): SearchTabState()

        data class SearchResult(
            val modelList: List<Model>,
            val searchKeyword: String? = null,
            val currentPage: String? = null,
            val totalResultCount: Int? = null
        ): SearchTabState()

    }

    data class Error(
        val e: Throwable,
        val searchKeyword: String? = null
    ): SearchTabState()

}
