package com.soda1127.itbookstorecleanarchitecture.tabs.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchTabViewModel @Inject constructor(
    private val searchRepository: SearchRepository
): ViewModel() {

    val stateFlow = MutableStateFlow<SearchTabState>(SearchTabState.Uninitialized)

    fun getNewBooks() = viewModelScope.launch {
        stateFlow.update { SearchTabState.Loading }

        try {
            val bookEntities = searchRepository.getNewBooks()
            stateFlow.update { SearchTabState.Result(bookEntities) }
        } catch (e: Exception) {
            stateFlow.update { SearchTabState.Error(e) }
        }
    }

    fun searchBooksByKeyword(keyword: String) = viewModelScope.launch {
        stateFlow.update { SearchTabState.Loading }

        try {
            val bookEntities = searchRepository.searchBooksByKeyword(keyword)
            stateFlow.update { SearchTabState.Result(bookEntities) }
        } catch (e: Exception) {
            stateFlow.update { SearchTabState.Error(e) }
        }
    }

}
