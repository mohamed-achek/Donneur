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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
                .align(Alignment.Center), // <-- Fix here
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
    var showDonationHistory by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // <-- Make scrollable
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.profile_photo),
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
            onClick = { showDonationHistory = true }, // Show popup
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
        Spacer(modifier = Modifier.height(24.dp))
        // DonationHistorySection removed from here
        if (showDonationHistory) {
            DonationHistoryDialog(
                custom_fontFamily = custom_fontFamily,
                onDismiss = { showDonationHistory = false }
            )
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
    var showAddDialog by remember { mutableStateOf(false) }
    var addLoading by remember { mutableStateOf(false) }

    // --- Add Donation Data Dialog State ---
    var lastDonationDate by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var hemoglobinLevel by remember { mutableStateOf("") }
    var bloodPressure by remember { mutableStateOf("") }
    var addError by remember { mutableStateOf<String?>(null) }

    // Fetch from Firestore
    LaunchedEffect(userId, addLoading) {
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
                error = null
            } else {
                donationData = null
                error = "No donation data found."
            }
            loading = false
        } catch (e: Exception) {
            error = when {
                e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ->
                    "Cloud Firestore API access denied.\nPlease enable Firestore for your project in the Google Cloud Console and check your Firestore rules."
                e.message?.contains("offline", ignoreCase = true) == true ||
                e.message?.contains("client is offline", ignoreCase = true) == true ->
                    "Failed to load: You appear to be offline. Please check your internet connection and try again."
                e.message?.contains("API has not been used", ignoreCase = true) == true ||
                e.message?.contains("API is disabled", ignoreCase = true) == true ->
                    "Cloud Firestore API is not enabled for your project. Enable it in the Google Cloud Console."
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
            } else if (error != null && donationData == null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        error ?: "",
                        color = Color.Red,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Donation Data")
                }
                // --- Add Donation Data Dialog ---
                if (showAddDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        title = { Text("Add Donation Data") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = lastDonationDate,
                                    onValueChange = { lastDonationDate = it },
                                    label = { Text("Last Donation Date (yyyy-MM-dd)") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = weightKg,
                                    onValueChange = { weightKg = it },
                                    label = { Text("Weight (kg)") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = hemoglobinLevel,
                                    onValueChange = { hemoglobinLevel = it },
                                    label = { Text("Hemoglobin (g/dL)") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = bloodPressure,
                                    onValueChange = { bloodPressure = it },
                                    label = { Text("Blood Pressure") },
                                    singleLine = true
                                )
                                if (addError != null) {
                                    Text(addError ?: "", color = Color.Red)
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    addLoading = true
                                    addError = null
                                    // Validate and save
                                    try {
                                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        val date = sdf.parse(lastDonationDate)
                                        if (date == null) {
                                            addError = "Invalid date format"
                                            addLoading = false
                                            return@TextButton
                                        }
                                        val weight = weightKg.toFloatOrNull()
                                        val hemo = hemoglobinLevel.toFloatOrNull()
                                        if (weight == null || hemo == null) {
                                            addError = "Invalid weight or hemoglobin"
                                            addLoading = false
                                            return@TextButton
                                        }
                                        val data = hashMapOf(
                                            "lastDonationDate" to com.google.firebase.Timestamp(date),
                                            "weightKg" to weight,
                                            "hemoglobinLevel" to hemo,
                                            "bloodPressure" to bloodPressure,
                                            "nextEligibleDate" to null,
                                            "eligible" to null
                                        )
                                        FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(userId!!)
                                            .collection("donationData")
                                            .document("latest")
                                            .set(data)
                                            .addOnSuccessListener {
                                                showAddDialog = false
                                                addLoading = false
                                                // Clear fields
                                                lastDonationDate = ""
                                                weightKg = ""
                                                hemoglobinLevel = ""
                                                bloodPressure = ""
                                            }
                                            .addOnFailureListener { e ->
                                                addError = "Failed to save: ${e.message}"
                                                addLoading = false
                                            }
                                    } catch (e: Exception) {
                                        addError = "Error: ${e.message}"
                                        addLoading = false
                                    }
                                }
                            ) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddDialog = false }) {
                                Text("Cancel")
                            }
                        }
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
            } else if (error != null) {
                // fallback for other errors
                Text(error ?: "", color = Color.Red)
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

@Composable
fun DonationHistoryDialog(
    custom_fontFamily: FontFamily,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Donation History",
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = custom_fontFamily,
                    fontSize = 18.sp
                )
            )
        },
        text = {
            DonationHistorySection(custom_fontFamily)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DonationHistorySection(custom_fontFamily: FontFamily) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var history by remember { mutableStateOf<List<DonationHistoryItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        if (userId == null) {
            error = "Not signed in"
            loading = false
            return@LaunchedEffect
        }
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("donationData")
                .get()
                .await()
            history = snapshot.documents.mapNotNull { doc ->
                doc.toDonationHistoryItem()
            }.sortedByDescending { it.date }
            loading = false
            error = null
        } catch (e: Exception) {
            error = "Failed to load donation history: ${e.message}"
            loading = false
        }
    }

    Text(
        text = "Donation History",
        style = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontFamily = custom_fontFamily,
            fontSize = 18.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
    when {
        loading -> {
            Text(
                text = "Loading...",
                style = TextStyle(
                    fontFamily = custom_fontFamily,
                    fontSize = 15.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        error != null -> {
            Text(
                text = error ?: "",
                style = TextStyle(
                    fontFamily = custom_fontFamily,
                    fontSize = 15.sp,
                    color = Color.Red
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        history.isEmpty() -> {
            Text(
                text = "No donation history yet.",
                style = TextStyle(
                    fontFamily = custom_fontFamily,
                    fontSize = 15.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                history.forEach { item ->
                    DonationHistoryRow(item, custom_fontFamily)
                }
            }
        }
    }
}

data class DonationHistoryItem(
    val date: String,
    val bloodType: String,
    val hemoglobin: String,
    val bloodPressure: String,
    val location: String
)

fun com.google.firebase.firestore.DocumentSnapshot.toDonationHistoryItem(): DonationHistoryItem? {
    val timestamp = this.getTimestamp("lastDonationDate")
    val dateStr = timestamp?.toDate()?.let {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(it)
    } ?: "N/A"
    val bloodType = this.getString("bloodType") ?: "N/A"
    val hemoglobin = (this.getDouble("hemoglobinLevel") ?: this.get("hemoglobinLevel")?.toString()?.toDoubleOrNull())?.toString() ?: "N/A"
    val bloodPressure = this.getString("bloodPressure") ?: "70 mmHg"
    val location = this.getString("location") ?: "Hopital El Menzah"
    return DonationHistoryItem(
        date = dateStr,
        bloodType = bloodType,
        hemoglobin = hemoglobin,
        bloodPressure = bloodPressure,
        location = location
    )
}

@Composable
fun DonationHistoryRow(item: DonationHistoryItem, custom_fontFamily: FontFamily) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Date: ${item.date}",
                style = TextStyle(fontWeight = FontWeight.Bold, fontFamily = custom_fontFamily, fontSize = 15.sp)
            )
            Text(
                text = "Blood Type: ${item.bloodType} | Hemoglobin: ${item.hemoglobin} g/dL | BP: ${item.bloodPressure}",
                style = TextStyle(fontFamily = custom_fontFamily, fontSize = 14.sp)
            )
            Text(
                text = "Location: ${item.location}",
                style = TextStyle(fontFamily = custom_fontFamily, fontSize = 14.sp)
            )
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
