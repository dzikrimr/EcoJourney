package com.example.ecojourney.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily

@Composable
fun DontHaveAccountRow(
    onSignupTap: () -> Unit = {},
) {
    Row(
        modifier = Modifier.padding(top = 12.dp, bottom = 52.dp)
    ) {
        Text(
            "Belum Punya Akun? ",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = PJakartaSansFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        )

        Text(
            "Daftar disini",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = PJakartaSansFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3F6B1B)
            ),
            modifier = Modifier.clickable {
                onSignupTap()
            }
        )
    }
}