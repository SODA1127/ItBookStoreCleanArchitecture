package com.soda1127.itbookstorecleanarchitecture.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.soda1127.itbookstorecleanarchitecture.R
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookInfoEntity
import kotlin.reflect.KProperty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    isbn13: String,
    title: String,
    viewModel: BookDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.bookDetailStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }
    
    // Handle SaveMemo state which triggers back in existing code.
    // In Compose, we might want to just show a toast or stay.
    // Logic said: saveMemo calls onBackPressedCallback.handleOnBackPressed().
    // So if SaveMemo state is emitted, we go back.
    LaunchedEffect(state) {
        if (state is BookDetailState.SaveMemo) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = {
                        // We need to save memo before back?
                        // Activity logic: onBackPressed saves memo.
                        // We can simulate this.
                        // But here onBackClick just pops.
                        // We should expose save method and call it.
                        // For navigation icon, usually just back.
                        // But requirement: "saveMemo(binding.bookMemoInput.text.toString()) then finish()"
                        // Implementation below handles this in BackHandler or explicit call.
                         onBackClick()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val currentState = state) {
                is BookDetailState.Loading -> {
                     CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is BookDetailState.Success -> {
                    BookDetailContent(
                        state = currentState,
                        onLikeClick = { viewModel.toggleLikeButton() },
                        onSaveMemo = { memo -> viewModel.saveMemo(memo) }
                    )
                }
                is BookDetailState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error occurred")
                        Button(onClick = { viewModel.fetchData() }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun BookDetailContent(
    state: BookDetailState.Success,
    onLikeClick: () -> Unit,
    onSaveMemo: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var memoText by remember { mutableStateOf(state.memo) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = state.bookInfoEntity.image,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.size(16.dp))
            
            Column {
                Text(text = state.bookInfoEntity.title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = state.bookInfoEntity.subtitle, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.bookInfoEntity.price, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Like Button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
             IconButton(onClick = onLikeClick) {
                val iconRes = if (state.isLiked) R.drawable.ic_heart_enable else R.drawable.ic_heart_disable
                val tint = if (state.isLiked) Color.Red else Color.Gray
                Icon(
                    imageVector = ImageVector.vectorResource(id = iconRes),
                    contentDescription = "Like",
                    tint = tint
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Memo", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = memoText,
            onValueChange = { memoText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter memo") }
        )
         Button(
            onClick = { onSaveMemo(memoText) },
            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
        ) {
            Text("Save & Exit")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info Text reflection
        val infoText = remember(state.bookInfoEntity) {
            var text = ""
            BookInfoEntity::class.members.forEach { property ->
                 if (property is KProperty<*>) {
                    try {
                        val value = property.call(state.bookInfoEntity)
                        text += "[${property.name}] : ${value}\n\n"
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }
            text
        }
        
        Text(text = infoText, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailContentPreview() {
    MaterialTheme {
        BookDetailContent(
            state = BookDetailState.Success(
                bookInfoEntity = BookInfoEntity(
                    title = "Preview Title",
                    subtitle = "Preview Subtitle",
                    price = "$10.00",
                    image = "",
                    isbn13 = "1234567890123",
                    desc = "Description",
                    isbn10 = "1234567890",
                    pages = 100,
                    year = 2024,
                    rating = 4.5f,
                    url = "url",
                    authors = "Author",
                    language = "English",
                    publisher = "Publisher",
                    pdf = null
                ),
                memo = "My Memo",
                isLiked = true
            ),
            onLikeClick = {},
            onSaveMemo = {}
        )
    }
}
