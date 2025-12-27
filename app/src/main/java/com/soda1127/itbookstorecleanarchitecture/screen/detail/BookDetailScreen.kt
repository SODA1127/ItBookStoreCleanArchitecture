@file:OptIn(ExperimentalToolbarApi::class)

package com.soda1127.itbookstorecleanarchitecture.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.soda1127.itbookstorecleanarchitecture.R
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookInfoEntity
import kotlin.reflect.KProperty
import me.onebone.toolbar.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val event = viewModel.eventFlow.collectAsState(initial = null).value
    var title by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.fetchData() }

    LaunchedEffect(event) {
        when (val event = event) {
            is BookDetailEvent.ShowToast -> {
                onShowSnackbar(event.message)
            }

            else -> {}
        }
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

    val collapsingState = rememberCollapsingToolbarScaffoldState()

    LaunchedEffect(state) {
        if (state is BookDetailState.Success) {
            collapsingState.toolbarState.expand(0)
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val progress = collapsingState.toolbarState.progress
                if (progress > 0f && progress < 1f) {
                    if (progress > 0.5f) {
                        collapsingState.toolbarState.expand(200)
                    } else {
                        collapsingState.toolbarState.collapse(200)
                    }
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    CollapsingToolbarScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        state = collapsingState,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            val progress = collapsingState.toolbarState.progress
            val toolbarState = collapsingState.toolbarState

            // Success Header Content
            if (state is BookDetailState.Success) {
                BookDetailHeader(
                    state = state as BookDetailState.Success,
                    progress = progress,
                    onLikeClick = { viewModel.toggleLikeButton() },
                    onSaveMemo = { memo -> viewModel.saveMemo(memo) },
                    modifier =
                        Modifier
                            .road(Alignment.TopStart, Alignment.TopStart)
                            .scrollable(
                                orientation = Orientation.Vertical,
                                state =
                                    rememberScrollableState { delta ->
                                        toolbarState.dispatchRawDelta(delta)
                                        delta
                                    }
                            )
                )
            }

            // Fixed Top Bar (pinned)
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 1f - progress))
                        .pin()
                        .zIndex(1f)
                        .scrollable(
                            orientation = Orientation.Vertical,
                            state =
                                rememberScrollableState { delta ->
                                    toolbarState.dispatchRawDelta(delta)
                                    delta
                                }
                        )
            ) {
                TopAppBar(
                    title = {
                        val titleText =
                            (state as? BookDetailState.Success)?.bookInfoEntity?.title ?: ""
                        Text(
                            text = titleText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.graphicsLayer { alpha = 1f - progress }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor =
                                MaterialTheme.colorScheme.onSurface
                        )
                )
                if (progress < 0.01f) {
                    HorizontalDivider()
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val s = state) {
                is BookDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is BookDetailState.Success -> {
                    BookDetailContent(state = s)
                }

                is BookDetailState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error occurred")
                        Button(onClick = { viewModel.fetchData() }) { Text("Retry") }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun BookDetailHeader(
    state: BookDetailState.Success,
    progress: Float,
    onLikeClick: () -> Unit,
    onSaveMemo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var memoText by remember(state.memo) { mutableStateOf(state.memo) }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 64.dp)
                .graphicsLayer { alpha = progress }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
        ) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(state.bookInfoEntity.image)
                        .crossfade(true)
                        .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.size(16.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .heightIn(min = 120.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = state.bookInfoEntity.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (state.bookInfoEntity.subtitle.isNotEmpty()) {
                        Text(
                            text = state.bookInfoEntity.subtitle,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterStart),
                            text = state.bookInfoEntity.price,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        IconButton(
                            onClick = onLikeClick,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            val iconRes =
                                if (state.isLiked) R.drawable.ic_heart_enable
                                else R.drawable.ic_heart_disable
                            val tint = if (state.isLiked) Color.Red else Color.Gray
                            Icon(
                                imageVector = ImageVector.vectorResource(id = iconRes),
                                contentDescription = "Like",
                                tint = tint
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column {
            Text("Memo", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = memoText,
                onValueChange = { memoText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter memo") }
            )
            Button(
                onClick = { onSaveMemo(memoText) },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) { Text("Save & Exit") }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun BookDetailContent(state: BookDetailState.Success) {
    // Info Text reflection
    val infoTexts: List<Pair<String, String>> =
        remember(state.bookInfoEntity) {
            val infoTextPairs = mutableListOf<Pair<String, String>>()
            BookInfoEntity::class.members.forEach { property ->
                if (property is KProperty<*>) {
                    try {
                        val value = property.call(state.bookInfoEntity)
                        infoTextPairs.add(property.name to value.toString())
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }
            infoTextPairs
        }

    val gridState = rememberLazyGridState()

    // Body (Properties Grid)
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(minSize = 180.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // Properties grouped into a table-like structure
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                infoTexts.chunked(2).forEach { rowItems ->
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        rowItems.forEachIndexed { index, textInfo ->
                            Box(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(horizontal = 8.dp)
                            ) { TitleAndInfo(textInfo) }
                            if (index < rowItems.size - 1) {
                                VerticalDivider(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            } else if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun TitleAndInfo(textInfo: Pair<String, String>) {
    val (title, infoText) = textInfo
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = infoText,
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun TitleAndInfoPreview() {
    MaterialTheme {
        TitleAndInfo(
            textInfo =
                "Title" to
                    "Test Test Test Test Test Test Test Test Test Test Test Test Test Test "
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailHeaderPreview() {
    MaterialTheme {
        BookDetailHeader(
            state =
                BookDetailState.Success(
                    bookInfoEntity =
                        BookInfoEntity(
                            title = "Preview Title",
                            subtitle = "Preview Subtitle",
                            price = "$10.00",
                            image = "",
                            isbn13 = "1234567890123",
                            desc =
                                "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription",
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
            progress = 1f,
            onLikeClick = {},
            onSaveMemo = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailContentPreview() {
    MaterialTheme {
        BookDetailContent(
            state =
                BookDetailState.Success(
                    bookInfoEntity =
                        BookInfoEntity(
                            title = "Preview Title",
                            subtitle = "Preview Subtitle",
                            price = "$10.00",
                            image = "",
                            isbn13 = "1234567890123",
                            desc =
                                "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription",
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
                )
        )
    }
}
