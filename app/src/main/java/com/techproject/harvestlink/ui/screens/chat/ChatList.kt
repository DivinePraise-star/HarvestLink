package com.techproject.harvestlink.ui.screens.chat


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.techproject.harvestlink.R
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.Farmer
import com.techproject.harvestlink.ui.theme.HarvestLinkTheme
import com.techproject.harvestlink.ui.ScreenHeader
import com.techproject.harvestlink.ui.screens.AppDestinations
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatList(
    navController: NavController,
    chatListViewModel: ChatListViewModel = viewModel(factory = ChatListViewModel.Factory)
) {
    val chatListUiState = chatListViewModel.chatListUiState.collectAsState().value
    val conversation = chatListUiState.conversations


    if(!chatListUiState.isNewChat){
        Box {
            Column(modifier = Modifier.fillMaxSize()) {
                ScreenHeader(
                    title = stringResource(R.string.messagesScreenHeader),
                    modifier = Modifier
                        .padding(12.dp)
                )
                when {
                    chatListUiState.isLoadingConversations -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) { CircularProgressIndicator() }
                    }
                    conversation.isEmpty() -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) { Text("No Messages") }
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(conversation) { each ->
                                ChatItem(
                                    id = each.conversationId,
                                    userName = each.userName,
                                    lastMessage = each.lastMessage,
                                    time = SimpleDateFormat(
                                        "h:mm a",
                                        Locale.getDefault()
                                    ).format(Date(each.lastMessageTimestamp)),
                                    unreadCount = each.unreadCount ?: 0,
                                    onClick = {
                                        navController.navigate("${AppDestinations.MESSAGES.name}/${each.conversationId}/${each.userId}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
            IconButton(
                onClick = { chatListViewModel.toggleNewChat() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .size(50.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.plus),
                    contentDescription = stringResource(R.string.new_chat),
                    modifier = Modifier.padding(8.dp)

                )
            }
        }
    }else{
        NewChat(chatListViewModel,chatListUiState,navController)
    }
}

@Composable
fun NewChat(
    chatListViewModel: ChatListViewModel,
    chatListUiState: ChatListUiState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        chatListViewModel.fetchFarmers()
    }
    val farmerList = chatListUiState.farmers
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        BackHandler { chatListViewModel.toggleNewChat() }
        ScreenHeader(
            title = stringResource(R.string.new_chat),
            modifier = Modifier
                .padding(12.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            IconButton(
                onClick = { chatListViewModel.toggleNewChat() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .size(50.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.backbutton),
                    contentDescription = stringResource(R.string.upButton),
                    modifier = Modifier.padding(8.dp)
                )
            }
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(4.dp),
                placeholder = { Text("Search for a farmer...") },
                shape = RoundedCornerShape(dimensionResource(R.dimen.chatBoxBoarderRadius)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )

            )
        }
        when {
            chatListUiState.isLoadingFarmers -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { CircularProgressIndicator() }
            }
            farmerList.isEmpty() -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { Text("No Farmers") }
            }
            else -> {
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                    items(farmerList) { each ->
                        NewChatItem(
                            id = each.id,
                            userName = each.name,
                            onClick = {
                                scope.launch {
                                    val conversationId = chatListViewModel.getOrCreateConversation(each.id)
                                    navController.navigate("${AppDestinations.MESSAGES.name}/$conversationId/${each.id}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable for a single chat box on the Chat List screen
 */
@Composable
fun ChatItem(
    id: String,
    lastMessage: String?,
    userName: String?,
    time: String,
    unreadCount: Int,
    isOnline: Boolean = false,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        onClick = { onClick(id) },
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Box {
                Icon(
                    painter = painterResource(R.drawable.user_profile),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                )
                if(isOnline){
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userName ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = lastMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (unreadCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unreadCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewChatItem(
    id: String,
    userName: String?,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        onClick = { onClick(id) },
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.user_profile),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = userName ?: "Unknown",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview(){
    HarvestLinkTheme(darkTheme = true){

    }
}