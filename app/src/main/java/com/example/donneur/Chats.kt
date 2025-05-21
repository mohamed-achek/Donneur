package com.example.donneur

// ...existing imports...
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// --- Add these data classes to resolve ChatMessage reference ---
data class ChatMessage(
    val profilePic: Int = R.drawable.profile_photo,
    val senderName: String = "",
    val content: String = "",
    val timestamp: String = "",
    val isNew: Boolean = false,
    val chatId: String = ""
)

data class ChatDetailMessage(
    val sender: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

// ...existing data classes...

@Composable
fun Chats() {
    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid
    val chatList = remember { mutableStateListOf<ChatMessage>() }
    val db = FirebaseDatabase.getInstance().reference

    // --- New Chat Dialog State ---
    var showNewChatDialog by remember { mutableStateOf(false) }
    var newChatEmail by remember { mutableStateOf("") }
    var newChatError by remember { mutableStateOf<String?>(null) }
    var creatingChat by remember { mutableStateOf(false) }

    // Sample chats fallback
    fun ensureSampleChats() {
        if (chatList.none { it.chatId == "sample_chat_1" }) {
            chatList.add(
                ChatMessage(
                    profilePic = R.drawable.post1_profile,
                    senderName = "Sarah Ben Ali",
                    content = "Hey! Are you available for donation?",
                    timestamp = "09:30",
                    isNew = true,
                    chatId = "sample_chat_1"
                )
            )
        }
        if (chatList.none { it.chatId == "sample_chat_2" }) {
            chatList.add(
                ChatMessage(
                    profilePic = R.drawable.post2_profile,
                    senderName = "Ahmed Khemiri",
                    content = "Thank you for your help last week!",
                    timestamp = "Yesterday",
                    isNew = false,
                    chatId = "sample_chat_2"
                )
            )
        }
    }

    // Load chat list for current user
    LaunchedEffect(userId) {
        chatList.clear()
        if (userId != null) {
            db.child("userChats").child(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatList.clear()
                        snapshot.children.forEach { chatSnap ->
                            val chatId = chatSnap.key ?: return@forEach
                            val chatData = chatSnap.getValue(ChatMessage::class.java)
                            if (chatData != null) {
                                chatList.add(chatData.copy(chatId = chatId))
                            }
                        }
                        ensureSampleChats()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        ensureSampleChats()
                    }
                })
        } else {
            ensureSampleChats()
        }
        ensureSampleChats()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "chatList") {
            composable("chatList") {
                ChatListScreen(
                    chatMessages = chatList,
                    onChatClick = { message ->
                        navController.navigate("chatDetail/${message.chatId}/${message.senderName}")
                    },
                    onNewChatClick = { showNewChatDialog = true }
                )
            }
            composable(
                "chatDetail/{chatId}/{senderName}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("senderName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                val senderName = backStackEntry.arguments?.getString("senderName") ?: ""
                ChatDetailScreen(
                    chatId = chatId,
                    senderName = senderName,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        FloatingActionButton(
            onClick = { showNewChatDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Chat", tint = Color.White)
        }

        if (showNewChatDialog) {
            AlertDialog(
                onDismissRequest = { showNewChatDialog = false },
                title = { Text("Start New Chat") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newChatEmail,
                            onValueChange = { newChatEmail = it },
                            label = { Text("Recipient Email") }
                        )
                        if (newChatError != null) {
                            Text(newChatError ?: "", color = Color.Red)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newChatEmail.isBlank()) {
                                newChatError = "Email required"
                                return@TextButton
                            }
                            creatingChat = true
                            newChatError = null
                            val usersRef = db.child("users")
                            usersRef.orderByChild("email").equalTo(newChatEmail)
                                .get().addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        val recipientEntry = snapshot.children.first()
                                        val recipientId = recipientEntry.key ?: ""
                                        val recipientName = recipientEntry.child("name").getValue(String::class.java) ?: newChatEmail
                                        val chatId = if (userId!! < recipientId) "${userId}_$recipientId" else "${recipientId}_$userId"
                                        val chatData = ChatMessage(
                                            profilePic = R.drawable.profile_photo,
                                            senderName = recipientName,
                                            content = "",
                                            timestamp = "",
                                            isNew = false,
                                            chatId = chatId
                                        )
                                        db.child("userChats").child(userId).child(chatId).setValue(chatData)
                                        db.child("userChats").child(recipientId).child(chatId).setValue(
                                            chatData.copy(senderName = user?.displayName ?: "You")
                                        )
                                        showNewChatDialog = false
                                        newChatEmail = ""
                                        creatingChat = false
                                    } else {
                                        newChatError = "User not found"
                                        creatingChat = false
                                    }
                                }.addOnFailureListener {
                                    newChatError = "Failed to search user"
                                    creatingChat = false
                                }
                        }
                    ) {
                        Text(if (creatingChat) "Creating..." else "Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNewChatDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ChatListScreen(
    chatMessages: List<ChatMessage>,
    onChatClick: (ChatMessage) -> Unit,
    onNewChatClick: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filteredChats = if (search.isBlank()) chatMessages else chatMessages.filter {
        it.senderName.contains(search, ignoreCase = true) || it.content.contains(search, ignoreCase = true)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Text(
            text = "Chats",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            modifier = Modifier
                .padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
        )
        // Search bar
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            shape = RoundedCornerShape(20.dp),
            label = { Text("Search chats...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (filteredChats.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No chats yet. Start a new chat!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(filteredChats) { message ->
                    ChatListItem(message, onClick = { onChatClick(message) })
                }
            }
        }
    }
}

@Composable
fun ChatListItem(message: ChatMessage, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shadowElevation = if (message.isNew) 4.dp else 1.dp,
        shape = RoundedCornerShape(16.dp),
        color = if (message.isNew) Color(0xFFE3F8F3) else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = message.profilePic),
                contentDescription = null,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = message.senderName,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = message.content,
                    style = TextStyle(
                        fontWeight = if (message.isNew) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 15.sp,
                        color = if (message.isNew) MaterialTheme.colorScheme.primary else Color.Gray
                    ),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = message.timestamp,
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = Color(0xFF26A586)
                    )
                )
                if (message.isNew) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF26A586)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "•",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatDetailScreen(chatId: String, senderName: String, onBack: () -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatDetailMessage>() }
    val db = FirebaseDatabase.getInstance().reference

    // Load messages for this chat
    LaunchedEffect(chatId) {
        if (chatId.isBlank()) return@LaunchedEffect
        db.child("chats").child(chatId).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    snapshot.children.forEach { msgSnap ->
                        val msg = msgSnap.getValue(ChatDetailMessage::class.java)
                        if (msg != null) messages.add(msg)
                    }
                    // --- Show the initial content of the conversation if no messages exist ---
                    if (messages.isEmpty()) {
                        // Find the chat summary from userChats for this chatId
                        db.child("userChats").child(userId).child(chatId)
                            .get().addOnSuccessListener { chatSnap ->
                                val chatData = chatSnap.getValue(ChatMessage::class.java)
                                if (chatData != null && chatData.content.isNotBlank()) {
                                    messages.add(
                                        ChatDetailMessage(
                                            sender = chatData.senderName,
                                            text = chatData.content,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )
                                }
                            }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        // Sticky header
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                TextButton(onClick = onBack) {
                    Text("< Back", color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = senderName,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                ChatBubble(
                    message = msg,
                    isMe = msg.sender == userId,
                    senderName = if (msg.sender == userId) "You" else senderName
                )
            }
        }
        // Message input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                label = { Text("Type a message") },
                shape = RoundedCornerShape(20.dp),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (messageText.isNotBlank() && chatId.isNotBlank()) {
                        val msg = ChatDetailMessage(sender = userId, text = messageText, timestamp = System.currentTimeMillis())
                        val msgRef = db.child("chats").child(chatId).child("messages").push()
                        msgRef.setValue(msg)
                        messageText = ""
                    }
                },
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatDetailMessage, isMe: Boolean, senderName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (isMe) Color(0xFF26A586) else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (isMe) Color.White else Color.Black,
                    fontSize = 16.sp
                )
            }
            Text(
                text = senderName,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 2.dp)
            )
        }
    }
}
