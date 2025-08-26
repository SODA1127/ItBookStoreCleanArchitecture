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
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailActivity
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
                MaterialTheme {
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
    val chatState by viewModel.chatState.collectAsState()
    val listState = rememberLazyListState()
    var messageText by remember { mutableStateOf("") }
    var keyboardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    val topBarBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val density = LocalDensity.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                // 키보드가 올라왔는지 감지 (화면 높이가 줄어들면 키보드가 올라온 것)
                val screenHeight = with(density) { size.height.toDp() }
                keyboardVisible = screenHeight < 600.dp // 임계값 설정
            }
    ) {
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
                        containerColor = MaterialTheme.colorScheme.surface
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
                Text(
                    text = message.content,
                    color = if (isUser) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
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
                maxLines = 4
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

