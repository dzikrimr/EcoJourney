package com.example.ecojourney.menuprofile

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Devices
import androidx.navigation.compose.rememberNavController

@Composable
fun AboutScreen(navController: NavHostController) {
    Surface(color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Mengatur konten di tengah horizontal
        ) {
            // Teks "About" di tengah atas
            Text(
                text = "Tentang Aplikasi Ini.",
                fontFamily = PJakartaFontFamily,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth() // Mengisi lebar untuk memastikan teks ada di tengah
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f)) // Spacer untuk mendorong teks ke tengah layar

            // Teks "Coming Soon" di tengah-tengah layar
            Text(
                text = "Coming Soon...",
                color = Color.Gray,
                fontFamily = PJakartaFontFamily,
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f)) // Spacer di bawah untuk keseimbangan
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640, device = Devices.PIXEL_4)
@Composable
fun AboutScreenPreview() {
    // Mock NavController for preview
    AboutScreen(navController = rememberNavController())
}
