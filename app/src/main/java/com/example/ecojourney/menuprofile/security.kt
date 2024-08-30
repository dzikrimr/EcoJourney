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
fun SecurityScreen(navController: NavHostController) {
    Surface(color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Align content horizontally center
        ) {
            // Teks "Security" di tengah atas
            Text(
                text = "Keamanan dan Kata sandi",
                fontFamily = PJakartaFontFamily,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth() // Ensure the text is centered
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f)) // Spacer to push the text to the center of the screen

            // Teks "Coming Soon" di tengah-tengah layar
            Text(
                text = "Coming Soon...",
                fontFamily = PJakartaFontFamily,
                color = Color.Gray,
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f)) // Spacer below to balance the layout
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640, device = Devices.PIXEL_4)
@Composable
fun SecurityScreenPreview() {
    // Mock NavController for preview
    SecurityScreen(navController = rememberNavController())
}
