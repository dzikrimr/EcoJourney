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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.progressbar.StickProgressBar
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.Locale
import java.time.format.TextStyle as DateFormatTextStyle

suspend fun fetchCarbonFootprintResult(userId: String): Float {
    val db = FirebaseFirestore.getInstance()
    val documentSnapshot = db.collection("users").document(userId).get().await()
    return documentSnapshot.getDouble("carbonFootprintResult")?.toFloat() ?: 0f
}

@Composable
fun HomeDetail(navController: NavHostController,  userId: String) {

    val selectedTabIndex = remember { mutableStateOf(0) }

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image dengan aspect ratio
            Image(
                painter = painterResource(id = R.drawable.header_img),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth() // Mengisi lebar
                    .aspectRatio(16f / 5.65f) // Mengatur rasio aspek tinggi
                    .align(Alignment.TopCenter) // Mengatur posisi gambar di atas tengah
            )

            // Konten overlay dengan teks dan tombol kembali
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp, top = 40.dp), // Sesuaikan padding sesuai kebutuhan
                contentAlignment = Alignment.TopStart // Mengatur konten di tengah atas
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start // Mengatur Row di sebelah kiri secara horizontal
                ) {
                    // Tombol Bulat
                    IconButton(
                        onClick = {
                            navController.popBackStack() // Navigasi kembali ke layar sebelumnya (Home)
                        },
                        modifier = Modifier
                            .size(48.dp) // Ukuran tombol
                            .padding(end = 0.dp), // Jarak antara tombol dan teks
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp) // Ukuran tombol
                                .background(Color.White, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.back_arrow), // Ganti dengan sumber ikon Anda
                                contentDescription = "Kembali",
                                tint = Color(0xFF3F6B1B) // Mengatur warna ikon
                            )
                        }
                    }
                }
            }

            // Teks di tengah dan tabs di bawahnya
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp) // Sesuaikan padding sesuai kebutuhan
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

                Spacer(modifier = Modifier.height(60.dp)) // Jarak antara teks dan tabs

                ClickableTabs(
                    selectedItem = selectedTabIndex.value,
                    tabsList = listOf("Harian", "Bulanan"), // Replace with actual tab items
                    onClick = { index ->
                        selectedTabIndex.value = index // Update the selected index
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                when (selectedTabIndex.value) {
                    0 -> HarianContent(userId = userId)
                    1 -> BulananContent(userId = userId)

                }
            }
        }
    }
}

@Composable
fun HarianContent(userId: String) {
    val totalDays = 14 // Number of days to display

    val currentDate = LocalDate.now()
    val currentDayOfMonth = currentDate.dayOfMonth
    val currentMonth = currentDate.month.getDisplayName(DateFormatTextStyle.FULL, Locale("id", "ID"))
    val currentYear = currentDate.year

    // Generate a list of dates with the current date at the end
    val startDate = currentDate.minusDays(totalDays.toLong() - 1) // The earliest date to show
    val days = (0 until totalDays).map { startDate.plusDays(it.toLong()) }

    var selectedDay by remember { mutableStateOf(currentDayOfMonth) }
    var displayDate by remember { mutableStateOf("$currentDayOfMonth $currentMonth $currentYear") }
    var carbonFootprintResult by remember { mutableStateOf<Float?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                carbonFootprintResult = fetchCarbonFootprintResult(userId)
            } catch (e: Exception) {
                carbonFootprintResult = 0f
            }

            // Find the index of the current date and scroll to it
            val currentDateIndex = days.indexOfFirst { it.dayOfMonth == currentDayOfMonth }
            if (currentDateIndex != -1) {
                listState.scrollToItem(currentDateIndex)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            modifier = Modifier
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(
            state = listState, // Attach the state to LazyRow
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End // Ensure cards are arranged from right to left
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
                    append("${carbonFootprintResult?.let { "%.2f".format(it) } ?: "..."}/ ")
                }
                withStyle(style = SpanStyle(color = Color(0xFF3F6B1B))) {
                    append("26 kg")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(" hari ini!")
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
            currentValue = carbonFootprintResult ?: 0f,
            iconHeight = 25.dp
        )
    }
}


@Composable
fun BulananContent(userId: String) {
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    )

    val currentDate = LocalDate.now()
    val currentMonthIndex = currentDate.monthValue - 1
    val currentYear = currentDate.year

    var selectedMonth by remember { mutableStateOf(currentMonthIndex) }
    var displayMonth by remember { mutableStateOf("${months[currentMonthIndex]} $currentYear") }
    var carbonFootprintResult by remember { mutableStateOf<Float?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                carbonFootprintResult = fetchCarbonFootprintResult(userId)
            } catch (e: Exception) {
                // Handle the error
                carbonFootprintResult = 0f
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            modifier = Modifier
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(
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
                    append("${carbonFootprintResult?.let { "%.2f".format(it) } ?: "..."}/ ")
                }
                withStyle(style = SpanStyle(color = Color(0xFF3F6B1B))) {
                    append("780 kg")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(" bulan ini!")
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
            currentValue = carbonFootprintResult ?: 0f,
            maxValue = 780f,
            iconHeight = 25.dp
        )
    }
}

// Helper function to cycle through days of the week
fun List<String>.cycle(): List<String> {
    return (this + this).take(31) // Repeat the list to cover all days
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
            containerColor = Color.Transparent // Set to transparent to apply gradient background
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
                    text = dayOfWeek.take(3), // Only take the first 3 characters
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
            containerColor = Color.Transparent // Set to transparent to apply gradient background
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
    // Provide a dummy userId
    HomeDetail(navController = navController, userId = "dummyUserId")
}

@Preview(showBackground = true, name = "Harian Content Preview", widthDp = 360, heightDp = 780)
@Composable
fun HarianContentPreview() {
    // Provide a dummy userId
    HarianContent(userId = "dummyUserId")
}

@Preview(showBackground = true, name = "Bulanan Content Preview", widthDp = 360, heightDp = 780)
@Composable
fun BulananContentPreview() {
    BulananContent(userId = "dummyUserId")
}





