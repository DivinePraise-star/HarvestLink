package com.techproject.harvestlink.ui.screens.chat


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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.techproject.harvestlink.R
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.ui.theme.HarvestLinkTheme
import com.techproject.harvestlink.ui.ScreenHeader
import com.techproject.harvestlink.ui.screens.AppDestinations
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatList(
    navController: NavController,
    chatListViewModel: ChatListViewModel = viewModel(factory = ChatListViewModel.Factory)
) {

    val conversation = chatListViewModel.conversations.collectAsState().value

//    val lastMessagesPerUser = userMessages.userMessages
//            .asSequence()
//            .filter {
//                it.senderId == currentUserId || it.receiverId == currentUserId
//            }
//            .groupBy {
//                if (it.senderId == currentUserId) it.receiverId else it.senderId
//            }
//            .mapValues { (_, messages) ->
//                messages.maxByOrNull { it.timestamp }
//            }
//            .toMap()

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = stringResource(R.string.messagesScreenHeader),
            modifier = Modifier
                .padding(12.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(conversation) { each ->
                if (conversation.isNotEmpty()) {
                    ChatItem(
                        id = each.conversationId,
                        userName = each.userName,
                        lastMessage = each.lastMessage,
                        time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(each.lastMessageTimestamp)),
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



@Preview(showBackground = true)
@Composable
fun ChatScreenPreview(){
    HarvestLinkTheme(darkTheme = true){

    }
}