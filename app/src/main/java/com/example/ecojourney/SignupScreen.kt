package com.example.ecojourney

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
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
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(650.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ecowhite),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "EcoJourney",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PJakartaFontFamily,
                            color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Text(
                    "Daftar",
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F6B1B)
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 50.dp, bottom = 20.dp)
                )

                CTextField(title = "Nama Lengkap", hint = "Masukkan Nama Lengkap", value = name, onValueChange = { name = it })
                CTextField(title = "Email", hint = "Masukkan Email", value = email, onValueChange = { email = it })
                CTextFieldPass(title = "Password", hint = "Masukkan Kata Sandi", value = password, onValueChange = { password = it }, isPassword = true)
                CTextFieldPass(title = "Konfirmasi Kata Sandi", hint = "Konfirmasi Kata Sandi", value = confirmPassword, onValueChange = { confirmPassword = it }, isPassword = true)

                Spacer(modifier = Modifier.height(20.dp))

                if (isSignupButtonEnabled) {
                    CButton(
                        text = "Daftar",
                        onClick = {
                            coroutineScope.launch {
                                signupUser(name, email, password) { isSuccess, message ->
                                    if (isSuccess) {
                                        // Navigate to main screen
                                        navController.navigate("main") {
                                            popUpTo("signup") { inclusive = true }
                                        }
                                    } else {
                                        // Show error message
                                        errorMessage = message
                                    }
                                }
                            }
                        }
                    )
                } else {
                    CButton2(text = "Daftar")
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OrDivider()

                Spacer(modifier = Modifier.height(10.dp))

                CButtonOutlinedG(
                    text = "Masuk dengan Google",
                    onClick = {
                        onGoogleSignInClick()
                    },
                    iconResId = R.drawable.google_ic
                )

                Spacer(modifier = Modifier.height(8.dp))

                CButtonOutlinedF(
                    text = "Masuk dengan Facebook",
                    onClick = {
                        // Handle login with Facebook
                    },
                    iconResId = R.drawable.fcb_ic
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Text(
                        "Belum Punya Akun? ",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    )

                    Text(
                        "Masuk disini",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3F6B1B)
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate("login")
                        }
                    )
                }

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
            // Handle result here, e.g., show a Toast in a real app
        },
        onGoogleSignInClick = { /* Handle Google Sign-In click */ }
    )
}