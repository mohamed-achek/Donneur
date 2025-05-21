package com.example.donneur

// ...existing imports...

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PostData(
    val id: String = "",
    val content: String = "",
    val author: String = "Mohamed Achek",
    val authorTag: String = "@med_6",
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun Post(
    onPostCreated: (() -> Unit)? = null,
    onPostFailed: ((Exception) -> Unit)? = null
) {
    var postContent by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val custom_fontFamily = FontFamily(
        Font(R.font.nunito_bold, FontWeight.Bold),
        Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
        Font(R.font.nunito_extralight, FontWeight.ExtraLight),
        Font(R.font.nunito_light, FontWeight.Light),
        Font(R.font.nunito_medium, FontWeight.Medium),
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_semibold , FontWeight.SemiBold)
    )
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // --- Post Creation Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_photo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFF26A586), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Mohamed Achek",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = custom_fontFamily,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "@med_6",
                                style = TextStyle(
                                    fontWeight = FontWeight.Light,
                                    fontFamily = custom_fontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF687684)
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = postContent,
                        onValueChange = { postContent = it },
                        placeholder = {
                            Text(
                                text = "What's on your mind?",
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = custom_fontFamily,
                                    color = Color.Gray
                                )
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val contentToPost = postContent.trim()
                                if (contentToPost.isNotBlank()) {
                                    coroutineScope.launch {
                                        try {
                                            // --- Use Firestore for posting ---
                                            val firestore = FirebaseFirestore.getInstance()
                                            val postsRef = firestore.collection("posts")
                                            val newPost = PostData(
                                                content = contentToPost
                                            )
                                            postsRef.add(newPost)
                                                .addOnSuccessListener {
                                                    postContent = ""
                                                    onPostCreated?.invoke()
                                                    snackbarMessage = "Publication successful"
                                                }
                                                .addOnFailureListener { exception ->
                                                    onPostFailed?.invoke(exception)
                                                    snackbarMessage = "Failed to create post: ${exception.message}"
                                                }
                                        } catch (e: Exception) {
                                            onPostFailed?.invoke(e)
                                            snackbarMessage = "Exception: ${e.message}"
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = "Post",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = custom_fontFamily,
                                    fontSize = 17.sp
                                )
                            )
                        }
                    }
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = Color(0xFFEEEEEE)
            )
        }
        snackbarMessage?.let { message ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Snackbar(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(message)
                }
            }
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(2000)
                snackbarMessage = null
            }
        }
    }
}

suspend fun addExamplePostsIfNeeded() {
    val firestore = FirebaseFirestore.getInstance()
    val postsRef = firestore.collection("posts")
    val snapshot = withContext(Dispatchers.IO) { postsRef.get().await() }
    // Only add if there are no posts yet
    if (snapshot.isEmpty) {
        val examplePosts = listOf(
            PostData(
                content = "Urgent: O+ blood needed at El Menzah Hospital. Please help if you can!",
                author = "Sarah Ben Ali",
                authorTag = "@sarah_ba",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2 // 2 hours ago
            ),
            PostData(
                content = "Looking for A- blood donors for my father at Charles Nicolle Hospital. Please reach out if you can help.",
                author = "Yassine Trabelsi",
                authorTag = "@yassine_t",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 5 // 5 hours ago
            )
        )
        examplePosts.forEach { post ->
            postsRef.add(post).await()
        }
    }
}

@Composable
fun PostsList(
    realtime: Boolean = true,
    custom_fontFamily: FontFamily
) {
    val posts = remember { mutableStateListOf<PostData>() }
    val firestore = FirebaseFirestore.getInstance()
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Ensure example posts are added only once, before listening for updates
        if (!initialized) {
            initialized = true
            withContext(Dispatchers.IO) {
                addExamplePostsIfNeeded()
            }
        }
        firestore.collection("posts")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    posts.clear()
                    for (doc in snapshot.documents) {
                        doc.toObject(PostData::class.java)?.let { posts.add(it) }
                    }
                }
            }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        posts.sortedByDescending { it.timestamp }.forEach { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White, Color(0xFFF7F7F7))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_photo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color(0xFF26A586), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = post.author,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = custom_fontFamily,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = post.authorTag,
                                style = TextStyle(
                                    fontWeight = FontWeight.Light,
                                    fontFamily = custom_fontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF687684)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = post.timestamp.toTimeAgo(),
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color(0xFF26A586),
                                fontFamily = custom_fontFamily
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = post.content,
                        style = TextStyle(
                            fontFamily = custom_fontFamily,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 10,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// --- Helper function for time ago formatting ---
fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return when {
        days > 0 -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(this))
        hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
        minutes > 0 -> "$minutes min${if (minutes > 1) "s" else ""} ago"
        else -> "Just now"
    }
}

// --- Blood Type Compatibility Checker ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodTypeCompatibilityChecker() {
    val bloodTypes = listOf(
        "O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(bloodTypes[0]) }

    data class BloodCompatibility(
        val donateTo: List<String>,
        val receiveFrom: List<String>
    )

    val compatibility = mapOf(
        "O-" to BloodCompatibility(
            donateTo = listOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"),
            receiveFrom = listOf("O-")
        ),
        "O+" to BloodCompatibility(
            donateTo = listOf("O+", "A+", "B+", "AB+"),
            receiveFrom = listOf("O-", "O+")
        ),
        "A-" to BloodCompatibility(
            donateTo = listOf("A-", "A+", "AB-", "AB+"),
            receiveFrom = listOf("O-", "A-")
        ),
        "A+" to BloodCompatibility(
            donateTo = listOf("A+", "AB+"),
            receiveFrom = listOf("O-", "O+", "A-", "A+")
        ),
        "B-" to BloodCompatibility(
            donateTo = listOf("B-", "B+", "AB-", "AB+"),
            receiveFrom = listOf("O-", "B-")
        ),
        "B+" to BloodCompatibility(
            donateTo = listOf("B+", "AB+"),
            receiveFrom = listOf("O-", "O+", "B-", "B+")
        ),
        "AB-" to BloodCompatibility(
            donateTo = listOf("AB-", "AB+"),
            receiveFrom = listOf("O-", "A-", "B-", "AB-")
        ),
        "AB+" to BloodCompatibility(
            donateTo = listOf("AB+"),
            receiveFrom = listOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")
        )
    )

    val donateTo = compatibility[selectedType]?.donateTo ?: emptyList()
    val receiveFrom = compatibility[selectedType]?.receiveFrom ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Blood Type Compatibility Checker",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = FontFamily.Default
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select your blood type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                bloodTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "You can donate to:",
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        )
        Text(
            donateTo.joinToString(", "),
            style = TextStyle(fontSize = 15.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "You can receive from:",
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        )
        Text(
            receiveFrom.joinToString(", "),
            style = TextStyle(fontSize = 15.sp)
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun Display(){
    Column {
        Post()
        Spacer(modifier = Modifier.height(16.dp))
        BloodTypeCompatibilityChecker()
    }
}
