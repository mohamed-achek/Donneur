package com.example.donneur

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.donneur.ui.theme.BloodBondTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class SignUp : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // If user is already signed in, navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            setContent {
                BloodBondTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SignUpPage(this)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(context: Context){
    val custom_fontFamily = FontFamily(
        Font(R.font.nunito_bold, FontWeight.Bold),
        Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
        Font(R.font.nunito_extralight, FontWeight.ExtraLight),
        Font(R.font.nunito_light, FontWeight.Light),
        Font(R.font.nunito_medium, FontWeight.Medium),
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_semibold , FontWeight.SemiBold)
    )
    val context = LocalContext.current
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ){
        Image(
            painter = painterResource(id = R.drawable.signin_1),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(30.dp, RoundedCornerShape(30.dp), true)
                .padding(horizontal = 5.dp),
            contentScale = ContentScale.FillWidth,
        )
        var firstName by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var bloodType by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var checkedState by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(450.dp)
                .shadow(10.dp, RoundedCornerShape(30.dp), true),
            shape = RoundedCornerShape(15.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))
            Row {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(
                        "First Name",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_fontFamily
                        )
                    ) },
                    shape = RoundedCornerShape(15.dp),
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(start = 20.dp, end = 5.dp)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = {lastName = it},
                    label = { Text(
                        "Last Name",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_fontFamily
                        )
                    ) },
                    shape = RoundedCornerShape(15.dp),
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, end = 20.dp))
            }
            Row {
                OutlinedTextField(
                    value = age,
                    onValueChange = {age = it},
                    label = { Text(
                        "Age",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_fontFamily
                        )
                    ) },
                    shape = RoundedCornerShape(15.dp),
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(start = 20.dp, end = 5.dp))
                OutlinedTextField(
                    value = bloodType,
                    onValueChange = {bloodType = it},
                    label = { Text(
                        "Blood Type",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_fontFamily
                        )
                    ) },
                    shape = RoundedCornerShape(15.dp),
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, end = 20.dp))
            }
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = { Text(
                    "Email Address",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    )
                ) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.mail),
                        contentDescription = "email",
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(15.dp),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_fontFamily
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text(
                    "Password",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    )
                ) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "password",
                        modifier = Modifier.size(20.dp)
                    )
                },
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    focusedBorderColor = Color.Blue, // Color when the text field is focused
//                    unfocusedBorderColor = Color.Gray // Color when the text field is not focused
//                ),
                shape = RoundedCornerShape(15.dp),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_fontFamily
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {confirmPassword = it},
                label = { Text(
                    "Confirm Password",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    )
                ) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.password),
                        contentDescription = "confirm password",
                        modifier = Modifier.size(20.dp)
                    )
                },
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    focusedBorderColor = Color.Blue, // Color when the text field is focused
//                    unfocusedBorderColor = Color.Gray // Color when the text field is not focused
//                ),
                shape = RoundedCornerShape(15.dp),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_fontFamily
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Checkbox(
                    checked = checkedState,
                    onCheckedChange = { checkedState = it },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Text(
                    text = "I agree Terms and Conditions",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Button(
                onClick = { signUpUser(context, email, password)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(50.dp, RoundedCornerShape(15.dp))
                    .height(45.dp),
                shape =  RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    )
                )
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "Already Registered?",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_fontFamily
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            TextButton(onClick = {
                val intent = Intent(context, SignIn::class.java)
                context.startActivity(intent)
            }) {
                Text(
                    text = "Lets Sign in.",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily,
                        fontStyle = FontStyle.Italic
                    ),
                )
            }
        }

    }
}

private fun signUpUser(context: Context, email: String, password: String) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success
                // You can add further actions here like navigating to another screen
                // or displaying a success message.
                val intent = Intent(context, SignIn::class.java)
                context.startActivity(intent)
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    context, "Authentication failed. ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}

//@Preview(
//    showBackground = true,
//    showSystemUi = true
//)
//@Composable
//fun displayPage1(){
//    SignUpPage(this)
//}