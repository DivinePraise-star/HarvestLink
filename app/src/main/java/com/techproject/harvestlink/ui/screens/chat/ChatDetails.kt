package com.techproject.harvestlink.ui.screens.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.techproject.harvestlink.R
import com.techproject.harvestlink.model.Message
import com.techproject.harvestlink.model.MessageStatus
import com.techproject.harvestlink.model.MessageType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ChatDetails(
    onClick: () -> Unit,
    chatDetailsViewModel: ChatDetailsViewModel = viewModel(factory = ChatDetailsViewModel.Factory),
) {
    val messageUi = chatDetailsViewModel.messagesUi.collectAsState().value
    val recipient = chatDetailsViewModel.recipientId
    val currentUserId = chatDetailsViewModel.currentUserId


    val groupedMessages = messageUi.messages.groupBy { message ->
        getGroupHeader(message.createdAt)
    }

    // 1. Image Picker
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { chatDetailsViewModel.sendMessage(
            text = it.toString(),
            type = MessageType.image
        )}
    }

    // 2. Video Picker
    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { chatDetailsViewModel.sendMessage(
            text = it.toString(),
            type = MessageType.video
        )}
    }

    // 3. File/Document Picker
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { chatDetailsViewModel.sendMessage(
            text = it.toString(),
            type = MessageType.file
        )}
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ChatTopBar(
            user = messageUi,
            onClick = onClick
        )
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                groupedMessages.forEach { (date,messagesInDate) ->
                    items(messagesInDate) { message  ->
                        MessageBubble(
                            message = message,
                            isMe = message.senderId == currentUserId
                        )
                    }
                    item {
                        DateHeader(date)
                    }
                }
            }
        }

        ChatTextArea(
            onPickImage = { imageLauncher.launch("image/*") },
            onPickVideo = { videoLauncher.launch("video/*") },
            onPickFile = { fileLauncher.launch(arrayOf("*/*")) },
            onSendMessage = { textMessage ->
                chatDetailsViewModel.sendMessage(
                    text = textMessage,
                )
            })
    }
}

/**
 * Function used to group the messages by date
 */
fun getGroupHeader(timestamp: Long): String {
    val messageDate = Date(timestamp)
    val now = Calendar.getInstance()
    val msgCal = Calendar.getInstance().apply { time = messageDate }

    return when {
        // Check if same day, month, and year
        now.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR) -> "Today"

        // Check if yesterday
        now.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - msgCal.get(Calendar.DAY_OF_YEAR) == 1 -> "Yesterday"

        // Otherwise, full date
        else -> SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(messageDate)
    }
}

/**
 * Displays a top bar on the chat conversation screen
 */
@Composable
fun ChatTopBar(
    user: MessageUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChatIconButton(
                iconRes = R.drawable.backbutton,
                onClick =  onClick
            )
            Box{
                Icon(
                    painter = painterResource(R.drawable.user_profile),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .offset((-2).dp, (-2).dp) // slight inward shift
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer) // green
                        .border(2.dp, Color.White, CircleShape) // white ring
                )
            }
            Column(
                modifier = Modifier.padding(start =8.dp)
            ) {
                Text(
                    text = user.recipientName ?: "Unknown",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "Online")
            }

        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            ChatIconButton(
                iconRes = R.drawable.phone_button,
                onClick = {}
            )
            ChatIconButton(
                iconRes = R.drawable.options_button,
                onClick = {},
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

/**
 * Displays a text area where the user inputs text messages
 */
@Composable
fun ChatTextArea(
    onPickImage: () -> Unit,
    onPickVideo: () -> Unit,
    onPickFile: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // State for dropdown
    var textValue by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Attachment Button with Dropdown
        Box {
            ChatIconButton(
                iconRes = R.drawable.attach_button,
                onClick = { expanded = true },
                contentDescription = "Attach File"
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Image") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.image_icon),
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(R.dimen.chatDropDownIconsSize))
                        ) },
                    onClick = {
                        expanded = false
                        onPickImage()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Video") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.video_icon),
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(R.dimen.chatDropDownIconsSize))
                        ) },
                    onClick = {
                        expanded = false
                        onPickVideo()
                    }
                )
                DropdownMenuItem(
                    text = { Text("File") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.document_icon),
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(R.dimen.chatDropDownIconsSize))
                        ) },
                    onClick = {
                        expanded = false
                        onPickFile()
                    }
                )
            }
        }

        // Input Field
        OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            placeholder = { Text("Type a message...") },
            shape = RoundedCornerShape(dimensionResource(R.dimen.chatBoxBoarderRadius)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )

        )
        ChatIconButton(
            onClick = {
                if (textValue.isNotBlank()) {
                    onSendMessage(textValue)
                    textValue = ""
                }
            },
            iconRes = R.drawable.send_button,
            contentDescription = "Send",
            color = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

/**
 * Displays an individual message bubble
 */
@Composable
fun MessageBubble(
    message: Message,
    isMe: Boolean
){
    val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val timeString = timeFormatter.format(Date(message.createdAt))
    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topEnd = 12.dp,
                topStart = 12.dp,
                bottomEnd = if(isMe) 0.dp else 12.dp,
                bottomStart = if(isMe) 12.dp else 0.dp
            )
        ) {
            when (message.type) {
                MessageType.text -> {
                    Text(text = message.content, modifier = Modifier.padding(12.dp))
                }
                MessageType.image -> {
                    AsyncImage(
                        model = message.content,
                        contentDescription = "Image message",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                MessageType.video -> {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = message.content, // Coil can show video frames
                            contentDescription = "Video preview",
                            modifier = Modifier
                                .size(200.dp)
                                .alpha(0.6f)
                        )
                        Icon(Icons.Default.Favorite, contentDescription = "Play", modifier = Modifier.size(48.dp))
                    }
                }
                MessageType.file -> {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Document File")
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.End
        ){
            Text(
                text = timeString,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )

            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                val statusIcon = when (message.status) {
                    MessageStatus.read -> R.drawable.check_read
                    MessageStatus.delivered -> R.drawable.check_read
                    MessageStatus.sent -> R.drawable.check
                    else -> R.drawable.clock_waiting
                }

                Icon(
                    painter = painterResource(id = statusIcon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (message.status == MessageStatus.read) Color(0xFF00BFFF) else Color.Gray
                )
            }
        }
    }
}

/**
 * Composable for the chat screen icons
 */
@Composable
fun ChatIconButton(
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    color: IconButtonColors = IconButtonDefaults.iconButtonColors()
){
    IconButton(
        onClick = onClick,
        colors = color,
        modifier = modifier
            .size(42.dp)
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription
        )
    }
}

/**
 * Displays the date headings on the chat screen
 */
@Composable
fun DateHeader(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = date,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}