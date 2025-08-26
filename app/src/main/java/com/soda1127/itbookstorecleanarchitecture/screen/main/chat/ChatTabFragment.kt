package com.soda1127.itbookstorecleanarchitecture.screen.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailActivity
import dev.jeziellago.compose.markdowntext.MarkdownText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.soda1127.itbookstorecleanarchitecture.screen.base.ScrollableScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatTabFragment : Fragment(), ScrollableScreen {

    private val viewModel: ChatViewModel by viewModels()
    private var composeView: ComposeView? = null

    companion object {
        const val TAG = "ChatTabFragment"

        fun newInstance(): ChatTabFragment {
            return ChatTabFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            composeView = this
            setContent {
                val isDarkTheme = isSystemInDarkTheme()
                MaterialTheme(
                    colorScheme = if (isDarkTheme) {
                        darkColorScheme(
                            primary = Color(0xFFD0BCFF),
                            onPrimary = Color(0xFF381E72),
                            primaryContainer = Color(0xFF4F378B),
                            onPrimaryContainer = Color(0xFFEADDFF),
                            secondary = Color(0xFFCCC2DC),
                            onSecondary = Color(0xFF332D41),
                            secondaryContainer = Color(0xFF4A4458),
                            onSecondaryContainer = Color(0xFFE8DEF8),
                            tertiary = Color(0xFFEFB8C8),
                            onTertiary = Color(0xFF492532),
                            tertiaryContainer = Color(0xFF633B48),
                            onTertiaryContainer = Color(0xFFFFD8E4),
                            error = Color(0xFFFFB4AB),
                            onError = Color(0xFF690005),
                            errorContainer = Color(0xFF93000A),
                            onErrorContainer = Color(0xFFFFDAD6),
                            background = Color(0xFF1C1B1F),
                            onBackground = Color(0xFFE6E1E5),
                            surface = Color(0xFF1C1B1F),
                            onSurface = Color(0xFFE6E1E5),
                            surfaceVariant = Color(0xFF49454F),
                            onSurfaceVariant = Color(0xFFCAC4D0),
                            outline = Color(0xFF938F99),
                            outlineVariant = Color(0xFF49454F)
                        )
                    } else {
                        lightColorScheme(
                            primary = Color(0xFF6750A4),
                            onPrimary = Color(0xFFFFFFFF),
                            primaryContainer = Color(0xFFEADDFF),
                            onPrimaryContainer = Color(0xFF21005D),
                            secondary = Color(0xFF625B71),
                            onSecondary = Color(0xFFFFFFFF),
                            secondaryContainer = Color(0xFFE8DEF8),
                            onSecondaryContainer = Color(0xFF1D192B),
                            tertiary = Color(0xFF7D5260),
                            onTertiary = Color(0xFFFFFFFF),
                            tertiaryContainer = Color(0xFFFFD8E4),
                            onTertiaryContainer = Color(0xFF31111D),
                            error = Color(0xFFBA1A1A),
                            onError = Color(0xFFFFFFFF),
                            errorContainer = Color(0xFFFFDAD6),
                            onErrorContainer = Color(0xFF410002),
                            background = Color(0xFFFFFBFE),
                            onBackground = Color(0xFF1C1B1F),
                            surface = Color(0xFFFFFBFE),
                            onSurface = Color(0xFF1C1B1F),
                            surfaceVariant = Color(0xFFE7E0EC),
                            onSurfaceVariant = Color(0xFF49454F),
                            outline = Color(0xFF79747E),
                            outlineVariant = Color(0xFFCAC4D0)
                        )
                    }
                ) {
                    ChatScreen(viewModel)
                }
            }
        }
    }

    override fun scrollUp() {
        // LazyColumn의 스크롤을 맨 위로 이동
        composeView?.let { view ->
            view.setContent {
                MaterialTheme {
                    ChatScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val chatState by viewModel.chatStateFlow.collectAsState()
    val listState = rememberLazyListState()
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    val topBarBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Gemini AI 채팅") },
                    actions = {
                        if (chatState.messages.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearChat() }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "채팅 초기화"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    scrollBehavior = topBarBehavior
                )
            },
            bottomBar = {
                ChatInput(
                    value = messageText,
                    onValueChange = { messageText = it },
                    onSendMessage = { text ->
                        if (text.isNotBlank()) {
                            viewModel.sendMessage(text)
                            messageText = ""
                        }
                    },
                    isLoading = chatState.isLoading
                )
            }
        ) { paddingValues ->
            // 채팅 메시지 목록
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(chatState.messages) { message ->
                    ChatMessage(message)
                }
            }
        }
    }
}

