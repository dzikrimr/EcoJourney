package com.example.ecojourney.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.ui.ClickableTabs
import com.example.ecojourney.ui.progressbar.StickProgressBar
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.format.TextStyle as DateFormatTextStyle

suspend fun fetchCarbonFootprintResult(userId: String): Float {
    val db = FirebaseFirestore.getInstance()
    val documentSnapshot = db.collection("users").document(userId).get().await()
    return documentSnapshot.getDouble("carbonFootprintResult")?.toFloat() ?: 0f
}

suspend fun fetchVehicleHistory(userId: String): List<Map<String, Any>> {
    val db = FirebaseFirestore.getInstance()
    val documentSnapshot = db.collection("users").document(userId).get().await()
    return documentSnapshot.get("vehicleHistory") as? List<Map<String, Any>> ?: emptyList()
}

suspend fun fetchDailyCarbonFootprint(userId: String, date: String): Float {
    val db = FirebaseFirestore.getInstance()
    val documentSnapshot = db.collection("users").document(userId).get().await()
    val carbonFootprint = documentSnapshot.getDouble("carbonFootprint.daily.$date")?.toFloat() ?: 0f
    return carbonFootprint
}

suspend fun fetchMonthlyCarbonFootprint(userId: String, month: String): Float {
    val db = FirebaseFirestore.getInstance()
    val documentSnapshot = db.collection("users").document(userId).get().await()
    val carbonFootprint = documentSnapshot.getDouble("carbonFootprint.monthly.$month")?.toFloat() ?: 0f
    return carbonFootprint
}

@Composable
fun HomeDetail(navController: NavHostController, userId: String) {
    val selectedTabIndex = remember { mutableStateOf(0) }
    var vehicleHistory by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var totalCarbonFootprint by remember { mutableStateOf<Float?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            vehicleHistory = fetchVehicleHistory(userId)
            totalCarbonFootprint = fetchCarbonFootprintResult(userId)
        }
    }

    Surface(
        color = colorResource(id = R.color.white1100),
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
                        onClick = { navController.popBackStack() },
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
                    text = "Cek Jejak Carbon",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Spacer(modifier = Modifier.height(60.dp))
                ClickableTabs(
                    selectedItem = selectedTabIndex.value,
                    tabsList = listOf("Harian", "Bulanan"),
                    onClick = { index ->
                        selectedTabIndex.value = index
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                when (selectedTabIndex.value) {
                    0 -> HarianContent(userId = userId, vehicleHistory = vehicleHistory, totalCarbonFootprint = totalCarbonFootprint)
                    1 -> BulananContent(userId = userId, vehicleHistory = vehicleHistory, totalCarbonFootprint = totalCarbonFootprint)
                }
            }
        }
    }
}

