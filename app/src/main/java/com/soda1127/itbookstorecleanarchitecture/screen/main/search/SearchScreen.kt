package com.soda1127.itbookstorecleanarchitecture.screen.main.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soda1127.itbookstorecleanarchitecture.model.book.BookLoadRetryModel
import com.soda1127.itbookstorecleanarchitecture.model.book.BookLoadingModel
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.widget.item.BookItem
import com.soda1127.itbookstorecleanarchitecture.widget.item.SearchHistoryItem

@Composable
fun SearchScreen(
    viewModel: SearchTabViewModel = hiltViewModel(),
    onBookClick: (String, String) -> Unit
) {
    val state by viewModel.searchTabStateFlow.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        if (state is SearchTabState.Uninitialized) {
            viewModel.fetchData()
        }
    }

    LaunchedEffect(state) {
        val currentState = state
        if (currentState is SearchTabState.Success.SearchResult) {
            searchQuery = currentState.searchKeyword ?: ""
        }
    }

    SearchContent(
        state = state,
        searchQuery = searchQuery,
        onQueryChange = {
            searchQuery = it
            if (it.isEmpty()) {
                viewModel.fetchData()
            }
        },
        onSearch = {
            viewModel.searchByKeyword(searchQuery)
            keyboardController?.hide()
        },
        onHistoryClick = {
            searchQuery = it
            viewModel.searchByHistory(it)
            keyboardController?.hide()
        },
        onRemoveHistory = { viewModel.removeHistory(it) },
        onBookClick = onBookClick,
        onLoadMore = { viewModel.loadMoreSearchResult() },
        onRetry = {
            if (state is SearchTabState.Success.SearchResult) { // Retry for load more
                 viewModel.loadMoreSearchResult(true)
            } else if (state is SearchTabState.Error) { // Retry for general error
                val errorState = state as SearchTabState.Error
                if (errorState.searchKeyword != null) {
                    viewModel.searchByKeyword(errorState.searchKeyword)
                } else {
                    viewModel.fetchData()
                }
            }
        }
    )
}

@Composable
fun SearchContent(
    state: SearchTabState,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onHistoryClick: (String) -> Unit,
    onRemoveHistory: (String) -> Unit,
    onBookClick: (String, String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search Books") },
            trailingIcon = {
                IconButton(onClick = onSearch) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when (state) {
                is SearchTabState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SearchTabState.Success.SearchHistory -> {
                    if (state.modelList.isEmpty()) {
                        Text("No Search History", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn {
                            items(state.modelList) { history ->
                                SearchHistoryItem(
                                    keyword = history.text,
                                    onHistoryClick = onHistoryClick,
                                    onRemoveClick = onRemoveHistory
                                )
                            }
                        }
                    }
                }
                is SearchTabState.Success.SearchResult -> {
                    if (state.modelList.isEmpty()) {
                        Text("No Results Found", modifier = Modifier.align(Alignment.Center))
                    } else {
                        val listState = rememberLazyListState()
                        // Pagination logic
                        val isAtBottom by remember {
                           derivedStateOf {
                               val layoutInfo = listState.layoutInfo
                               val totalItems = layoutInfo.totalItemsCount
                               val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                               lastVisibleItemIndex >= totalItems - 1
                           }
                        }

                        LaunchedEffect(isAtBottom) {
                            if (isAtBottom) {
                                onLoadMore()
                            }
                        }

                        LazyColumn(state = listState) {
                            items(state.modelList, key = { it.id }) { item ->
                                when (item) {
                                    is BookModel -> {
                                        BookItem(book = item) {
                                            onBookClick(item.isbn13, item.title)
                                        }
                                    }
                                    is BookLoadingModel -> {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                    is BookLoadRetryModel -> {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(text = "Error: ${item.errorMessage}")
                                            Button(onClick = onRetry) {
                                                Text("Retry")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is SearchTabState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: ${state.e.localizedMessage}")
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }
                is SearchTabState.Uninitialized -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchContentPreview() {
    MaterialTheme {
        SearchContent(
            state = SearchTabState.Success.SearchHistory(
                modelList = listOf(
                    com.soda1127.itbookstorecleanarchitecture.model.search.SearchHistoryModel(
                         id = "1", type = com.soda1127.itbookstorecleanarchitecture.model.CellType.SEARCH_HISTORY_CELL, text = "Kotlin"
                    )
                )
            ),
            searchQuery = "",
            onQueryChange = {},
            onSearch = {},
            onHistoryClick = {},
            onRemoveHistory = {},
            onBookClick = { _, _ -> },
            onLoadMore = {},
            onRetry = {}
        )
    }
}
