package com.soda1127.itbookstorecleanarchitecture.screen.main.newtab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.widget.item.BookItem

@Composable
fun BookNewScreen(
    viewModel: BookNewTabViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    onBookClick: (String, String) -> Unit
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (state is NewTabState.Uninitialized) {
            viewModel.fetchData()
        }
    }

    BookNewContent(
        state = state,
        paddingValues = paddingValues,
        onBookClick = onBookClick,
        onLikeClick = viewModel::toggleLikeButton
    )
}

@Composable
fun BookNewContent(
    state: NewTabState,
    paddingValues: PaddingValues,
    onBookClick: (String, String) -> Unit,
    onLikeClick: (BookModel) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            is NewTabState.Loading -> {
                CircularProgressIndicator()
            }

            is NewTabState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues
                ) {
                    items(state.modelList) { book ->
                        BookItem(
                            book = book,
                            onClick = { onBookClick(book.isbn13, book.title) },
                            onLikeClick = { onLikeClick(book) }
                        )
                    }
                }
            }

            is NewTabState.Error -> {
                Text(text = "Error: ${state.e.localizedMessage}")
            }

            is NewTabState.Uninitialized -> {
                // Initial state, do nothing or show loading
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookNewContentPreview() {
    MaterialTheme {
        BookNewContent(
            state =
                NewTabState.Success(
                    modelList =
                        listOf(
                            com.soda1127.itbookstorecleanarchitecture.model.book
                                .BookModel(
                                    id = "1",
                                    title = "Preview Book",
                                    subtitle = "Subtitle",
                                    isbn13 = "123",
                                    price = "$10",
                                    image =
                                        "https://itbook.store/img/books/9781484239063.png",
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
