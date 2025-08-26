package com.soda1127.itbookstorecleanarchitecture.screen.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
    
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    Scaffold(
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
                )
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
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
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
                        imageVector = Icons.Default.Send,
                        contentDescription = "전송"
                    )
                }
            }
        }
    }
}

