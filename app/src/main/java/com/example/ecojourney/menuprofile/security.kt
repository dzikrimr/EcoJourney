package com.example.ecojourney.menuprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ecojourney.ui.theme.PJakartaFontFamily

@Composable
fun SecurityScreen(navController: NavHostController) {
    // Your Settings screen UI goes here
    // You can use Material3 components or your custom UI
    Surface(color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Security", fontFamily = PJakartaFontFamily)
            // Add more UI elements for Settings
        }
    }
}