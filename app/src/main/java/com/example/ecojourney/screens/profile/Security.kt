package com.example.ecojourney.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import android.util.Log
import com.example.ecojourney.ui.CTextField

@Composable
fun SecurityScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Clear messages after a delay
    LaunchedEffect(successMessage, errorMessage) {
        if (successMessage.isNotEmpty() || errorMessage.isNotEmpty()) {
            kotlinx.coroutines.delay(3000)
            successMessage = ""
            errorMessage = ""
        }
    }

    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Header Image
            Image(
                painter = painterResource(id = R.drawable.header_img),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 5.65f)
                    .align(Alignment.TopCenter)
            )

            // Back Button and Title
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp, top = 35.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.back_arrow),
                                contentDescription = "Kembali",
                                tint = Color(0xFF3F6B1B)
                            )
                        }
                    }
                }

                Text(
                    text = "Keamanan",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .padding(end = 30.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 150.dp)
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ganti Kata Sandi",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F6B1B),
                        fontFamily = PJakartaFontFamily
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Current Password
                CTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    hint = "Kata Sandi Saat Ini",
                    title = "Kata Sandi Saat Ini",
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                // New Password
                CTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    hint = "Kata Sandi Baru",
                    title = "Kata Sandi Baru",
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Confirm New Password
                CTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    hint = "Konfirmasi Kata Sandi Baru",
                    title = "Konfirmasi Kata Sandi Baru",
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Error Message
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = PJakartaSansFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Success Message
                if (successMessage.isNotEmpty()) {
                    Text(
                        text = successMessage,
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        fontFamily = PJakartaSansFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Change Password Button
                Button(
                    onClick = {
                        if (newPassword != confirmPassword) {
                            errorMessage = "Kata sandi baru tidak cocok dengan konfirmasi"
                            return@Button
                        }
                        if (newPassword.length < 6) {
                            errorMessage = "Kata sandi baru harus minimal 6 karakter"
                            return@Button
                        }
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val user = FirebaseAuth.getInstance().currentUser
                                if (user != null) {
                                    // Re-authenticate user
                                    val credential = com.google.firebase.auth.EmailAuthProvider
                                        .getCredential(user.email ?: "", currentPassword)
                                    user.reauthenticate(credential).addOnSuccessListener {
                                        user.updatePassword(newPassword).addOnSuccessListener {
                                            successMessage = "Kata sandi berhasil diperbarui"
                                            currentPassword = ""
                                            newPassword = ""
                                            confirmPassword = ""
                                            isLoading = false
                                        }.addOnFailureListener { e ->
                                            errorMessage = "Gagal memperbarui kata sandi: ${e.message}"
                                            isLoading = false
                                        }
                                    }.addOnFailureListener { e ->
                                        errorMessage = "Kata sandi saat ini salah: ${e.message}"
                                        isLoading = false
                                    }
                                } else {
                                    errorMessage = "Pengguna tidak ditemukan"
                                    isLoading = false
                                }
                            } catch (e: Exception) {
                                errorMessage = "Terjadi kesalahan: ${e.message}"
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F6B1B),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLoading) "Menyimpan..." else "Ganti Kata Sandi",
                        fontFamily = PJakartaSansFontFamily,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Delete Account Button
                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Hapus Akun",
                        color = Color.Red,
                        fontSize = 16.sp,
                        fontFamily = PJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Delete Account Confirmation Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(
                            text = "Konfirmasi Hapus Akun",
                            fontFamily = PJakartaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Text(
                            text = "Apakah Anda yakin ingin menghapus akun Anda? Tindakan ini tidak dapat dibatalkan.",
                            fontFamily = PJakartaSansFontFamily,
                            fontSize = 14.sp
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        val user = FirebaseAuth.getInstance().currentUser
                                        if (user != null) {
                                            val email = user.email ?: ""
                                            val uid = user.uid
                                            val db = Firebase.firestore
                                            val userDocRefByEmail = db.collection("users").document(email)
                                            val userDocRefByUid = db.collection("users").document(uid)

                                            userDocRefByEmail.get()
                                                .addOnSuccessListener { documentSnapshot ->
                                                    if (documentSnapshot.exists()) {
                                                        userDocRefByEmail.delete()
                                                            .addOnSuccessListener {
                                                                user.delete().addOnSuccessListener {
                                                                    navController.navigate("login") {
                                                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                                                    }
                                                                    isLoading = false
                                                                    showDeleteDialog = false
                                                                }.addOnFailureListener { e ->
                                                                    errorMessage = "Gagal menghapus akun: ${e.message}"
                                                                    isLoading = false
                                                                    showDeleteDialog = false
                                                                }
                                                            }
                                                            .addOnFailureListener { e ->
                                                                errorMessage = "Gagal menghapus data: ${e.message}"
                                                                isLoading = false
                                                                showDeleteDialog = false
                                                            }
                                                    } else {
                                                        userDocRefByUid.delete()
                                                            .addOnSuccessListener {
                                                                user.delete().addOnSuccessListener {
                                                                    navController.navigate("login") {
                                                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                                                    }
                                                                    isLoading = false
                                                                    showDeleteDialog = false
                                                                }.addOnFailureListener { e ->
                                                                    errorMessage = "Gagal menghapus akun: ${e.message}"
                                                                    isLoading = false
                                                                    showDeleteDialog = false
                                                                }
                                                            }
                                                            .addOnFailureListener { e ->
                                                                errorMessage = "Gagal menghapus data: ${e.message}"
                                                                isLoading = false
                                                                showDeleteDialog = false
                                                            }
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e("SecurityScreen", "Error checking email document: ${e.message}")
                                                    userDocRefByUid.delete()
                                                        .addOnSuccessListener {
                                                            user.delete().addOnSuccessListener {
                                                                navController.navigate("login") {
                                                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                                                }
                                                                isLoading = false
                                                                showDeleteDialog = false
                                                            }.addOnFailureListener { e ->
                                                                errorMessage = "Gagal menghapus akun: ${e.message}"
                                                                isLoading = false
                                                                showDeleteDialog = false
                                                            }
                                                        }
                                                        .addOnFailureListener { e ->
                                                            errorMessage = "Gagal menghapus data: ${e.message}"
                                                            isLoading = false
                                                            showDeleteDialog = false
                                                        }
                                                }
                                        } else {
                                            errorMessage = "Pengguna tidak ditemukan"
                                            isLoading = false
                                            showDeleteDialog = false
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Terjadi kesalahan: ${e.message}"
                                        isLoading = false
                                        showDeleteDialog = false
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "Hapus",
                                color = Color.Red,
                                fontFamily = PJakartaSansFontFamily
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text(
                                text = "Batal",
                                fontFamily = PJakartaSansFontFamily
                            )
                        }
                    },
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    textContentColor = Color.Black
                )
            }

            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF3F6B1B))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640, device = Devices.PIXEL_4)
@Composable
fun SecurityScreenPreview() {
    SecurityScreen(navController = rememberNavController())
}