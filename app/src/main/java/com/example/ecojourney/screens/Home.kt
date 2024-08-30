package com.example.ecojourney.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.CButton
import com.example.ecojourney.R
import com.example.ecojourney.SharedPrefsHelper
import com.example.ecojourney.imageslider.ImageSlider
import com.example.ecojourney.imageslider.CustomPagerIndicator
import com.example.ecojourney.progressbar.AnimatedCircularProgressBar
import com.example.ecojourney.progressbar.AnimatedCircularProgressBarBase
import com.example.ecojourney.progressbar.SmallCircle
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.example.ecojourney.spinner.CustomSpinnerDropdown
import com.example.ecojourney.spinner.CustomTextField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun calculateCarbonFootprint(
    vehicleType: String,
    distance: Float,
    capacity: Float,
    speed: Float
): Float {
    val baseEmissionFactor = when (vehicleType) {
        "Motor" -> 0.1f // Example emission factor
        "Mobil" -> 0.2f
        "Truk" -> 0.3f
        else -> 0.0f
    }

    // Example formula for carbon calculation
    return baseEmissionFactor * distance * (capacity / 1000) * (speed / 60)
}

fun saveUserInputToFirebase(
    userId: String,
    vehicleType: String,
    distance: String,
    capacity: String,
    speed: String,
    carbonFootprint: Float
) {
    val db = FirebaseFirestore.getInstance()

    // Create a HashMap and cast it explicitly to MutableMap<String, Any>
    val userInputs = hashMapOf(
        "vehicleType" to vehicleType,
        "distance" to distance,
        "capacity" to capacity,
        "speed" to speed,
        "carbonFootprint" to carbonFootprint,
        "carbonFootprintResult" to carbonFootprint // store the carbon footprint result
    ) as MutableMap<String, Any>

    db.collection("users").document(userId)
        .update(userInputs)
        .addOnSuccessListener {
            Log.d("Firebase", "Data updated successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Failed to update data", e)
        }
}

fun getUserName(onResult: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    if (user != null) {
        if (!user.displayName.isNullOrEmpty()) {
            Log.d("GetUserName", "User display name found: ${user.displayName}")
            onResult(user.displayName!!)
        } else {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val name = documentSnapshot.getString("name")
                        Log.d("GetUserName", "User name from Firestore: $name")
                        onResult(name ?: "User") // Default to "User" if name is null
                    } else {
                        Log.d("GetUserName", "User document does not exist")
                        onResult("User") // Default name if document does not exist
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("GetUserName", "Failed to fetch user name", e)
                    onResult("User") // Default name in case of failure
                }
        }
    } else {
        Log.d("GetUserName", "No user is signed in")
        onResult("User") // Default name if no user is signed in
    }
}

