package com.example.ecojourney.screens

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
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.SharedPrefsHelper
import com.example.ecojourney.progressbar.StickProgressBar
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun Profile(navController: NavHostController, userId: String) {

    val context = LocalContext.current
    // State variables to hold user data
    var userName by remember { mutableStateOf("Nama User") }
    var userEmail by remember { mutableStateOf("example@gmail.com") }
    var carbonFootprintResult by remember { mutableStateOf<Float?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch user data
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Fetch user name from Firebase
                getUserName { name ->
                    userName = name
                }
            } catch (e: Exception) {
                Log.e("Profile", "Error fetching user name: ${e.message}")
                userName = "Nama User"
            }

            try {
                // Fetch user email from FirebaseAuth
                val email = FirebaseAuth.getInstance().currentUser?.email ?: "example@gmail.com"
                Log.d("Profile", "Fetched email from FirebaseAuth: $email")
                userEmail = email
            } catch (e: Exception) {
                Log.e("Profile", "Error fetching user email: ${e.message}")
                userEmail = ""
            }

            try {
                // Fetch carbon footprint result
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

    // Define additional SVG images for right side
    val buttonRightImages = listOf(
        R.drawable.arrow_green,
        R.drawable.arrow_green,
        R.drawable.arrow_green,
        R.drawable.arrow_green
    )

    fun handleLogout(context: Context, navController: NavHostController) {
        coroutineScope.launch {
            try {
                // Clear SharedPreferences
                val sharedPrefs = SharedPrefsHelper.getSharedPreferences(context)
                sharedPrefs.edit().clear().apply()

                // Perform Firebase logout
                FirebaseAuth.getInstance().signOut()

                // Navigate to login screen and clear back stack
                navController.navigate("login") {
                    // Clear back stack to prevent back navigation to the profile screen
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
                                        .width(200.dp)
                                        .height(22.dp),
                                    currentValue = it,
                                    maxValue = 780f,
                                    width = 200.dp
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
                                        .padding(horizontal = 30.dp) // Padding kiri dan kanan
                                        .padding(top = 16.dp) // Padding atas
                                        .padding(bottom = 12.dp) // Padding bawah (ditambahkan)
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
                    // Large circular profile picture
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.Center)
                            .background(Color(0xFFFFDF8B), shape = CircleShape)
                            .clickable {

                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_default),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    // Small circular button on top of the profile picture
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
                            contentDescription = "Small Icon",
                            modifier = Modifier
                                .size(24.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
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
                                        handleLogout(context, navController) // Call the logout function
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
                            // Circle with SVG image on the left side
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Color(0xFFE0E0E0),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = buttonImages[index]),
                                    contentDescription = "SVG Icon",
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp)) // Space between circle and text

                            // Button text with two levels of styling
                            Column(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .fillMaxHeight()
                                    .weight(1f) // Ensure text takes up remaining space
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

                            // New SVG image on the right side
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Color(0xFFE0E0E0),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                                    .align(Alignment.CenterVertically),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = buttonRightImages[index]),
                                    contentDescription = "SVG Icon",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun ProfilePreview() {
    val navController = rememberNavController() // Create a mock NavController for the preview
    Profile(navController, userId = "")
}
