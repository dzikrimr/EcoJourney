package com.example.ecojourney.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun AboutScreen(navController: NavHostController) {
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
                text = "Tentang EcoJourney",
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // About Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tentang EcoJourney",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F6B1B),
                        fontFamily = PJakartaFontFamily
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "EcoJourney adalah aplikasi yang dirancang untuk membantu Anda menjalani gaya hidup ramah lingkungan. Kami percaya bahwa setiap langkah kecil menuju keberlanjutan dapat membuat perubahan besar untuk bumi kita. Dengan EcoJourney, Anda dapat:",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        fontFamily = PJakartaSansFontFamily,
                        textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "•Melacak jejak karbon dari perjalanan Anda.\n\n" +
                            "•Mendukung penghijauan melalui donasi penanaman pohon.\n\n" +
                            "•Mendapatkan tips dan tantangan untuk hidup lebih hijau.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = PJakartaSansFontFamily,
                        textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Misi kami adalah menginspirasi dan memberdayakan masyarakat untuk menjaga bumi demi generasi mendatang. Bergabunglah dengan kami untuk menjadikan dunia tempat yang lebih hijau dan sehat!",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = PJakartaSansFontFamily,
                        textAlign = TextAlign.Justify
                    )
                )
            }

            // Footer with Contact Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hubungi Kami",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F6B1B),
                        fontFamily = PJakartaFontFamily
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Email: support@ecojourney.com",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = PJakartaSansFontFamily,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640, device = Devices.PIXEL_4)
@Composable
fun AboutScreenPreview() {
    AboutScreen(navController = rememberNavController())
}