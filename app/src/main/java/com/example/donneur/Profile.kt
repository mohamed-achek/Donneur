package com.example.donneur

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Profile(){
    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileScreen()
        }
    }
}

@Composable
fun ProfileScreen(){
    Column {
        TextButton(
            onClick = {},
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth()
        ){
            Row {
                Image(
                    painter = painterResource(id = R.drawable.profile_photo),
                    contentDescription = "user profile",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(125.dp)
                )
            }

        }

    }
}


@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun display2(){
    ProfileScreen()
}