package com.example.donneur

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import androidx.navigation.navArgument

data class ChatDetailMessage(
    val sender: String, // "You" or senderName
    val text: String
)

@Composable
fun Chats() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "chatList") {
        composable("chatList") {
            ChatScreen(
                chatMessages = listOf(
                    ChatMessage(
                        R.drawable.profile2,
                        "Ahmed Khemiri",
                        "ahmed.khemiri@gmail.com",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.post2_profile,
                        "Sarah Ben Ali",
                        "Yesser, merci beaucoup !",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.post1_profile,
                        "Mouna Trabelsi",
                        "Fichier téléchargé.",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.post2_profile,
                        "Omar Ghariani",
                        "Voici un autre tuto si tu veux...",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.post1_profile,
                        "Wassim Jaziri",
                        "On travaille à distance pour le moment...",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.post2_profile,
                        "Hana Bouhlel",
                        "On verra plus tard nchallah.",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.profile2,
                        "Tarek Mejri",
                        "Merci pour ton aide !",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.post1_profile,
                        "Ines Miled",
                        "Ahmed est une personne incroyable.",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.post1_profile,
                        "Sami Jelassi",
                        "Ahmed est une personne incroyable.",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.post1_profile,
                        "Ines Miled",
                        "Ahmed est une personne incroyable.",
                        "08:45",
                        true
                    ),
                    ChatMessage(
                        R.drawable.post1_profile,
                        "Ines Miled",
                        "Ahmed est une personne incroyable.",
                        "08:45",
                        true
                    ),
                ),
                onChatClick = { message ->
                    navController.navigate("chatDetail/${message.senderName}")
                }
            )
        }
        composable(
            "chatDetail/{senderName}",
            arguments = listOf(navArgument("senderName") { type = NavType.StringType })
        ) { backStackEntry ->
            val senderName = backStackEntry.arguments?.getString("senderName") ?: ""
            ChatDetailScreen(senderName = senderName, onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun ChatScreen(
    chatMessages: List<ChatMessage>,
    onChatClick: (ChatMessage) -> Unit
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
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(50.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.new_chat),
                    contentDescription = search
                )
            }
        }
        LazyColumn(
            modifier = Modifier
        ) {
            items(chatMessages) { message ->
                ChatItem(message, onClick = { onChatClick(message) })
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
                    .shadow(7.dp, CircleShape),
                contentScale = ContentScale.Crop,
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

// Simple chat detail screen with in-memory messages
@Composable
fun ChatDetailScreen(senderName: String, onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatDetailMessage(sender = senderName, text = "Hello!"),
            ChatDetailMessage(sender = "You", text = "How are you?")
        )
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
                    horizontalArrangement = if (msg.sender == "You") Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        horizontalAlignment = if (msg.sender == "You") Alignment.End else Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (msg.sender == "You") Color(0xFF26A586) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = if (msg.sender == "You") Color.White else Color.Black
                            )
                        }
                        Text(
                            text = msg.sender,
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
                    if (messageText.isNotBlank()) {
                        messages.add(ChatDetailMessage(sender = "You", text = messageText))
                        messageText = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}

// Define data model for chat message
data class ChatMessage(
    val profilePic: Int, // Resource ID for profile picture
    val senderName: String,
    val content: String,
    val timestamp: String,
    val isNew: Boolean
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DisplayChats(){
    ChatScreen(
        chatMessages = listOf<ChatMessage>(
            ChatMessage(
                R.drawable.post1_profile,
                "Sarah Ben Ali",
                "Ashek is a very good person",
                "08:45",
                true
            ),
        ),
        onChatClick = {}
    )
}

