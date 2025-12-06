package com.soda1127.itbookstorecleanarchitecture.screen.main.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soda1127.itbookstorecleanarchitecture.model.CellType
import com.soda1127.itbookstorecleanarchitecture.model.book.BookLoadRetryModel
import com.soda1127.itbookstorecleanarchitecture.model.book.BookLoadingModel
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.model.search.SearchHistoryModel
import com.soda1127.itbookstorecleanarchitecture.widget.item.BookItem
import com.soda1127.itbookstorecleanarchitecture.widget.item.SearchHistoryItem

@Composable
fun SearchScreen(
    viewModel: SearchTabViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    onBookClick: (String, String) -> Unit,
    listState: LazyListState
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }
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
        listState = listState,
        state = state,
        searchQuery = searchQuery,
        paddingValues = paddingValues,
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
        onRetry = { viewModel.handleOnRetry(state = state) },
        onLikeClick = viewModel::toggleLikeButton
    )
}

@Composable
fun SearchContent(
    listState: LazyListState,
    paddingValues: PaddingValues,
    state: SearchTabState,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onHistoryClick: (String) -> Unit,
    onRemoveHistory: (String) -> Unit,
    onBookClick: (String, String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onLikeClick: (BookModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                        )
                    ),
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
                    SearchResultContent(
                        listState = listState,
                        state = state,
                        onBookClick = onBookClick,
                        onLoadMore = onLoadMore,
                        onRetry = onRetry,
                        onLikeClick = onLikeClick
                    )
                }

                is SearchTabState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${state.e.localizedMessage}")
                        Button(onClick = onRetry) { Text("Retry") }
                    }
                }

                is SearchTabState.Uninitialized -> {}
            }
        }
    }
}

@Composable
fun BoxScope.SearchResultContent(
    listState: LazyListState,
    state: SearchTabState.Success.SearchResult,
    onBookClick: (String, String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onLikeClick: (BookModel) -> Unit
) {
    if (state.modelList.isEmpty()) {
        Text("No Results Found", modifier = Modifier.align(Alignment.Center))
    } else {
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

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.modelList, key = { it.id }) { item ->
                when (item) {
                    is BookModel -> {
                        BookItem(
                            book = item,
                            onClick = { onBookClick(item.isbn13, item.title) },
                            onLikeClick = onLikeClick
                        )
                    }

                    is BookLoadingModel -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }

                    is BookLoadRetryModel -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Error: ${item.errorMessage}")
                            Button(onClick = onRetry) { Text("Retry") }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchHistoryPreview() {
    MaterialTheme {
        SearchContent(
            listState = LazyListState(0, 0),
            paddingValues = PaddingValues(),
            state =
                SearchTabState.Success.SearchHistory(
                    modelList =
                        listOf(
                            SearchHistoryModel(
                                id = "1",
                                type = CellType.SEARCH_HISTORY_CELL,
                                text = "Kotlin"
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
            onRetry = {},
            onLikeClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultEmptyPreview() {
    MaterialTheme {
        SearchContent(
            listState = LazyListState(0, 0),
            paddingValues = PaddingValues(),
            state =
                SearchTabState.Success.SearchResult(
                    modelList = emptyList(),
                    searchKeyword = "Kotlin"
                ),
            searchQuery = "Kotlin",
            onQueryChange = {},
            onSearch = {},
            onHistoryClick = {},
            onRemoveHistory = {},
            onBookClick = { _, _ -> },
            onLoadMore = {},
            onRetry = {},
            onLikeClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultPreview() {
    MaterialTheme {
        SearchContent(
            listState = LazyListState(0, 0),
            paddingValues = PaddingValues(),
            state =
                SearchTabState.Success.SearchResult(
                    modelList =
                        (0..10).map {
                            BookModel(
                                id = it.toString(),
                                title = "Kotlin Programming $it",
                                subtitle = "Learn Kotlin $it",
                                isbn13 = "1234567890$it",
                                price = "$${10 + it}",
                                image = "",
                                url = "https://example.com/book$it",
                                isLiked = it % 2 == 0
                            )
                        },
                    searchKeyword = "Kotlin"
                ),
            searchQuery = "Kotlin",
            onQueryChange = {},
            onSearch = {},
            onHistoryClick = {},
            onRemoveHistory = {},
            onBookClick = { _, _ -> },
            onLoadMore = {},
            onRetry = {},
            onLikeClick = {},
        )
    }
}
