package com.example.ecojourney.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.ui.CButton
import com.example.ecojourney.ui.CButton2
import com.example.ecojourney.ui.CButtonOutlinedF
import com.example.ecojourney.ui.CButtonOutlinedG
import com.example.ecojourney.ui.CTextField
import com.example.ecojourney.ui.CTextFieldPass
import com.example.ecojourney.ui.OrDivider
import com.example.ecojourney.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily

fun signupUser(
    name: String,
    email: String,
    password: String,
    onSignupResult: (Boolean, String?) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Check if name or email already exists
    db.collection("users")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { emailSnapshot ->
            if (!emailSnapshot.isEmpty) {
                onSignupResult(false, "Email sudah terdaftar.")
            } else {
                // Proceed with signup
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: ""
                            // Save the user data to Firestore with userId as document ID
                            val user = hashMapOf(
                                "name" to name,
                                "email" to email
                            )
                            db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener {
                                    Log.d("SignupUser", "User data saved successfully.")
                                    onSignupResult(true, null)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("SignupUser", "Failed to save user data: ${e.message}")
                                    onSignupResult(false, "Gagal menyimpan data pengguna: ${e.message}")
                                }
                        } else {
                            Log.e("SignupUser", "Signup failed: ${task.exception?.message}")
                            onSignupResult(false, task.exception?.message ?: "Pendaftaran gagal.")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("SignupUser", "Failed to signup: ${e.message}")
                        onSignupResult(false, "Gagal mendaftar: ${e.message}")
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.e("SignupUser", "Failed to check email: ${e.message}")
            onSignupResult(false, "Gagal memeriksa email: ${e.message}")
        }
}

@Composable
fun SignupScreen(
    navController: NavHostController,
    onSignupResult: (Boolean, String?) -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSignupButtonEnabled by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Calculate responsive values
    val isSmallScreen = screenHeight < 700.dp
    val isTablet = screenWidth > 600.dp
    val horizontalPadding = if (isTablet) 64.dp else 18.dp
    val logoSize = if (isTablet) 48.dp else if (isSmallScreen) 32.dp else 40.dp
    val titleFontSize = if (isTablet) 32.sp else if (isSmallScreen) 22.sp else 26.sp
    val headerTopPadding = if (isSmallScreen) 16.dp else 20.dp
    val formTopPadding = if (isSmallScreen) 80.dp else 120.dp
    val spacingBetweenElements = if (isSmallScreen) 12.dp else 20.dp
    val bottomBoxHeight = screenHeight * 0.85f

    // Update button enable state based on input fields
    LaunchedEffect(name, email, password, confirmPassword) {
        isSignupButtonEnabled = name.isNotEmpty() &&
                email.isNotEmpty() &&
                password.length >= 6 &&
                password == confirmPassword
    }

    Surface(
        color = Color(0xFF3F6B1B),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header with logo and app name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = headerTopPadding)
                    .zIndex(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ecowhite),
                    contentDescription = null,
                    modifier = Modifier.size(logoSize)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "EcoJourney",
                    style = TextStyle(
                        fontSize = if (isTablet) 28.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PJakartaFontFamily,
                        color = Color.White
                    )
                )
            }

            // White background container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBoxHeight)
                    .align(Alignment.BottomCenter)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            )

            // Scrollable content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = formTopPadding,
                        start = horizontalPadding,
                        end = horizontalPadding,
                        bottom = 16.dp
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Text(
                    "Daftar",
                    style = TextStyle(
                        fontSize = titleFontSize,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F6B1B)
                    ),
                    modifier = Modifier.padding(
                        top = if (isSmallScreen) 16.dp else 24.dp,
                        bottom = spacingBetweenElements
                    )
                )

                // Form fields
                CTextField(
                    title = "Nama Lengkap",
                    hint = "Masukkan Nama Lengkap",
                    value = name,
                    onValueChange = { name = it }
                )

                CTextField(
                    title = "Email",
                    hint = "Masukkan Email",
                    value = email,
                    onValueChange = { email = it }
                )

                CTextFieldPass(
                    title = "Password",
                    hint = "Masukkan Kata Sandi",
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true
                )

                CTextFieldPass(
                    title = "Konfirmasi Kata Sandi",
                    hint = "Konfirmasi Kata Sandi",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(spacingBetweenElements))

                // Signup button
                if (isSignupButtonEnabled) {
                    CButton(
                        text = "Daftar",
                        onClick = {
                            coroutineScope.launch {
                                signupUser(name, email, password) { isSuccess, message ->
                                    if (isSuccess) {
                                        navController.navigate("main") {
                                            popUpTo("signup") { inclusive = true }
                                        }
                                    } else {
                                        errorMessage = message
                                    }
                                }
                            }
                        }
                    )
                } else {
                    CButton2(text = "Daftar")
                }

                // Error message
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = TextStyle(
                            fontSize = if (isTablet) 16.sp else 14.sp
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(if (isSmallScreen) 8.dp else 10.dp))

                // Divider
                OrDivider()

                Spacer(modifier = Modifier.height(if (isSmallScreen) 8.dp else 10.dp))

                // Social login buttons
                CButtonOutlinedG(
                    text = "Masuk dengan Google",
                    onClick = { onGoogleSignInClick() },
                    iconResId = R.drawable.google_ic
                )

                Spacer(modifier = Modifier.height(8.dp))

                CButtonOutlinedF(
                    text = "Masuk dengan Facebook",
                    onClick = { /* Handle login with Facebook */ },
                    iconResId = R.drawable.fcb_ic
                )

                Spacer(modifier = Modifier.height(if (isSmallScreen) 8.dp else 10.dp))

                // Login link
                Row(
                    modifier = Modifier.padding(bottom = if (isSmallScreen) 16.dp else 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Sudah Punya Akun? ",
                        style = TextStyle(
                            fontSize = if (isTablet) 16.sp else 14.sp,
                            fontFamily = PJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    )

                    Text(
                        "Masuk disini",
                        style = TextStyle(
                            fontSize = if (isTablet) 16.sp else 14.sp,
                            fontFamily = PJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3F6B1B)
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate("login")
                        }
                    )
                }

                // Add extra space at bottom for scroll
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        navController = rememberNavController(),
        onSignupResult = { isSuccess, message ->
            // Handle result here
        },
        onGoogleSignInClick = { /* Handle Google Sign-In click */ }
    )
}

@Preview(showBackground = true, widthDp = 600, heightDp = 800)
@Composable
fun SignupScreenTabletPreview() {
    SignupScreen(
        navController = rememberNavController(),
        onSignupResult = { isSuccess, message ->
            // Handle result here
        },
        onGoogleSignInClick = { /* Handle Google Sign-In click */ }
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SignupScreenSmallPreview() {
    SignupScreen(
        navController = rememberNavController(),
        onSignupResult = { isSuccess, message ->
            // Handle result here
        },
        onGoogleSignInClick = { /* Handle Google Sign-In click */ }
    )
}