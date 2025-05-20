package com.example.donneur

// ...existing imports...
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class ChatMessage(
    val profilePic: Int = R.drawable.profile2,
    val senderName: String = "",
    val content: String = "",
    val timestamp: String = "",
    val isNew: Boolean = false,
    val chatId: String = ""
)

data class ChatDetailMessage(
    val sender: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

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

    // Load chat list for current user
    LaunchedEffect(userId) {
        if (userId == null) return@LaunchedEffect
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
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "chatList") {
            composable("chatList") {
                ChatScreen(
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
                ChatDetailScreen(chatId = chatId, senderName = senderName, onBack = { navController.popBackStack() })
            }
        }

        // Floating Action Button to add a chat
        FloatingActionButton(
            onClick = { showNewChatDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Chat")
        }

        // --- New Chat Dialog ---
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
                            // --- Find user by email in Firebase Database ---
                            val usersRef = db.child("users")
                            usersRef.orderByChild("email").equalTo(newChatEmail)
                                .get().addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        val recipientEntry = snapshot.children.first()
                                        val recipientId = recipientEntry.key ?: ""
                                        val recipientName = recipientEntry.child("name").getValue(String::class.java) ?: newChatEmail
                                        val chatId = if (userId!! < recipientId) "${userId}_$recipientId" else "${recipientId}_$userId"
                                        val chatData = ChatMessage(
                                            profilePic = R.drawable.profile2,
                                            senderName = recipientName,
                                            content = "",
                                            timestamp = "",
                                            isNew = false,
                                            chatId = chatId
                                        )
                                        // Add chat for both users
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

fun startChatWithUser(
    myUserId: String,
    myDisplayName: String,
    recipientId: String,
    recipientName: String,
    onChatReady: (chatId: String, recipientName: String) -> Unit
) {
    val db = FirebaseDatabase.getInstance().reference
    val chatId = if (myUserId < recipientId) "${myUserId}_$recipientId" else "${recipientId}_$myUserId"
    val chatDataForMe = ChatMessage(
        profilePic = R.drawable.profile2,
        senderName = recipientName,
        content = "",
        timestamp = "",
        isNew = false,
        chatId = chatId
    )
    val chatDataForThem = ChatMessage(
        profilePic = R.drawable.profile2,
        senderName = myDisplayName,
        content = "",
        timestamp = "",
        isNew = false,
        chatId = chatId
    )
    db.child("userChats").child(myUserId).child(chatId).setValue(chatDataForMe)
    db.child("userChats").child(recipientId).child(chatId).setValue(chatDataForThem)
    onChatReady(chatId, recipientName)
}

@Composable
fun ChatScreen(
    chatMessages: List<ChatMessage>,
    onChatClick: (ChatMessage) -> Unit,
    onNewChatClick: () -> Unit
) {
    var search by rememberSaveable { mutableStateOf("") }
    Column (
        modifier = Modifier.fillMaxSize()
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                shape = RoundedCornerShape(15.dp),
                enabled = true,
                label = {
                    Text(
                        text = "Search... ",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_fontFamily,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(0.67f),
            )
            Spacer(modifier = Modifier.width(10.dp))
            ExtendedFloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(50.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = search,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            ExtendedFloatingActionButton(
                onClick = onNewChatClick,
                modifier = Modifier
                    .size(50.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.new_chat),
                    contentDescription = search
                )
            }
        }
        if (chatMessages.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No chats yet. Start a new chat!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
            ) {
                items(chatMessages) { message ->
                    ChatItem(message, onClick = { onChatClick(message) })
                }
            }
        }
    }
}

@Composable
fun ChatItem(message: ChatMessage, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape  = RoundedCornerShape(20.dp),
        modifier= Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = message.profilePic),
                contentDescription = null,
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape),
                alignment = Alignment.CenterStart
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = message.senderName,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_fontFamily,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = message.content,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = custom_fontFamily,
                            fontSize = 15.sp
                        )
                    )
                }
                Column (
                    modifier = Modifier
                ){
                    Text(
                        text = message.timestamp,
                        color = Color(0xFF26A586),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_fontFamily,
                            fontSize = 15.sp
                        )
                    )
                    if (message.isNew) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .align(Alignment.CenterHorizontally)
                                .background(Color(0xFF26A586), CircleShape)
                        ){
                            Text(text = "11",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = custom_fontFamily,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
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
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBack, modifier = Modifier.padding(end = 8.dp)) {
                Text("< Back")
            }
            Text(
                text = senderName,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = if (msg.sender == userId) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        horizontalAlignment = if (msg.sender == userId) Alignment.End else Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (msg.sender == userId) Color(0xFF26A586) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = if (msg.sender == userId) Color.White else Color.Black
                            )
                        }
                        Text(
                            text = if (msg.sender == userId) "You" else senderName,
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
        // --- Message input and send button ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                label = { Text("Type a message") }
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
                }
            ) {
                Text("Send")
            }
        }
    }
}
