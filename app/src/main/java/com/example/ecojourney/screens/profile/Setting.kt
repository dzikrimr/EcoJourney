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
import androidx.compose.ui.res.colorResource
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

@Composable
fun SettingsScreen(navController: NavHostController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(id = R.color.white1100))) {
        // Fixed Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.white1100))
                .align(Alignment.TopCenter)
        ) {
            // Header Image
            Image(
                painter = painterResource(id = R.drawable.header_img),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 5.65f)
            )
        }

        // Back Button and Title
        Box(
            modifier = Modifier
                .padding(start = 30.dp, top = 35.dp)
                .align(Alignment.TopStart)
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

            // Title
            Text(
                text = "Pengaturan",
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
                .padding(top = 150.dp) // Adjust based on header height
                .padding(horizontal = 40.dp)
                .padding(bottom = 40.dp), // Bottom padding for navbar clearance
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Settings Title
            Text(
                text = "Pengaturan Aplikasi",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F6B1B),
                    fontFamily = PJakartaFontFamily
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Notification Toggle
            var notificationsEnabled by remember { mutableStateOf(true) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifikasi",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        fontFamily = PJakartaSansFontFamily
                    )
                )
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF3F6B1B),
                        checkedTrackColor = Color(0xFF3F6B1B).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }

            // Location Tracking Toggle
            var locationTrackingEnabled by remember { mutableStateOf(true) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pelacakan Lokasi",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        fontFamily = PJakartaSansFontFamily
                    )
                )
                Switch(
                    checked = locationTrackingEnabled,
                    onCheckedChange = { locationTrackingEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF3F6B1B),
                        checkedTrackColor = Color(0xFF3F6B1B).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }

            // Dark Mode Toggle
            var darkModeEnabled by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mode Gelap",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        fontFamily = PJakartaSansFontFamily
                    )
                )
                Switch(
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF3F6B1B),
                        checkedTrackColor = Color(0xFF3F6B1B).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Preview( showBackground = true, widthDp = 320, heightDp = 640, device = Devices.PIXEL_4)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}
