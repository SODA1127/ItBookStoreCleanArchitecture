package com.soda1127.itbookstorecleanarchitecture.screen.main.bookmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.widget.item.BookItem

@Composable
fun BookmarkScreen(
    viewModel: BookmarkTabViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    onBookClick: (String, String) -> Unit
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (state is BookmarkState.Uninitialized) {
            viewModel.fetchData()
        }
    }

    // Refresh bookmark list when entering (simple way to ensure fresh data)
    // In strict architectural terms, Flow should handle updates, but if DB updates happen
    // elsewhere,
    // we rely on repository emitting new values or re-fetching. Check VM.
    // VM calls getBooksInWishList() which returns a Flow. So it should update automatically.

    BookmarkContent(
        state = state,
        paddingValues = paddingValues,
        onBookClick = onBookClick,
        onLikeClick = { viewModel.toggleLikeButton(it) }
    )
}

@Composable
fun BookmarkContent(
    state: BookmarkState,
    paddingValues: PaddingValues,
    onBookClick: (String, String) -> Unit,
    onLikeClick: (BookModel) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            is BookmarkState.Loading -> {
                CircularProgressIndicator()
            }

            is BookmarkState.Success -> {
                if (state.modelList.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(paddingValues)
                    ) { Text(text = "No Bookmarks", modifier = Modifier.padding(16.dp)) }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = paddingValues) {
                        items(state.modelList) { book ->
                            BookItem(
                                book = book,
                                onClick = { onBookClick(book.isbn13, book.title) },
                                onLikeClick = { onLikeClick(book) }
                            )
                        }
                    }
                }
            }

            is BookmarkState.Uninitialized -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarkContentPreview() {
    MaterialTheme {
        BookmarkContent(
            state =
                BookmarkState.Success(
                    modelList =
                        listOf(
                            BookModel(
                                id = "1",
                                title = "Bookmarked Book",
                                subtitle = "Subtitle",
                                isbn13 = "123",
                                price = "$20",
                                image = "",
                                url = ""
                            )
                        )
                ),
            onBookClick = { _, _ -> },
            onLikeClick = {},
            paddingValues = PaddingValues()
        )
    }
}
