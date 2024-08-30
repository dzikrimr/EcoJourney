package com.example.ecojourney.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily

@Composable
fun Explore(navController: NavHostController) {
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
                        .size(38.dp) // Ukuran tombol
                        .padding(end = 0.dp), // Jarak antara tombol dan teks
                ) {

                }
            }
        }

        // Teks di tengah dan tabs di bawahnya
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 45.dp) // Sesuaikan padding sesuai kebutuhan
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Jelajah",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                )
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

}


@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun ExplorePreview() {
    val navController = rememberNavController() // Create a mock NavController for the preview
    Explore(navController)
}