@Composable
fun HarianContent(userId: String, vehicleHistory: List<Map<String, Any>>, totalCarbonFootprint: Float?) {
    val totalDays = 14
    val currentDate = LocalDate.now()
    val currentDayOfMonth = currentDate.dayOfMonth
    val currentMonth = currentDate.month.getDisplayName(DateFormatTextStyle.FULL, Locale("id", "ID"))
    val currentYear = currentDate.year
    val startDate = currentDate.minusDays(totalDays.toLong() - 1)
    val days = (0 until totalDays).map { startDate.plusDays(it.toLong()) }
    var selectedDay by remember { mutableStateOf(currentDayOfMonth) }
    var displayDate by remember { mutableStateOf("$currentDayOfMonth $currentMonth $currentYear") }
    val listState = rememberLazyListState()

    val selectedDateStr = days.find { it.dayOfMonth == selectedDay }?.toString() ?: ""
    val filteredHistory = vehicleHistory.filter {
        val timestamp = (it["timestamp"] as? Long) ?: 0L
        val entryDate = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC).toLocalDate().toString()
        entryDate == selectedDateStr
    }

    // Group by vehicle type and calculate total carbon footprint for each vehicle type
    val groupedVehicles = filteredHistory.groupBy { it["vehicleType"] as? String ?: "" }
        .mapValues { (_, entries) ->
            val totalCarbon = entries.sumOf {
                ((it["carbonFootprint"] as? Number)?.toFloat() ?: 0f).toDouble()
            }.toFloat()
            val latestEntry = entries.lastOrNull()
            mapOf(
                "vehicleType" to (latestEntry?.get("vehicleType") ?: ""),
                "distance" to (latestEntry?.get("distance") ?: ""),
                "capacity" to (latestEntry?.get("capacity") ?: ""),
                "speed" to (latestEntry?.get("speed") ?: ""),
                "carbonFootprint" to totalCarbon,
                "entryCount" to entries.size
            )
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Hari ini,",
            fontSize = 16.sp,
            fontFamily = PJakartaFontFamily,
            color = Color(0xFF3F6B1B),
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.Start)
        )
        Text(
            text = displayDate,
            fontSize = 20.sp,
            fontFamily = PJakartaFontFamily,
            color = Color(0xFF3F6B1B),
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            items(days) { date ->
                DayCard(
                    day = date.dayOfMonth,
                    dayOfWeek = date.dayOfWeek.getDisplayName(DateFormatTextStyle.FULL, Locale("id", "ID")),
                    isSelected = date.dayOfMonth == selectedDay,
                    onClick = {
                        selectedDay = date.dayOfMonth
                        displayDate = "${date.dayOfWeek.getDisplayName(DateFormatTextStyle.FULL, Locale("id", "ID"))}, ${date.dayOfMonth} $currentMonth $currentYear"
                    }
                )
                Spacer(modifier = Modifier.width(22.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Jejak Karbonmu!",
            fontSize = 16.sp,
            fontFamily = PJakartaFontFamily,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append("Kamu sudah menghasilkan karbon sebanyak ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFFFBA00))) {
                    append("${totalCarbonFootprint?.let { "%.2f".format(it) } ?: "0.00"} kg ")
                }
                withStyle(style = SpanStyle(color = Color(0xFF3F6B1B))) {
                    append("/ 26.00 kg")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(" total!")
                }
            },
            fontSize = 14.sp,
            fontFamily = PJakartaSansFontFamily,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        StickProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            currentValue = totalCarbonFootprint ?: 0f,
            maxValue = 26f,
            iconHeight = 25.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            items(groupedVehicles.entries.toList()) { (vehicleType, vehicleData) ->
                VehicleCard(
                    vehicleType = vehicleType,
                    distance = vehicleData["distance"] as? String ?: "",
                    capacity = vehicleData["capacity"] as? String ?: "",
                    speed = vehicleData["speed"] as? String ?: "",
                    carbonFootprint = vehicleData["carbonFootprint"] as? Float ?: 0f,
                    entryCount = vehicleData["entryCount"] as? Int ?: 0
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BulananContent(userId: String, vehicleHistory: List<Map<String, Any>>, totalCarbonFootprint: Float?) {
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    )
    val currentDate = LocalDate.now()
    val currentMonthIndex = currentDate.monthValue - 1
    val currentYear = currentDate.year
    var selectedMonth by remember { mutableStateOf(currentMonthIndex) }
    var displayMonth by remember { mutableStateOf("${months[currentMonthIndex]} $currentYear") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem(currentMonthIndex)
    }

    val selectedMonthStr = "${currentYear}-${String.format("%02d", selectedMonth + 1)}"
    val filteredHistory = vehicleHistory.filter {
        val timestamp = (it["timestamp"] as? Long) ?: 0L
        val entryMonth = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        entryMonth == selectedMonthStr
    }

    // Group by vehicle type and calculate total carbon footprint for each vehicle type
    val groupedVehicles = filteredHistory.groupBy { it["vehicleType"] as? String ?: "" }
        .mapValues { (_, entries) ->
            val totalCarbon = entries.sumOf {
                ((it["carbonFootprint"] as? Number)?.toFloat() ?: 0f).toDouble()
            }.toFloat()
            val latestEntry = entries.lastOrNull()
            mapOf(
                "vehicleType" to (latestEntry?.get("vehicleType") ?: ""),
                "distance" to (latestEntry?.get("distance") ?: ""),
                "capacity" to (latestEntry?.get("capacity") ?: ""),
                "speed" to (latestEntry?.get("speed") ?: ""),
                "carbonFootprint" to totalCarbon,
                "entryCount" to entries.size
            )
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Bulan ini,",
            fontSize = 16.sp,
            fontFamily = PJakartaFontFamily,
            color = Color(0xFF3F6B1B),
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.Start)
        )
        Text(
            text = displayMonth,
            fontSize = 20.sp,
            fontFamily = PJakartaFontFamily,
            color = Color(0xFF3F6B1B),
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            items(months.indices.toList()) { monthIndex ->
                MonthCard(
                    monthIndex = monthIndex,
                    monthName = months[monthIndex],
                    isSelected = monthIndex == selectedMonth,
                    onClick = {
                        selectedMonth = monthIndex
                        displayMonth = "${months[monthIndex]} $currentYear"
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Jejak Karbonmu!",
            fontSize = 16.sp,
            fontFamily = PJakartaFontFamily,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append("Kamu sudah menghasilkan karbon sebanyak ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFFFBA00))) {
                    append("${totalCarbonFootprint?.let { "%.2f".format(it) } ?: "0.00"} kg ")
                }
                withStyle(style = SpanStyle(color = Color(0xFF3F6B1B))) {
                    append("/ 780.00 kg")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(" total!")
                }
            },
            fontSize = 14.sp,
            fontFamily = PJakartaSansFontFamily,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        StickProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            currentValue = totalCarbonFootprint ?: 0f,
            maxValue = 780f,
            iconHeight = 25.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            items(groupedVehicles.entries.toList()) { (vehicleType, vehicleData) ->
                VehicleCard(
                    vehicleType = vehicleType,
                    distance = vehicleData["distance"] as? String ?: "",
                    capacity = vehicleData["capacity"] as? String ?: "",
                    speed = vehicleData["speed"] as? String ?: "",
                    carbonFootprint = vehicleData["carbonFootprint"] as? Float ?: 0f,
                    entryCount = vehicleData["entryCount"] as? Int ?: 0
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun VehicleCard(
    vehicleType: String,
    distance: String,
    capacity: String,
    speed: String,
    carbonFootprint: Float,
    entryCount: Int = 1
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFBA00), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = when (vehicleType.lowercase()) {
                        "motor" -> R.drawable.scooter
                        "mobil" -> R.drawable.car
                        "truk" -> R.drawable.truck
                        else -> R.drawable.scooter
                    }),
                    contentDescription = "$vehicleType icon",
                    modifier = Modifier.size(52.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = vehicleType.replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F6B1B)
                )
                Text(
                    text = "$capacity cc / $distance km",
                    fontSize = 10.sp,
                    fontFamily = PJakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    color = Color.Black
                )
                Text(
                    text = "$speed km/jam",
                    fontSize = 10.sp,
                    fontFamily = PJakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    color = Color.Black
                )
                if (entryCount > 1) {
                    Text(
                        text = "$entryCount perhitungan",
                        fontSize = 8.sp,
                        fontFamily = PJakartaSansFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF666666)
                    )
                }
            }
            Text(
                text = "%.2f kg".format(carbonFootprint),
                fontSize = 16.sp,
                fontFamily = PJakartaFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFBA00)
            )
        }
    }
}

@Composable
fun DayCard(day: Int, dayOfWeek: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(60.dp, 90.dp)
            .clickable { onClick() }
            .shadow(
                elevation = if (isSelected) 20.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isSelected) Color(0xFF3F6B1B) else Color.Transparent,
                spotColor = if (isSelected) Color(0xFF3F6B1B) else Color.Transparent
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isSelected) {
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF3F6B1B), Color(0xFF6A795D))
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color.White, Color.White)
                        )
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dayOfWeek.take(3),
                    fontSize = 14.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp,
                    letterSpacing = 0.5.sp,
                    color = if (isSelected) Color.White else Color(0xFF747474)
                )
                Text(
                    text = "$day",
                    fontSize = 24.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp,
                    color = if (isSelected) Color.White else Color(0xFF747474)
                )
            }
        }
    }
}

