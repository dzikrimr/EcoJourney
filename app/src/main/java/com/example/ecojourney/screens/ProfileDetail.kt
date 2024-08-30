package com.example.ecojourney.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.ecojourney.CButton
import com.example.ecojourney.CTextField
import com.example.ecojourney.spinner.CustomSpinnerDropdown
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Composable
fun ProfileDetail(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Function to open gallery
    val openGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            uploadImageToFirebase(uri) { downloadUrl ->
                saveUserProfileImage(downloadUrl, name, email)
            }
        }
    }

    // Fetch current user details
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Fetch current user email
                val currentEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                email = currentEmail

                // Fetch current user name and vehicle type from Firebase
                getUserDetails { fetchedName, fetchedVehicleType ->
                    name = fetchedName
                    selectedItem = fetchedVehicleType
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image with aspect ratio
            Image(
                painter = painterResource(id = R.drawable.header_img),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 5.65f)
                    .align(Alignment.TopCenter)
            )

            // Overlay content with text and back button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp, top = 40.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 0.dp),
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
            }

            // Center text and tabs below it
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Profil",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold,
                    )
                )

                Spacer(modifier = Modifier.height(50.dp))

                // Circular Image before text fields
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.BottomEnd // Align the pencil icon to the bottom end
                ) {
                    Image(
                        painter = if (imageUri != null) {
                            rememberImagePainter(data = imageUri)
                        } else {
                            painterResource(id = R.drawable.img_default)
                        },
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFDF8B))
                    )

                    // Pencil icon in the bottom right corner
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, shape = CircleShape)
                            .clickable {
                                openGalleryLauncher.launch("image/*")
                            }, // Handle the click event
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pencil), // Replace with your pencil icon resource
                            contentDescription = "Edit",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Name TextField
                CTextField(
                    value = name,
                    onValueChange = { name = it },
                    hint = "Nama Panggilan",
                    title = "Nama Panggilan",
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Display email as a read-only TextField
                CTextField(
                    value = email,
                    onValueChange = { /* Email is read-only, no action here */ },
                    hint = "Email",
                    title = "Email",
                    isReadOnly = true, // Set email field as read-only
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Jenis Kendaraan",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = PJakartaFontFamily
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))

                CustomSpinnerDropdown(
                    items = listOf("Motor", "Mobil", "Truk"),
                    selectedItem = selectedItem,
                    onItemSelected = { selectedItem = it },
                    hint = "Select an option",
                    width = 300.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
                Spacer(modifier = Modifier.height(100.dp))

                CButton(
                    onClick = {
                        // Implement the action to update the name, vehicle type, and image in Firebase
                        updateUserProfile(name, email, selectedItem)
                    },
                    text = "Simpan"
                )
            }
        }
    }
}


fun updateUserProfile(name: String, email: String, vehicleType: String) {
    if (email.isBlank()) {
        // Handle the case where the email is empty or invalid
        return
    }

    val db = Firebase.firestore
    val userDocRef = db.collection("users").document(email)

    userDocRef.update("name", name, "vehicleType", vehicleType)
        .addOnSuccessListener {
            // Name and vehicle type successfully updated
        }
        .addOnFailureListener { exception ->
            if (exception is com.google.firebase.firestore.FirebaseFirestoreException && exception.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.NOT_FOUND) {
                // Document not found, create a new one
                userDocRef.set(mapOf("name" to name, "profileImageUrl" to "", "vehicleType" to vehicleType))
                    .addOnSuccessListener {
                        // Document created successfully
                    }
                    .addOnFailureListener {
                        // Handle failures
                    }
            } else {
                // Handle other types of failures
            }
        }
}

fun getUserDetails(onComplete: (String, String) -> Unit) {
    val db = Firebase.firestore
    val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val userDocRef = db.collection("users").document(email)

    userDocRef.get()
        .addOnSuccessListener { documentSnapshot ->
            val name = documentSnapshot.getString("name") ?: ""
            val vehicleType = documentSnapshot.getString("vehicleType") ?: ""
            onComplete(name, vehicleType)
        }
        .addOnFailureListener {
            // Handle failures
            onComplete("", "")
        }
}


// Function to upload image to Firebase
fun uploadImageToFirebase(uri: Uri, onComplete: (String) -> Unit) {
    val storageRef = Firebase.storage.reference
    val profileImageRef = storageRef.child("profile_images/${UUID.randomUUID()}.jpg")

    profileImageRef.putFile(uri)
        .addOnSuccessListener {
            profileImageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                onComplete(downloadUrl.toString())
            }
        }
        .addOnFailureListener {
            // Handle failures
        }
}

fun saveUserProfileImage(imageUrl: String, name: String, email: String) {
    if (email.isBlank()) {
        // Handle the case where the email is empty or invalid
        return
    }

    val db = Firebase.firestore
    val userDocRef = db.collection("users").document(email)

    userDocRef.update("profileImageUrl", imageUrl)
        .addOnSuccessListener {
            // Image successfully updated
        }
        .addOnFailureListener {
            // Handle failures
        }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun ProfileDetailPreview() {
    val navController = rememberNavController() // Create a mock NavController for the preview
    ProfileDetail(navController)
}
