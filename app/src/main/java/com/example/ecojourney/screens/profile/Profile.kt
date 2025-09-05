package com.example.ecojourney.screens.profile

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.ecojourney.R
import com.example.ecojourney.data.local.SharedPrefsHelper
import com.example.ecojourney.ui.progressbar.StickProgressBar
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.example.ecojourney.screens.fetchCarbonFootprintResult

@Composable
fun Profile(navController: NavHostController, userId: String) {

    val context = LocalContext.current
    var userName by remember { mutableStateOf("Nama User") }
    var userEmail by remember { mutableStateOf("example@gmail.com") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var vehicleType by remember { mutableStateOf("") }
    var carbonFootprintResult by remember { mutableStateOf<Float?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser
                val email = user?.email ?: "example@gmail.com"
                val uid = user?.uid ?: ""
                Log.d("Profile", "Fetched email from FirebaseAuth: $email")
                userEmail = email

                getUserProfileData(email, uid) { name, imageUrl, vehicle ->
                    userName = name.ifEmpty { "Nama User" }
                    profileImageUrl = imageUrl.takeIf { it.isNotEmpty() }
                    vehicleType = vehicle
                }
            } catch (e: Exception) {
                Log.e("Profile", "Error fetching user data: ${e.message}")
                userEmail = "example@gmail.com"
                userName = "Nama User"
                profileImageUrl = null
            }

            try {
                carbonFootprintResult = fetchCarbonFootprintResult(userId)
            } catch (e: Exception) {
                Log.e("Profile", "Error fetching carbon footprint result: ${e.message}")
                carbonFootprintResult = 0f
            }
        }
    }

    val buttonImages = listOf(
        R.drawable.setting,
        R.drawable.guard,
        R.drawable.information,
        R.drawable.logout
    )

    val buttonTopTexts = listOf(
        "Pengaturan",
        "Keamanan dan Kata Sandi",
        "Tentang Aplikasi",
        "Keluar"
    )

    val buttonBottomTexts = listOf(
        "Atur dan kelola tampilan aplikasi",
        "Lakukan autentikasi akun disini",
        "Ketahui informasi terkait aplikasi",
        "Untuk keluar aplikasi"
    )

    val buttonRightImages = listOf(
        R.drawable.arrow_green,
        R.drawable.arrow_green,
        R.drawable.arrow_green,
        R.drawable.arrow_green
    )

    fun handleLogout(context: Context, navController: NavHostController) {
        coroutineScope.launch {
            try {
                val sharedPrefs = SharedPrefsHelper.getSharedPreferences(context)
                sharedPrefs.edit().clear().apply()
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            } catch (e: Exception) {
                Log.e("Profile", "Logout error: ${e.message}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.header_prof),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 8.0f)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profil",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                )
            )

            Spacer(modifier = Modifier.height(56.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(top = 50.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = userName,
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontFamily = PJakartaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                )
                            )

                            Text(
                                text = userEmail,
                                style = TextStyle(
                                    color = Color(0xFF787878),
                                    fontSize = 12.sp,
                                    fontFamily = PJakartaSansFontFamily,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            carbonFootprintResult?.let {
                                StickProgressBar(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .height(22.dp),
                                    currentValue = it,
                                    maxValue = 780f
                                )
                            }

                            carbonFootprintResult?.let {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(color = Color.Black)) {
                                            append("Kamu sudah menghasilkan karbon sebanyak ")
                                        }
                                        withStyle(style = SpanStyle(color = Color(0xFFFFBA00))) {
                                            append("${"%.2f".format(it)}/ ")
                                        }
                                        withStyle(style = SpanStyle(color = Color(0xFF3F6B1B))) {
                                            append("780 kg")
                                        }
                                        withStyle(style = SpanStyle(color = Color.Black)) {
                                            append(" bulan ini!")
                                        }
                                    },
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = PJakartaSansFontFamily,
                                        lineHeight = 20.sp,
                                        letterSpacing = 0.25.sp,
                                        textAlign = TextAlign.Center,
                                    ),
                                    modifier = Modifier
                                        .padding(horizontal = 30.dp)
                                        .padding(top = 16.dp)
                                        .padding(bottom = 12.dp)
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-100).dp)
                        .align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.Center)
                            .background(Color(0xFFFFDF8B), shape = CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        profileImageUrl?.let { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } ?: Image(
                            painter = painterResource(id = R.drawable.img_default),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color.White, shape = CircleShape)
                            .clickable {
                                navController.navigate("profile_detail")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pencil),
                            contentDescription = "Edit Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Menu",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(4) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .padding(vertical = 6.dp)
                            .clickable {
                                when (index) {
                                    0 -> navController.navigate("settings")
                                    1 -> navController.navigate("security")
                                    2 -> navController.navigate("about")
                                    3 -> {
                                        handleLogout(context, navController)
                                    }
                                }
                            },
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = buttonImages[index]),
                                    contentDescription = "SVG Icon",
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .fillMaxHeight()
                                    .weight(1f)
                            ) {
                                Text(
                                    text = buttonTopTexts[index],
                                    style = TextStyle(
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        fontFamily = PJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Start
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = buttonBottomTexts[index],
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontSize = 10.sp,
                                        fontFamily = PJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Start
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(4.dp)
                                    .align(Alignment.CenterVertically),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = buttonRightImages[index]),
                                    contentDescription = "SVG Icon",
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getUserProfileData(email: String, uid: String, onComplete: (String, String, String) -> Unit) {
    val db = Firebase.firestore
    val userDocRefByEmail = db.collection("users").document(email)
    val userDocRefByUid = db.collection("users").document(uid)

    // Try fetching by email first
    userDocRefByEmail.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val name = documentSnapshot.getString("name") ?: ""
                val profileImageUrl = documentSnapshot.getString("profileImageUrl") ?: ""
                val vehicleType = documentSnapshot.getString("vehicleType") ?: ""
                Log.d("Profile", "Fetched data by email: name=$name, imageUrl=$profileImageUrl, vehicleType=$vehicleType")
                onComplete(name, profileImageUrl, vehicleType)
            } else {
                // If email-based document doesn't exist, try UID
                userDocRefByUid.get()
                    .addOnSuccessListener { uidDocumentSnapshot ->
                        val name = uidDocumentSnapshot.getString("name") ?: ""
                        val profileImageUrl = uidDocumentSnapshot.getString("profileImageUrl") ?: ""
                        val vehicleType = uidDocumentSnapshot.getString("vehicleType") ?: ""
                        Log.d("Profile", "Fetched data by UID: name=$name, imageUrl=$profileImageUrl, vehicleType=$vehicleType")
                        onComplete(name, profileImageUrl, vehicleType)
                    }
                    .addOnFailureListener { e ->
                        Log.e("Profile", "Error fetching user profile data by UID: ${e.message}")
                        onComplete("", "", "")
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.e("Profile", "Error fetching user profile data by email: ${e.message}")
            // Try UID as fallback
            userDocRefByUid.get()
                .addOnSuccessListener { uidDocumentSnapshot ->
                    val name = uidDocumentSnapshot.getString("fullName") ?: uidDocumentSnapshot.getString("name") ?: ""
                    val profileImageUrl = uidDocumentSnapshot.getString("profileImageUrl") ?: ""
                    val vehicleType = uidDocumentSnapshot.getString("vehicleType") ?: ""
                    Log.d("Profile", "Fetched data by UID: name=$name, imageUrl=$profileImageUrl, vehicleType=$vehicleType")
                    onComplete(name, profileImageUrl, vehicleType)
                }
                .addOnFailureListener { e ->
                    Log.e("Profile", "Error fetching user profile data by UID: ${e.message}")
                    onComplete("", "", "")
                }
        }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun ProfilePreview() {
    val navController = rememberNavController()
    Profile(navController, userId = "")
}