@Composable
fun MonthCard(monthIndex: Int, monthName: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(60.dp, 90.dp)
            .clickable { onClick() }
            .shadow(
                elevation = if (isSelected) 20.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isSelected) Color(0xFF3F6B1B) else Color.Transparent,
                spotColor = if (isSelected) Color(0xFF3F6B1B) else Color.Transparent
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isSelected) {
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF3F6B1B), Color(0xFF6A795D))
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color.White, Color.White)
                        )
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = monthName,
                    fontSize = 14.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp,
                    letterSpacing = 0.5.sp,
                    color = if (isSelected) Color.White else Color(0xFF747474)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${monthIndex + 1}",
                    fontSize = 24.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp,
                    color = if (isSelected) Color.White else Color(0xFF747474)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun HomeDetailPreview() {
    val navController = rememberNavController()
    HomeDetail(navController = navController, userId = "dummyUserId")
}

@Preview(showBackground = true, name = "Harian Content Preview", widthDp = 360, heightDp = 780)
@Composable
fun HarianContentPreview() {
    HarianContent(userId = "dummyUserId", vehicleHistory = emptyList(), totalCarbonFootprint = 0f)
}

@Preview(showBackground = true, name = "Bulanan Content Preview", widthDp = 360, heightDp = 780)
@Composable
fun BulananContentPreview() {
    BulananContent(userId = "dummyUserId", vehicleHistory = emptyList(), totalCarbonFootprint = 0f)
}