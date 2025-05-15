package com.example.donneur

// ...existing imports...
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

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
            // Pass state and callbacks down
            PostScreen(
                postContent = postContent,
                onPostContentChange = { postContent = it },
                onPostClick = {
                    if (postContent.isNotBlank()) {
                        coroutineScope.launch {
                            try {
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
        // Show snackbar if message is set
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
            // Hide snackbar after a short delay
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
    val posts = remember { mutableStateListOf<PostData>() }
    LaunchedEffect(Unit) {
        val database = Firebase.database
        val postsRef = database.getReference("posts")
        if (realtime) {
            // Listen for real-time updates
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
            // One-time load
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
        posts.forEach { post ->
            Text(
                text = post.content,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun Display(){
    // PostScreen() // old preview
    Post()
}

