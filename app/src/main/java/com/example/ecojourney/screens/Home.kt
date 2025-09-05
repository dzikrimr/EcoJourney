package com.example.ecojourney.screens
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.ui.imageslider.ImageSlider
import com.example.ecojourney.ui.progressbar.AnimatedCircularProgressBar
import com.example.ecojourney.ui.progressbar.AnimatedCircularProgressBarBase
import com.example.ecojourney.ui.progressbar.SmallCircle
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.example.ecojourney.ui.CustomSpinnerDropdown
import com.example.ecojourney.ui.CustomTextField
import com.example.ecojourney.services.GPSTrackingService
import com.example.ecojourney.utils.LocationPermissionManager
import com.example.ecojourney.utils.TrackingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
        "Motor" -> 0.1f
        "Mobil" -> 0.2f
        "Truk" -> 0.3f
        else -> 0.0f
    }
    return baseEmissionFactor * distance * (capacity / 1000) * (speed / 60)
}
suspend fun saveUserInputToFirebase(
    userId: String,
    vehicleType: String,
    distance: String,
    capacity: String,
    speed: String,
    carbonFootprint: Float,
    isAutoTracking: Boolean = false
): Boolean {
    return try {
        val db = FirebaseFirestore.getInstance()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val dailyPath = "carbonFootprint.daily.$currentDate"
        val monthlyPath = "carbonFootprint.monthly.$currentMonth"
        val vehicleData = hashMapOf(
            "vehicleType" to vehicleType,
            "distance" to distance,
            "capacity" to capacity,
            "speed" to speed,
            "carbonFootprint" to carbonFootprint,
            "timestamp" to System.currentTimeMillis(),
            "isAutoTracking" to isAutoTracking
        )
        val documentSnapshot = db.collection("users").document(userId).get().await()
        if (documentSnapshot.exists()) {
            val currentHistory = documentSnapshot.get("vehicleHistory") as? List<Map<String, Any>> ?: emptyList()
            val updatedHistory = currentHistory + vehicleData
            db.collection("users").document(userId)
                .update(
                    mapOf(
                        "vehicleType" to vehicleType,
                        "distance" to distance,
                        "capacity" to capacity,
                        "speed" to speed,
                        "carbonFootprintResult" to FieldValue.increment(carbonFootprint.toDouble()),
                        "vehicleHistory" to updatedHistory,
                        dailyPath to FieldValue.increment(carbonFootprint.toDouble()),
                        monthlyPath to FieldValue.increment(carbonFootprint.toDouble())
                    )
                ).await()
        } else {
            val initialData = hashMapOf(
                "vehicleType" to vehicleType,
                "distance" to distance,
                "capacity" to capacity,
                "speed" to speed,
                "carbonFootprintResult" to carbonFootprint.toDouble(),
                dailyPath to carbonFootprint.toDouble(),
                monthlyPath to carbonFootprint.toDouble(),
                "vehicleHistory" to listOf(vehicleData)
            )
            db.collection("users").document(userId).set(initialData).await()
        }
        Log.d("Firebase", "Data saved successfully")
        true
    } catch (e: Exception) {
        Log.e("Firebase", "Failed to save data", e)
        false
    }
}
suspend fun getTotalCarbonFootprint(userId: String): Float {
    return try {
        val db = FirebaseFirestore.getInstance()
        val documentSnapshot = db.collection("users").document(userId).get().await()
        documentSnapshot.getDouble("carbonFootprintResult")?.toFloat() ?: 0f
    } catch (e: Exception) {
        Log.e("Firebase", "Failed to get carbon footprint", e)
        0f
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
                        onResult(name ?: "User")
                    } else {
                        Log.d("GetUserName", "User document does not exist")
                        onResult("User")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("GetUserName", "Failed to fetch user name", e)
                    onResult("User")
                }
        }
    } else {
        Log.d("GetUserName", "No user is signed in")
        onResult("User")
    }
}
@Composable
fun Home(
    navController: NavHostController,
    context: Context,
    isPreview: Boolean = false
) {
    val userName = remember { mutableStateOf("") }
    val spinnerItems = listOf("Motor", "Mobil", "Truk")
    var selectedItem by remember { mutableStateOf("") }
    var jarakTempuh by remember { mutableStateOf("") }
    var kapasitas by remember { mutableStateOf("") }
    var kecepatan by remember { mutableStateOf("") }
    // GPS Tracking states
    var trackingState by remember { mutableStateOf(TrackingState()) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    val isInputEmpty = jarakTempuh.isEmpty() || kapasitas.isEmpty() || kecepatan.isEmpty() || selectedItem.isEmpty()
    val isAutoCheckEnabled = selectedItem.isNotEmpty() && kapasitas.isNotEmpty()
    var totalCarbonFootprint by remember { mutableStateOf(0f) }
    val maxCarbon = 26f
    val coroutineScope = rememberCoroutineScope()
    val hasCalculated = totalCarbonFootprint > 0
    var carbonResultText by remember {
        mutableStateOf(AnnotatedString(""))
    }
    // Broadcast receiver untuk menerima update dari GPS service
    val trackingReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == GPSTrackingService.BROADCAST_TRACKING_UPDATE) {
                    val distance = intent.getDoubleExtra(GPSTrackingService.EXTRA_DISTANCE, 0.0)
                    val speed = intent.getFloatExtra(GPSTrackingService.EXTRA_SPEED, 0f)
                    val averageSpeed = intent.getFloatExtra(GPSTrackingService.EXTRA_AVERAGE_SPEED, 0f)
                    val duration = intent.getLongExtra(GPSTrackingService.EXTRA_DURATION, 0)
                    trackingState = trackingState.copy(
                        distance = distance,
                        currentSpeed = speed,
                        averageSpeed = averageSpeed,
                        duration = duration
                    )
                }
            }
        }
    }
    // Register/unregister broadcast receiver with proper flags for Android 14+
    LaunchedEffect(Unit) {
        val filter = IntentFilter(GPSTrackingService.BROADCAST_TRACKING_UPDATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(trackingReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            ContextCompat.registerReceiver(
                context,
                trackingReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            try {
                context.unregisterReceiver(trackingReceiver)
            } catch (e: IllegalArgumentException) {
                // Receiver sudah di-unregister
            }
        }
    }
    // Update carbon result text whenever totalCarbonFootprint changes
    LaunchedEffect(totalCarbonFootprint) {
        carbonResultText = buildAnnotatedString {
            if (totalCarbonFootprint > 0) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3F6B1B))) {
                    append("Kamu sudah menghasilkan karbon sebanyak ")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFFFBA00))) {
                    append("${"%.2f".format(totalCarbonFootprint)} kg ")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3F6B1B))) {
                    append("/ ${"%.2f".format(maxCarbon)} kg")
                }
            } else {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3F6B1B))) {
                    append("Segera Pantau jejak karbonmu sekarang!")
                }
            }
        }
    }
    suspend fun onCalculateCarbon(userId: String) {
        if (!isInputEmpty) {
            val distance = jarakTempuh.toFloatOrNull() ?: 0f
            val capacity = kapasitas.toFloatOrNull() ?: 0f
            val speed = kecepatan.toFloatOrNull() ?: 0f
            val carbonFootprint = calculateCarbonFootprint(selectedItem, distance, capacity, speed)
            if (isPreview) {
                // For preview, just simulate the calculation
                totalCarbonFootprint = carbonFootprint
                // Clear input fields
                selectedItem = ""
                jarakTempuh = ""
                kapasitas = ""
                kecepatan = ""
            } else {
                val success = saveUserInputToFirebase(
                    userId = userId,
                    vehicleType = selectedItem,
                    distance = jarakTempuh,
                    capacity = kapasitas,
                    speed = kecepatan,
                    carbonFootprint = carbonFootprint
                )
                if (success) {
                    // Refresh total carbon footprint from Firebase
                    totalCarbonFootprint = getTotalCarbonFootprint(userId)
                    // Clear input fields
                    selectedItem = ""
                    jarakTempuh = ""
                    kapasitas = ""
                    kecepatan = ""
                }
            }
        }
    }
    fun startGPSTracking() {
        if (!LocationPermissionManager.checkLocationPermissions(context)) {
            showPermissionDialog = true
            return
        }
        if (!LocationPermissionManager.isLocationEnabled(context)) {
            showLocationDialog = true
            return
        }
        // Start GPS tracking service
        val intent = Intent(context, GPSTrackingService::class.java).apply {
            action = GPSTrackingService.ACTION_START_TRACKING
        }
        context.startForegroundService(intent)
        trackingState = trackingState.copy(
            isTracking = true,
            startTime = System.currentTimeMillis()
        )
        Log.d("GPSTracking", "Started GPS tracking")
    }
    fun stopGPSTracking() {
        val intent = Intent(context, GPSTrackingService::class.java).apply {
            action = GPSTrackingService.ACTION_STOP_TRACKING
        }
        context.startService(intent)
        // Gunakan data tracking untuk perhitungan otomatis
        if (trackingState.distance > 0 && trackingState.averageSpeed > 0) {
            jarakTempuh = String.format("%.2f", trackingState.distance)
            kecepatan = String.format("%.0f", trackingState.averageSpeed)
            // Auto calculate dengan data GPS
            coroutineScope.launch {
                if (selectedItem.isNotEmpty() && kapasitas.isNotEmpty()) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                    if (userId.isNotEmpty()) {
                        val distance = trackingState.distance.toFloat()
                        val capacity = kapasitas.toFloatOrNull() ?: 0f
                        val speed = trackingState.averageSpeed
                        val carbonFootprint = calculateCarbonFootprint(selectedItem, distance, capacity, speed)
                        val success = saveUserInputToFirebase(
                            userId = userId,
                            vehicleType = selectedItem,
                            distance = String.format("%.2f", distance),
                            capacity = kapasitas,
                            speed = String.format("%.0f", speed),
                            carbonFootprint = carbonFootprint,
                            isAutoTracking = true
                        )
                        if (success) {
                            totalCarbonFootprint = getTotalCarbonFootprint(userId)
                            // Clear input fields
                            selectedItem = ""
                            jarakTempuh = ""
                            kapasitas = ""
                            kecepatan = ""
                        }
                    }
                }
            }
        }
        trackingState = TrackingState()
        Log.d("GPSTracking", "Stopped GPS tracking")
    }
    val userId = if (isPreview) "preview_user" else FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    // Load user name and total carbon footprint on component initialization
    LaunchedEffect(Unit) {
        if (isPreview) {
            userName.value = "Preview User"
            totalCarbonFootprint = 5.2f // Sample data for preview
        } else {
            getUserName { name ->
                userName.value = if (name.isNotEmpty()) name else "User"
            }
            if (userId.isNotEmpty()) {
                totalCarbonFootprint = getTotalCarbonFootprint(userId)
            }
        }
    }
    // Debug: Log tracking state changes
    LaunchedEffect(trackingState) {
        Log.d("GPSTracking", "TrackingState changed: isTracking=${trackingState.isTracking}, distance=${trackingState.distance}, speed=${trackingState.currentSpeed}, avgSpeed=${trackingState.averageSpeed}, duration=${trackingState.duration}")
    }
    // Permission Dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Izin Lokasi Diperlukan") },
            text = { Text("Aplikasi memerlukan izin akses lokasi untuk melacak perjalanan Anda secara otomatis.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    if (context is Activity) {
                        LocationPermissionManager.requestLocationPermissions(context)
                    }
                }) {
                    Text("Berikan Izin")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
    // Location Settings Dialog
    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("GPS Tidak Aktif") },
            text = { Text("Aktifkan GPS untuk melacak perjalanan Anda secara otomatis.") },
            confirmButton = {
                TextButton(onClick = {
                    showLocationDialog = false
                    LocationPermissionManager.openLocationSettings(context)
                }) {
                    Text("Buka Pengaturan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
    // Main UI Layout - Fixed structure
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Fixed Header Section (Non-scrollable)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp) // Fixed height for header
                .padding(top = 20.dp, end = 30.dp)
        ) {
            // Notification Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color = Color(0xFF3F6B1B), shape = CircleShape)
                    .align(Alignment.TopEnd)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.notiff_ic),
                    contentDescription = "Notification Icon",
                    modifier = Modifier
                        .size(25.dp)
                        .align(Alignment.Center)
                )
            }
            // User Greeting
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
        }
        // Scrollable Content Section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 90.dp, // Space for fixed header
                    start = 30.dp,
                    end = 30.dp
                )
                .verticalScroll(rememberScrollState())
        ) {
            // Image Slider
            ImageSlider(
                imageResIds = listOf(
                    R.drawable.banner1,
                    R.drawable.banner2
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(15.dp))
            // Main Card Content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        if (hasCalculated) {
                            AnimatedCircularProgressBar(
                                progress = totalCarbonFootprint / maxCarbon,
                                radius = 60.dp,
                                strokeWidth = 14.dp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            AnimatedCircularProgressBarBase(
                                radius = 60.dp,
                                strokeWidth = 14.dp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        if (hasCalculated) {
                            SmallCircle(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                            ) {
                                if (!isPreview) {
                                    navController.navigate("homeDetail/$userId")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    // Carbon Footprint Result
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
                    // Input Fields
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
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
                                modifier = Modifier.padding(bottom = 10.dp),
                                enabled = !trackingState.isTracking
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
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
                                onTextChanged = { kecepatan = it },
                                modifier = Modifier.padding(bottom = 10.dp),
                                enabled = !trackingState.isTracking
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // Tracking Information (Displayed during GPS tracking)
                    if (trackingState.isTracking) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Sedang Melacak...",
                                    fontSize = 14.sp,
                                    fontFamily = PJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3F6B1B)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text(
                                        text = "Jarak: ${trackingState.getFormattedDistance()}",
                                        fontSize = 12.sp,
                                        fontFamily = PJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Kecepatan: ${trackingState.getFormattedSpeed()}",
                                        fontSize = 12.sp,
                                        fontFamily = PJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Durasi: ${trackingState.getFormattedDuration()}",
                                    fontSize = 12.sp,
                                    fontFamily = PJakartaSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { stopGPSTracking() },
                                    shape = RoundedCornerShape(35.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFD32F2F)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .padding(horizontal = 10.dp)
                                ) {
                                    Text(
                                        text = "Hentikan Pelacakan",
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
                    Spacer(modifier = Modifier.height(10.dp))
                    // Manual Calculation Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                onCalculateCarbon(userId)
                            }
                        },
                        enabled = !isInputEmpty && !trackingState.isTracking,
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInputEmpty || trackingState.isTracking) Color(
                                0xFF555555
                            ) else Color(0xFF3F6B1B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(horizontal = 10.dp)
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
                    Spacer(modifier = Modifier.height(6.dp))
                    // Auto Check (GPS Tracking) Button
                    Button(
                        onClick = {
                            startGPSTracking()
                        },
                        enabled = isAutoCheckEnabled && !trackingState.isTracking,
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isAutoCheckEnabled || trackingState.isTracking) Color(
                                0xFF555555
                            ) else Color(0xFF3F6B1B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(
                            text = "Cek Otomatis",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PJakartaFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    // Hint for Auto Check
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Info icon",
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cek Otomatis dengan isi Jenis Kendaraan dan Kapasitas",
                            fontSize = 8.sp,
                            fontFamily = PJakartaSansFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFA5A5A5),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
            // Additional bottom padding for scrollable content
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomePreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current
    Home(
        context = context,
        navController = navController,
        isPreview = isInPreview
    )
}