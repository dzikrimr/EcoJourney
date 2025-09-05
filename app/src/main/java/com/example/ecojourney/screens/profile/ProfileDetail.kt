package com.example.ecojourney.screens.profile

import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.AsyncImage
import com.example.ecojourney.ui.CButton
import com.example.ecojourney.ui.CTextField
import com.example.ecojourney.ui.CustomSpinnerDropdown
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ProfileDetail(navController: NavHostController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Initialize Cloudinary (do this once, preferably in Application class)
    LaunchedEffect(Unit) {
        try {
            val config = mapOf(
                "cloud_name" to "dvwbrl4el",
                "api_key" to "775312872444875",
                "api_secret" to "ZGxDCAjtppiF6lPKuuYxK5-R0kU"
            )
            MediaManager.init(context, config)
        } catch (e: Exception) {
            Log.e("ProfileDetail", "Error initializing Cloudinary: ${e.message}")
        }
    }

    // Open gallery launcher
    val openGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
        }
    }

    // Fetch current user details
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser
                val userEmail = user?.email ?: ""
                val uid = user?.uid ?: ""
                email = userEmail // Set the mutable state variable

                getUserDetails(userEmail, uid) { fetchedName, fetchedVehicleType, fetchedImageUrl ->
                    name = fetchedName
                    selectedItem = fetchedVehicleType
                    currentImageUrl = fetchedImageUrl.takeIf { it.isNotEmpty() }
                }
            } catch (e: Exception) {
                Log.e("ProfileDetail", "Error fetching user details: ${e.message}")
            }
        }
    }

    // Show success message effect
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            kotlinx.coroutines.delay(2000)
            showSuccessMessage = false
            navController.popBackStack()
        }
    }

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.header_img),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 5.65f)
                    .align(Alignment.TopCenter)
            )

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

                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    when {
                        imageUri != null -> {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFDF8B)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        currentImageUrl != null -> {
                            AsyncImage(
                                model = currentImageUrl,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFDF8B)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Image(
                                painter = painterResource(id = R.drawable.img_default),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFDF8B))
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, shape = CircleShape)
                            .clickable {
                                openGalleryLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pencil),
                            contentDescription = "Edit",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

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

                CTextField(
                    value = email,
                    onValueChange = { /* Email is read-only */ },
                    hint = "Email",
                    title = "Email",
                    isReadOnly = true,
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
                    width = 350.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                CButton(
                    onClick = {
                        if (name.isNotBlank() && selectedItem.isNotBlank()) {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    if (imageUri != null) {
                                        // Upload image to Cloudinary, then update profile
                                        uploadImageToCloudinary(context, imageUri!!) { downloadUrl ->
                                            updateUserProfile(name, email, selectedItem, downloadUrl) {
                                                isLoading = false
                                                showSuccessMessage = true
                                                currentImageUrl = downloadUrl // Update local state
                                            }
                                        }
                                    } else {
                                        // Update profile without changing image
                                        updateUserProfile(name, email, selectedItem, currentImageUrl ?: "") {
                                            isLoading = false
                                            showSuccessMessage = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("ProfileDetail", "Error updating profile: ${e.message}")
                                    isLoading = false
                                }
                            }
                        }
                    },
                    text = if (isLoading) "Menyimpan..." else "Simpan"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (showSuccessMessage) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = "Profil berhasil diperbarui!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getUserDetails(email: String, uid: String, onComplete: (String, String, String) -> Unit) {
    val db = Firebase.firestore
    val userDocRefByEmail = db.collection("users").document(email)
    val userDocRefByUid = db.collection("users").document(uid)

    // Try fetching by email first
    userDocRefByEmail.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val name = documentSnapshot.getString("name") ?: ""
                val vehicleType = documentSnapshot.getString("vehicleType") ?: ""
                val profileImageUrl = documentSnapshot.getString("profileImageUrl") ?: ""
                Log.d("ProfileDetail", "Fetched data by email: name=$name, vehicleType=$vehicleType, imageUrl=$profileImageUrl")
                onComplete(name, vehicleType, profileImageUrl)
            } else {
                // Try UID if email document doesn't exist
                userDocRefByUid.get()
                    .addOnSuccessListener { uidDocumentSnapshot ->
                        val name = uidDocumentSnapshot.getString("name") ?: ""
                        val vehicleType = uidDocumentSnapshot.getString("vehicleType") ?: ""
                        val profileImageUrl = uidDocumentSnapshot.getString("profileImageUrl") ?: ""
                        Log.d("ProfileDetail", "Fetched data by UID: name=$name, vehicleType=$vehicleType, imageUrl=$profileImageUrl")
                        onComplete(name, vehicleType, profileImageUrl)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ProfileDetail", "Error fetching user details by UID: ${exception.message}")
                        onComplete("", "", "")
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.e("ProfileDetail", "Error fetching user details by email: ${exception.message}")
            // Try UID as fallback
            userDocRefByUid.get()
                .addOnSuccessListener { uidDocumentSnapshot ->
                    val name = uidDocumentSnapshot.getString("name") ?: ""
                    val vehicleType = uidDocumentSnapshot.getString("vehicleType") ?: ""
                    val profileImageUrl = uidDocumentSnapshot.getString("profileImageUrl") ?: ""
                    Log.d("ProfileDetail", "Fetched data by UID: name=$name, vehicleType=$vehicleType, imageUrl=$profileImageUrl")
                    onComplete(name, vehicleType, profileImageUrl)
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileDetail", "Error fetching user details by UID: ${exception.message}")
                    onComplete("", "", "")
                }
        }
}

fun updateUserProfile(name: String, email: String, vehicleType: String, imageUrl: String = "", onComplete: () -> Unit) {
    if (email.isBlank()) {
        onComplete()
        return
    }

    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid ?: email
    val userDocRefByEmail = db.collection("users").document(email)
    val userDocRefByUid = db.collection("users").document(uid)

    val updateData = mutableMapOf<String, Any>(
        "fullName" to name, // Use fullName consistently
        "vehicleType" to vehicleType
    )
    if (imageUrl.isNotEmpty()) {
        updateData["profileImageUrl"] = imageUrl
    }

    // Try updating by email first
    userDocRefByEmail.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                userDocRefByEmail.update(updateData)
                    .addOnSuccessListener {
                        Log.d("ProfileDetail", "Profile successfully updated by email")
                        onComplete()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ProfileDetail", "Error updating profile by email: ${exception.message}")
                        onComplete()
                    }
            } else {
                // Try UID if email document doesn't exist
                userDocRefByUid.set(updateData)
                    .addOnSuccessListener {
                        Log.d("ProfileDetail", "Profile successfully updated by UID")
                        onComplete()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ProfileDetail", "Error updating profile by UID: ${exception.message}")
                        onComplete()
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.e("ProfileDetail", "Error checking email document: ${exception.message}")
            // Try UID as fallback
            userDocRefByUid.set(updateData)
                .addOnSuccessListener {
                    Log.d("ProfileDetail", "Profile successfully updated by UID")
                    onComplete()
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileDetail", "Error updating profile by UID: ${exception.message}")
                    onComplete()
                }
        }
}

fun uploadImageToCloudinary(context: android.content.Context, uri: Uri, onComplete: (String) -> Unit) {
    try {
        // Convert Uri to File path
        val filePath = getRealPathFromURI(context, uri) ?: run {
            Log.e("ProfileDetail", "Failed to get file path from URI")
            onComplete("")
            return
        }

        // Upload to Cloudinary using unsigned upload preset
        MediaManager.get().upload(filePath)
            .unsigned("ecojourn") // Your upload preset name
            .option("public_id", "profile_images/${UUID.randomUUID()}")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("ProfileDetail", "Upload started: $requestId")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    // Optional: Handle progress
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val secureUrl = resultData["secure_url"]?.toString() ?: ""
                    Log.d("ProfileDetail", "Image uploaded successfully: $secureUrl")
                    onComplete(secureUrl)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Log.e("ProfileDetail", "Error uploading image: ${error.description}")
                    onComplete("")
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    Log.w("ProfileDetail", "Upload rescheduled: ${error.description}")
                }
            })
            .dispatch(context)
    } catch (e: Exception) {
        Log.e("ProfileDetail", "Error initiating Cloudinary upload: ${e.message}")
        onComplete("")
    }
}

fun getRealPathFromURI(context: android.content.Context, uri: Uri): String? {
    var path: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val idx = it.getColumnIndex(android.provider.MediaStore.Images.Media.DATA)
            if (idx != -1) {
                path = it.getString(idx)
            }
        }
    }
    return path
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun ProfileDetailPreview() {
    val navController = rememberNavController()
    ProfileDetail(navController)
}