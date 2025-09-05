package com.example.ecojourney.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily

@Composable
fun DonateDetail(navController: NavHostController) {
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
                        .background(Color.White, shape = androidx.compose.foundation.shape.CircleShape),
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
                text = "Donasi",
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

        // Centered Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp) // Adjust based on header height
                .padding(horizontal = 40.dp)
                .padding(bottom = 40.dp), // Bottom padding for navbar clearance
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Donation Temporarily Unavailable",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontFamily = PJakartaFontFamily,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun DonateDetailPreview() {
    val navController = rememberNavController()
    DonateDetail(navController)
}