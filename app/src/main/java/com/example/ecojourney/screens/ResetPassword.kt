package com.example.ecojourney.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.ui.CButton
import com.example.ecojourney.ui.CButton2
import com.example.ecojourney.ui.CTextField
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ResetPassword(
    navController: NavHostController,
    onLoginResult: (Boolean, String?) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

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
                    "Reset Kata Sandi",
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F6B1B)
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 120.dp, bottom = 10.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        "Silakan masukkan email untuk meminta pengaturan kata sandi",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = PJakartaSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF7F7F7F)
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 5.dp, bottom = 40.dp)
                    )

                    CTextField(
                        title = "",
                        hint = "Masukkan Email",
                        value = email,
                        onValueChange = { email = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (email.isNotEmpty()) {
                        CButton(text = "Kirim", onClick = {
                            coroutineScope.launch {
                                sendPasswordResetEmail(email) { success, error ->
                                    if (success) {
                                        Toast.makeText(
                                            navController.context,
                                            "Reset link has been sent.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.navigate("login") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        errorMessage = error
                                    }
                                }
                            }
                        })
                    } else {
                        CButton2(text = "Kirim")
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            style = TextStyle(fontSize = 14.sp),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// Function to send password reset email using FirebaseAuth
fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null)
            } else {
                onResult(false, task.exception?.message)
            }
        }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ResetPasswordPreview() {
    ResetPassword(navController = rememberNavController(),
        onLoginResult = { isSuccess, message ->

        }
    )
}
