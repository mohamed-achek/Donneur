package com.example.donneur

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.donneur.ui.theme.BloodBondTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignIn : ComponentActivity() {

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
                        SignInPage(this)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInPage(context: Context){
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
                .shadow(10.dp, RoundedCornerShape(30.dp), true)
                .padding(horizontal = 5.dp),
            contentScale = ContentScale.FillWidth
        )
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(410.dp)
                .shadow(10.dp, RoundedCornerShape(30.dp), true)
                .background(Color(0XFFEFEEEA)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Column {
                ElevatedCard (
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp)
                ){
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "google logo",
                            modifier = Modifier
                                .size(25.dp)
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = "Sign in with Google",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = custom_fontFamily,
                                fontSize = 17.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                ElevatedCard (
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp)
                ){
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.apple),
                            contentDescription = "google logo",
                            modifier = Modifier
                                .size(25.dp)
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = "Sign in with Apple",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = custom_fontFamily,
                                fontSize = 17.sp
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "-------  Or Continue with  -------",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_fontFamily,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = { Text(
                    text = "Email Address",
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
                shape = RoundedCornerShape(15.dp),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_fontFamily
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
            )
            TextButton(onClick = {
                val intent = Intent(context, SignIn::class.java)
                context.startActivity(intent)
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Forgot Password?",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    ),
                )
            }
            Button(
                onClick = { signInUser(context, email, password)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(50.dp, RoundedCornerShape(15.dp))
                    .height(45.dp)
                    .align(Alignment.CenterHorizontally),
                shape =  RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Sign In",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "Not Registered Yet?",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_fontFamily
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            TextButton(onClick = {
                val intent = Intent(context, SignUp::class.java)
                context.startActivity(intent)
            }) {
                Text(
                    text = "Lets Sign up.",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_fontFamily,
                        fontStyle = FontStyle.Italic
                    ),
                )
            }
        }    }
}

private fun signInUser(context: Context, email: String, password: String) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Save the user's authentication state
                val sharedPref = context.getSharedPreferences("user_auth", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("signed_in", true)
                    apply()
                }
                val intent = Intent(context, MainActivity::class.java)
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
//fun displayPage(){
//    SignInPage(this)
//}