@Composable
fun ChatMessage(message: ChatMessage) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                MarkdownText(
                    markdown = message.content,
                    color = if (isUser)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )

                // 책 목록이 있는 경우 표시
                message.books?.let { books ->
                    if (books.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(books) { book ->
                                BookRecommendationItem(book = book)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("메시지를 입력하세요...") },
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (value.isNotBlank()) {
                            onSendMessage(value)
                        }
                    }
                ),
                maxLines = 4,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Button(
                onClick = {
                    if (value.isNotBlank()) {
                        onSendMessage(value)
                    }
                },
                enabled = value.isNotBlank() && !isLoading,
                modifier = Modifier.height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "전송"
                    )
                }
            }
        }
    }
}

@Composable
fun BookRecommendationItem(book: BookEntity) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable {
                context.startActivity(
                    BookDetailActivity.newIntent(context, book.isbn13, book.title)
                )
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // 책 이미지
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.image)
                    .crossfade(true)
                    .build(),
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 책 제목
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // 가격
            Text(
                text = book.price,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * ********** Previews Start **********
 */

@Preview(
    name = "Chat Screen - Light Theme",
    showBackground = true,
    backgroundColor = 0xFFFFFBFE
)
@Composable
fun ChatScreenLightPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005D),
            secondary = Color(0xFF625B71),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1D192B),
            tertiary = Color(0xFF7D5260),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFFD8E4),
            onTertiaryContainer = Color(0xFF31111D),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFFFBFE),
            onBackground = Color(0xFF1C1B1F),
            surface = Color(0xFFFFFBFE),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFE7E0EC),
            onSurfaceVariant = Color(0xFF49454F),
            outline = Color(0xFF79747E),
            outlineVariant = Color(0xFFCAC4D0)
        )
    ) {
        // 간단한 채팅 UI 미리보기
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // TopAppBar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "Gemini AI 채팅",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 채팅 메시지들
            ChatMessage(
                ChatMessage(
                    id = "preview1",
                    content = "안녕하세요! IT 도서 추천을 도와드릴게요.",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis(),
                    books = null
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ChatMessage(
                ChatMessage(
                    id = "preview2",
                    content = "안드로이드 개발 관련 책을 추천해주세요",
                    isFromUser = true,
                    timestamp = System.currentTimeMillis(),
                    books = null
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ChatMessage(
                ChatMessage(
                    id = "preview3",
                    content = "🔍 **'android' 관련 추천 도서**\n\n📚 **Learn Android Studio 3 with Kotlin**\n이 책은 Android 개발을 처음 시작하는 개발자들에게 적합합니다.",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis(),
                    books = listOf(
                        BookEntity(
                            title = "Learn Android Studio 3 with Kotlin",
                            subtitle = "Android Development Guide",
                            isbn13 = "9781234567890",
                            price = "₩35,000",
                            image = "https://example.com/book1.jpg",
                            url = "https://example.com/book1"
                        )
                    )
                )
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 입력 필드
            ChatInput(
                value = "안드로이드 개발",
                onValueChange = {},
                onSendMessage = {},
                isLoading = false
            )
        }
    }
}

@Preview(
    name = "Chat Screen - Dark Theme",
    showBackground = true,
    backgroundColor = 0xFF1C1B1F
)
@Composable
fun ChatScreenDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFD0BCFF),
            onPrimary = Color(0xFF381E72),
            primaryContainer = Color(0xFF4F378B),
            onPrimaryContainer = Color(0xFFEADDFF),
            secondary = Color(0xFFCCC2DC),
            onSecondary = Color(0xFF332D41),
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),
            tertiary = Color(0xFFEFB8C8),
            onTertiary = Color(0xFF492532),
            tertiaryContainer = Color(0xFF633B48),
            onTertiaryContainer = Color(0xFFFFD8E4),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E5),
            surface = Color(0xFF1C1B1F),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF49454F),
            onSurfaceVariant = Color(0xFFCAC4D0),
            outline = Color(0xFF938F99),
            outlineVariant = Color(0xFF49454F)
        )
    ) {
        // 간단한 채팅 UI 미리보기
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // TopAppBar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "Gemini AI 채팅",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 채팅 메시지들
            ChatMessage(
                ChatMessage(
                    id = "preview1",
                    content = "안녕하세요! IT 도서 추천을 도와드릴게요.",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis(),
                    books = null
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ChatMessage(
                ChatMessage(
                    id = "preview2",
                    content = "안드로이드 개발 관련 책을 추천해주세요",
                    isFromUser = true,
                    timestamp = System.currentTimeMillis(),
                    books = null
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ChatMessage(
                ChatMessage(
                    id = "preview3",
                    content = "🔍 **'android' 관련 추천 도서**\n\n📚 **Learn Android Studio 3 with Kotlin**\n이 책은 Android 개발을 처음 시작하는 개발자들에게 적합합니다.",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis(),
                    books = listOf(
                        BookEntity(
                            title = "Learn Android Studio 3 with Kotlin",
                            subtitle = "Android Development Guide",
                            isbn13 = "9781234567890",
                            price = "₩35,000",
                            image = "https://itbook.store/img/books/9781617293290.png",
                            url = "https://example.com/book1"
                        )
                    )
                )
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 입력 필드
            ChatInput(
                value = "안드로이드 개발",
                onValueChange = {},
                onSendMessage = {},
                isLoading = false
            )
        }
    }
}

@Preview(
    name = "Chat Message - User",
    showBackground = true
)
@Composable
fun ChatMessageUserPreview() {
    MaterialTheme {
        ChatMessage(
            ChatMessage(
                id = "preview1",
                content = "안드로이드 개발 관련 책을 추천해주세요",
                isFromUser = true,
                timestamp = System.currentTimeMillis(),
                books = null
            )
        )
    }
}

@Preview(
    name = "Chat Message - Bot",
    showBackground = true
)
@Composable
fun ChatMessageBotPreview() {
    MaterialTheme {
        ChatMessage(
            ChatMessage(
                id = "preview2",
                content = "안녕하세요! IT 도서 추천을 도와드릴게요. 어떤 분야의 책을 찾고 계신가요?",
                isFromUser = false,
                timestamp = System.currentTimeMillis(),
                books = null
            )
        )
    }
}

@Preview(
    name = "Chat Message - Bot with Books",
    showBackground = true
)
@Composable
fun ChatMessageBotWithBooksPreview() {
    MaterialTheme {
        ChatMessage(
            ChatMessage(
                id = "preview3",
                content = "🔍 **'android' 관련 추천 도서**\n\n📚 **Learn Android Studio 3 with Kotlin**\n이 책은 Android 개발을 처음 시작하는 개발자들에게 적합합니다.",
                isFromUser = false,
                timestamp = System.currentTimeMillis(),
                books = listOf(
                    BookEntity(
                        title = "Learn Android Studio 3 with Kotlin",
                        subtitle = "Android Development Guide",
                        isbn13 = "9781234567890",
                        price = "₩35,000",
                        image = "https://example.com/book1.jpg",
                        url = "https://example.com/book1"
                    ),
                    BookEntity(
                        title = "Android Programming: The Big Nerd Ranch Guide",
                        subtitle = "Complete Android Development",
                        isbn13 = "9780987654321",
                        price = "₩42,000",
                        image = "https://example.com/book2.jpg",
                        url = "https://example.com/book2"
                    )
                )
            )
        )
    }
}

@Preview(
    name = "Chat Input",
    showBackground = true
)
@Composable
fun ChatInputPreview() {
    MaterialTheme {
        ChatInput(
            value = "안드로이드 개발",
            onValueChange = {},
            onSendMessage = {},
            isLoading = false
        )
    }
}

@Preview(
    name = "Book Recommendation Item",
    showBackground = true
)
@Composable
fun BookRecommendationItemPreview() {
    MaterialTheme {
        BookRecommendationItem(
            BookEntity(
                title = "Learn Android Studio 3 with Kotlin",
                subtitle = "Android Development Guide",
                isbn13 = "9781234567890",
                price = "₩35,000",
                image = "https://itbook.store/img/books/9781617293290.png",
                url = "https://example.com/book1"
            )
        )
    }
}

/**
 * ********** Previews End **********
 */