@Composable
fun Home(navController: NavHostController, context: Context) {
    val userName = remember { mutableStateOf("") }
    val spinnerItems = listOf("Motor", "Mobil", "Truk")
    var selectedItem by remember { mutableStateOf("") }

    var jarakTempuh by remember { mutableStateOf("") }
    var kapasitas by remember { mutableStateOf("") }
    var kecepatan by remember { mutableStateOf("") }

    val isInputEmpty = jarakTempuh.isEmpty() || kapasitas.isEmpty() || kecepatan.isEmpty() || selectedItem.isEmpty()

    var progress by remember { mutableStateOf(SharedPrefsHelper.getProgress(context)) }
    val maxCarbon = 26f // Define the maximum carbon limit

    val hasCalculated = progress > 0

    val sharedPrefs = SharedPrefsHelper.getSharedPreferences(context)
    val lastUpdatedDate = SharedPrefsHelper.getLastUpdatedDate(context)
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    if (lastUpdatedDate != currentDate) {
        progress = 0f
        SharedPrefsHelper.setProgress(context, progress)
        SharedPrefsHelper.setLastUpdatedDate(context, currentDate)
    }

    var carbonResultText by remember {
        mutableStateOf(AnnotatedString(""))
    }

    LaunchedEffect(progress) {
        carbonResultText = buildAnnotatedString {
            if (progress > 0) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3F6B1B))) {
                    append("Kamu sudah menghasilkan karbon sebanyak ")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFFFBA00))) {
                    append("${progress} kg ") // Display the total carbon without capping
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3F6B1B))) {
                    append("/ ${maxCarbon} kg")
                }
            } else {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3F6B1B))) {
                    append("Segera Pantau jejak karbonmu sekarang!")
                }
            }
        }
    }

    fun onCalculateCarbon(userId: String) {
        if (!isInputEmpty) {
            val distance = jarakTempuh.toFloatOrNull() ?: 0f
            val capacity = kapasitas.toFloatOrNull() ?: 0f
            val speed = kecepatan.toFloatOrNull() ?: 0f

            val carbonFootprint = calculateCarbonFootprint(selectedItem, distance, capacity, speed)
            val newProgress = progress + carbonFootprint // Remove the minOf to allow progress to exceed maxCarbon

            // Fetch the current carbon footprint from Firestore
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId)

            userRef.get().addOnSuccessListener { documentSnapshot ->
                val existingCarbonFootprint = documentSnapshot.getDouble("carbonFootprintResult")?.toFloat() ?: 0f
                val updatedCarbonFootprint = existingCarbonFootprint + carbonFootprint

                // Update the Firestore document with the new total
                userRef.update(
                    mapOf(
                        "vehicleType" to selectedItem,
                        "distance" to jarakTempuh,
                        "capacity" to kapasitas,
                        "speed" to kecepatan,
                        "carbonFootprint" to carbonFootprint,
                        "carbonFootprintResult" to updatedCarbonFootprint
                    )
                ).addOnSuccessListener {
                    Log.d("Firebase", "Data updated successfully")
                }.addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to update data", e)
                }

                // Update the local progress and SharedPreferences
                progress = updatedCarbonFootprint
                SharedPrefsHelper.setProgress(context, progress)


                // Update the UI text
                carbonResultText = buildAnnotatedString {
                    if (progress > 0) {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3F6B1B))) {
                            append("Kamu sudah menghasilkan karbon sebanyak ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFFFBA00))) {
                            append("${progress} kg ") // Display the total carbon without capping
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3F6B1B))) {
                            append("/ ${maxCarbon} kg")
                        }
                    } else {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3F6B1B))) {
                            append("Segera Pantau jejak karbonmu sekarang!")
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("Firebase", "Failed to fetch current data", e)
            }
        }
    }

    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    LaunchedEffect(Unit) {
        getUserName { name ->
            userName.value = if (name.isNotEmpty()) name else "User"
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp, end = 30.dp) // Padding untuk seluruh konten dalam Box
    ) {
        // Lingkaran kecil di bagian atas kanan dengan ikon di dalamnya
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color = Color(0xFF3F6B1B), shape = CircleShape)
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.notiff_ic),
                contentDescription = "Icon inside circle",
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)
            )
        }

        // Menambahkan Box dengan desain setengah lingkaran di sisi kanan
        Box(
            modifier = Modifier
                .height(60.dp)
                .width(260.dp)
                .clip(RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50))
                .background(color = Color(0xFF3F6B1B))
                .align(Alignment.TopStart)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Ucapan "Halo, " disertai nama user
                Text(
                    text = "Halo, ${userName.value}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pantau terus jejak karbonmu!",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = PJakartaSansFontFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Box khusus untuk ImageSlider dan Card di bawahnya
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(start = 30.dp)
        ) {
            ImageSlider(
                imageResIds = listOf(
                    R.drawable.banner1, // Ganti dengan resource ID gambar yang ada
                    R.drawable.banner2
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 70.dp)
            )

            Spacer(modifier = Modifier.height(15.dp)) // Jarak antara ImageSlider dan Card

            // Menambahkan Card di bawah ImageSlider
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp), // Menentukan tinggi Card
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                // Konten dalam Card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Cek Jejak Karbon",
                        fontSize = 16.sp,
                        fontFamily = PJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F6B1B),
                        lineHeight = 19.sp,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        // AnimatedCircularProgressBar
                        if (hasCalculated) {
                            AnimatedCircularProgressBar(
                                progress = progress / maxCarbon, // Normalize progress to a value between 0 and 1
                                radius = 60.dp,
                                strokeWidth = 14.dp,
                                modifier = Modifier.align(Alignment.Center) // Center the progress bar
                            )
                        } else {
                            AnimatedCircularProgressBarBase(
                                radius = 60.dp,
                                strokeWidth = 14.dp,
                                modifier = Modifier.align(Alignment.Center) // Center the progress bar
                            )
                        }


                        if (hasCalculated) {
                            SmallCircle(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                            ) {
                                // Navigate to HomeDetail when SmallCircle is clicked
                                navController.navigate("homeDetail/$userId")

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = carbonResultText,
                        fontSize = 12.sp,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA5A5A5),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 50.dp)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Column for dropdowns on the left
                        Column {
                            CustomSpinnerDropdown(
                                items = spinnerItems,
                                hint = "Jenis Kendaraan",
                                selectedItem = selectedItem,
                                onItemSelected = { selectedItem = it },
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            CustomTextField(
                                text = jarakTempuh,
                                hint = "Jarak Tempuh",
                                label = "Km",
                                onTextChanged = { jarakTempuh = it },
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Column for dropdowns on the right
                        Column {
                            CustomTextField(
                                text = kapasitas,
                                hint = "Kapasitas",
                                label = "cc",
                                onTextChanged = { kapasitas = it },
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            CustomTextField(
                                text = kecepatan,
                                hint = "Kecepatan",
                                label = "Km/j",
                                onTextChanged = { kecepatan = it }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { onCalculateCarbon(userId) }, // Use the new logic
                        enabled = !isInputEmpty,
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInputEmpty) Color(0xFFA5A5A5) else Color(0xFF3F6B1B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(start = 15.dp, end = 15.dp)
                    ) {
                        Text(
                            text = "Cek Karbon",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PJakartaFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomePreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    Home(
        context = context,
        navController = navController
    )
}


