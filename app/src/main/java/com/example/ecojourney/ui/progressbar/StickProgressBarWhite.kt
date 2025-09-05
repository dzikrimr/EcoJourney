package com.example.ecojourney.ui.progressbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily

@Composable
fun StickProgressBarWhite(
    modifier: Modifier = Modifier,
    width: Dp = 300.dp,
    height: Dp = 18.dp,
    backgroundColor: Color = Color.White,
    foregroundColor: Brush = Brush.horizontalGradient(
        listOf(
            Color(0xFFFFDF8B),
            Color(0xFFFFCC45)
        )
    ),
    currentValue: Float = 0f,
    maxValue: Float = 26f,
    isShownText: Boolean = true,
    icon: ImageVector = ImageVector.vectorResource(id = R.drawable.star_prog),
    iconHeight: Dp = 38.dp
) {
    val percent = (currentValue / maxValue * 100).coerceIn(0f, 100f).toInt()

    Column(
        modifier = modifier
            .width(width)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start // Atur alignment ke kiri
    ) {
        // Progress Bar + Icon
        Box(
            modifier = Modifier
                .height(height + iconHeight / 12) // Tambahkan space untuk icon
        ) {
            // Background Progress
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .height(height)
                    .background(backgroundColor)
                    .shadow(1.5.dp, shape = RoundedCornerShape(10.dp))
                    .width(width)
            ) {
                // Foreground Progress
                Box(
                    modifier = Modifier
                        .background(foregroundColor)
                        .fillMaxHeight()
                        .width(width * percent / 100)
                )
            }

            // Icon
            Box(
                modifier = Modifier
                    .offset(
                        x = width * percent / 100 - iconHeight / 2,
                        y = height - iconHeight / 2
                    )
                    .size(iconHeight)
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Text di bawah progress bar
        if (isShownText) {
            Text(
                text = "Rp xx.xxx.xxx",
                modifier = Modifier
                    .padding(start = 4.dp), // Jarak dari sisi kiri
                fontSize = 14.sp,
                fontFamily = PJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorResource(id = R.color.yellow1100)
            )
        }
    }
}

@Preview
@Composable
fun StickProgressBarWhitePreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StickProgressBarWhite(currentValue = 12f) // Ganti menjadi StickProgressBarWhite
        }
    }
}


