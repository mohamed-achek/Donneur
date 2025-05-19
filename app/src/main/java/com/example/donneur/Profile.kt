package com.example.donneur

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

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
fun ProfileScreen() {
    val custom_fontFamily = FontFamily(
        Font(R.font.nunito_bold, FontWeight.Bold),
        Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
        Font(R.font.nunito_extralight, FontWeight.ExtraLight),
        Font(R.font.nunito_light, FontWeight.Light),
        Font(R.font.nunito_medium, FontWeight.Medium),
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_semibold , FontWeight.SemiBold)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.profile2),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(110.dp)
                .shadow(10.dp, CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Mohamed Achek",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontFamily = custom_fontFamily,
                fontSize = 22.sp
            )
        )
        Text(
            text = "@med_6",
            style = TextStyle(
                fontWeight = FontWeight.Light,
                fontFamily = custom_fontFamily,
                fontSize = 16.sp,
                color = Color(0xFF687684)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        // --- Donation Eligibility Tracker ---
        DonationEligibilityTracker()
        Spacer(modifier = Modifier.height(16.dp))
        // User stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStat("Blood Type", "O+")
            ProfileStat("Posts", "12")
            ProfileStat("Donations", "5")
        }
        Spacer(modifier = Modifier.height(24.dp))
        // About section
        Text(
            text = "About",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontFamily = custom_fontFamily,
                fontSize = 18.sp
            ),
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Passionate blood donor and community helper. Always ready to help those in need.",
            style = TextStyle(
                fontFamily = custom_fontFamily,
                fontSize = 15.sp
            ),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 4.dp, bottom = 16.dp)
        )
        // Actions
        Button(
            onClick = { /* TODO: Edit profile */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Edit Profile", fontFamily = custom_fontFamily)
        }
        Button(
            onClick = { /* TODO: View donation history */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Donation History", fontFamily = custom_fontFamily)
        }
        Button(
            onClick = { /* TODO: Settings */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Settings", fontFamily = custom_fontFamily)
        }
    }
}

// --- DonationEligibilityTracker Composable ---

@Composable
fun DonationEligibilityTracker() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var loading by remember { mutableStateOf(true) }
    var donationData by remember { mutableStateOf<DonationData?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Fetch from Firestore
    LaunchedEffect(userId) {
        if (userId == null) {
            error = "Not signed in"
            loading = false
            return@LaunchedEffect
        }
        try {
            val doc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("donationData")
                .document("latest")
                .get()
                .await()
            if (doc.exists()) {
                donationData = DonationData.fromMap(doc.data)
            }
            loading = false
        } catch (e: Exception) {
            // Improved error handling for offline and permission errors
            error = when {
                e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ->
                    "Cloud Firestore API access denied.\nPlease enable Firestore for your project in the Google Cloud Console and check your Firestore rules."
                e.message?.contains("offline", ignoreCase = true) == true ||
                e.message?.contains("client is offline", ignoreCase = true) == true ->
                    "Failed to load: You appear to be offline. Please check your internet connection and try again."
                else ->
                    "Failed to load: ${e.message}"
            }
            loading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                "Donation Eligibility Tracker",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            if (loading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Loading...")
                }
            } else if (error != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        error ?: "",
                        color = Color.Red,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (error?.contains("Firestore API access denied") == true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Visit the Google Cloud Console and enable the Firestore API for your project. " +
                        "Also check your Firestore security rules.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
                if (error?.contains("offline", ignoreCase = true) == true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Please check your internet connection and try again.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            } else if (donationData != null) {
                // ...existing code for showing eligibility...
                val now = System.currentTimeMillis()
                val lastDonation = donationData!!.lastDonationDate?.toDate()?.time ?: 0L
                val cooldownMillis = 8 * 7 * 24 * 60 * 60 * 1000L // 8 weeks
                val nextEligible = lastDonation + cooldownMillis
                val eligible = now >= nextEligible &&
                        (donationData!!.weightKg ?: 0f) >= 50f &&
                        (donationData!!.hemoglobinLevel ?: 0f) >= 12.5f
                val remaining = if (eligible) 0L else nextEligible - now

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                ) {
                    if (eligible) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Eligible",
                            tint = Color(0xFF26A586),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            "You are eligible to donate!",
                            color = Color(0xFF26A586),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Not eligible",
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            "Not eligible yet",
                            color = Color(0xFFFFA000),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                if (!eligible) {
                    val days = (remaining / (1000 * 60 * 60 * 24)).toInt()
                    val hours = ((remaining / (1000 * 60 * 60)) % 24).toInt()
                    val mins = ((remaining / (1000 * 60)) % 60).toInt()
                    Text(
                        "Next eligible in: ${days}d ${hours}h ${mins}m",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                // Show last donation date and vitals
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                Text(
                    "Last donation: " +
                            (donationData!!.lastDonationDate?.toDate()?.let { sdf.format(it) } ?: "N/A"),
                    fontSize = 14.sp
                )
                Text(
                    "Weight: ${donationData!!.weightKg ?: "?"} kg",
                    fontSize = 14.sp
                )
                Text(
                    "Hemoglobin: ${donationData!!.hemoglobinLevel ?: "?"} g/dL",
                    fontSize = 14.sp
                )
                Text(
                    "Blood Pressure: ${donationData!!.bloodPressure ?: "?"}",
                    fontSize = 14.sp
                )
            } else {
                Text("No donation data found.", color = Color.Gray)
            }
        }
    }
}

// --- Data Model for Firestore mapping ---
data class DonationData(
    val lastDonationDate: Timestamp? = null,
    val weightKg: Float? = null,
    val hemoglobinLevel: Float? = null,
    val bloodPressure: String? = null,
    val nextEligibleDate: Timestamp? = null,
    val eligible: Boolean? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>?): DonationData? {
            if (map == null) return null
            return DonationData(
                lastDonationDate = map["lastDonationDate"] as? Timestamp,
                weightKg = (map["weightKg"] as? Number)?.toFloat(),
                hemoglobinLevel = (map["hemoglobinLevel"] as? Number)?.toFloat(),
                bloodPressure = map["bloodPressure"] as? String,
                nextEligibleDate = map["nextEligibleDate"] as? Timestamp,
                eligible = map["eligible"] as? Boolean
            )
        }
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                fontSize = 18.sp
            )
        )
        Text(
            text = label,
            style = TextStyle(
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Default,
                fontSize = 14.sp,
                color = Color(0xFF687684)
            )
        )
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
