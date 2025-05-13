package com.example.donneur

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.firebase.Firebase
import com.google.firebase.database.database


@Composable
fun Post() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            PostScreen()
        }
    }
}

@Composable
fun PostScreen(){
    // Write a message to the database
    val database = Firebase.database
    val myRef = database.getReference("First Post")

    val custom_fontFamily = FontFamily(
        Font(R.font.nunito_bold, FontWeight.Bold),
        Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
        Font(R.font.nunito_extralight, FontWeight.ExtraLight),
        Font(R.font.nunito_light, FontWeight.Light),
        Font(R.font.nunito_medium, FontWeight.Medium),
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_semibold , FontWeight.SemiBold)
    )
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
            FilledTonalButton(onClick = { /*TODO*/ },
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
            var postContent by remember { mutableStateOf("") }
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
                onValueChange = {postContent = it},
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

fun onClickButton(){

}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun Display(){
    PostScreen()
}
