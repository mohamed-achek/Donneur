package com.example.donneur

// ...existing imports...
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

data class PostData(
    val id: String = "",
    val content: String = ""
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
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            PostScreen(
                postContent = postContent,
                onPostContentChange = { postContent = it },
                onPostClick = {
                    if (postContent.isNotBlank()) {
                        coroutineScope.launch {
                            try {
                                // --- Posting to Firebase Realtime Database ---
                                val database = Firebase.database
                                val postsRef = database.getReference("posts")
                                val newPostRef = postsRef.push()
                                val post = PostData(id = newPostRef.key ?: "", content = postContent)
                                newPostRef.setValue(post)
                                    .addOnSuccessListener {
                                        postContent = ""
                                        onPostCreated?.invoke()
                                        snackbarMessage = "Post created successfully"
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("Post", "Failed to create post", exception)
                                        onPostFailed?.invoke(exception)
                                        snackbarMessage = "Failed to create post: ${exception.message}"
                                    }
                            } catch (e: Exception) {
                                Log.e("Post", "Exception during post", e)
                                onPostFailed?.invoke(e)
                                snackbarMessage = "Exception: ${e.message}"
                            }
                        }
                    }
                },
                custom_fontFamily = custom_fontFamily
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

@Composable
fun PostScreen(
    postContent: String,
    onPostContentChange: (String) -> Unit,
    onPostClick: () -> Unit,
    custom_fontFamily: FontFamily
){
    Column {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            TextButton(onClick = { /* Do something when the button is clicked */ },
                modifier = Modifier.height(40.dp)) {
                Text("Cancel",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily,
                        fontSize = 17.sp
                    )
                )
            }
            FilledTonalButton(onClick = onPostClick,
                modifier = Modifier.height(40.dp)) {
                Text(text ="Post",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily,
                        fontSize = 17.sp
                    ))
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.profile2),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .shadow(10.dp, CircleShape)
                    .shadow(10.dp, RoundedCornerShape(30.dp), true)
            )
            OutlinedTextField(
                value = postContent,
                onValueChange = onPostContentChange,
                label = {
                    Text(
                        text = "Write your Post Content Here",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_fontFamily,
                            textAlign = TextAlign.Center
                        )
                    )
                },
                shape = RoundedCornerShape(15.dp),
                minLines = 20,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 0.dp)
                    .height(400.dp)
            )
        }
    }
}

// Composable to display posts from Firebase
@Composable
fun PostsList(realtime: Boolean = true) {
    // --- Reads posts from Firebase Realtime Database ---
    val posts = remember { mutableStateListOf<PostData>() }
    val postsRef = Firebase.database.getReference("posts")
    LaunchedEffect(Unit) {
        if (realtime) {
            val listener = object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    posts.clear()
                    snapshot.children.forEach { child ->
                        val post = child.getValue(PostData::class.java)
                        if (post != null) posts.add(post)
                    }
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
            }
            postsRef.addValueEventListener(listener)
        } else {
            postsRef.get().addOnSuccessListener { dataSnapshot ->
                posts.clear()
                dataSnapshot.children.forEach { child ->
                    val post = child.getValue(PostData::class.java)
                    if (post != null) posts.add(post)
                }
            }
        }
    }
    Column {
        posts.reversed().forEach { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = post.content,
                    modifier = Modifier
                        .padding(16.dp),
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 16.sp
                    )
                )
            }
        }
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
    // PostScreen() // old preview
    Column {
        Post()
        Spacer(modifier = Modifier.height(16.dp))
        BloodTypeCompatibilityChecker()
    }
}
