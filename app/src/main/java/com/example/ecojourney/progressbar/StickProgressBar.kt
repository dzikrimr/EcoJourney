package com.example.ecojourney.progressbar

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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily

@Composable
fun StickProgressBar(
    modifier: Modifier = Modifier,
    width: Dp = 300.dp,
    height: Dp = 18.dp,
    backgroundColor: Color = Color(0xFF3F6B1B),
    foregroundColor: Brush = Brush.horizontalGradient(
        listOf(
            Color(0xFFFFDF8B),
            Color(0xFFFFCC45)
        )
    ),
    currentValue: Float = 0f, // Current carbon value in kg
    maxValue: Float = 26f, // Maximum value for the progress bar
    isShownText: Boolean = true,
    icon: ImageVector = ImageVector.vectorResource(id = R.drawable.star_prog),
    iconHeight: Dp = 25.dp
) {
    val percent = (currentValue / maxValue * 100).coerceIn(0f, 100f).toInt()

    Box(
        modifier = modifier
            .width(width)
            .wrapContentHeight()
    ) {
        // Background and foreground progress bar with inner shadow
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .height(height)
                .background(backgroundColor)
                .shadow(1.5.dp, shape = RoundedCornerShape(10.dp)) // Add inner shadow
                .width(width)
        ) {
            if (isShownText) {
                Text(
                    text = "$currentValue kg",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(y = height / 2 - 12.dp)
                        .padding(end = 6.dp),
                    fontSize = 14.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp,
                    color = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .background(foregroundColor)
                    .fillMaxHeight()
                    .width(width * percent / 100)
            )
        }

        // Image that floats above the progress bar
        Box(
            modifier = Modifier
                .offset(x = width * percent / 100 - iconHeight / 2, y = -iconHeight / 6) // Float above the progress bar
                .size(iconHeight)
                .align(Alignment.CenterStart)
        ) {
            Image(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun StickProgressBarPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // Light background to see the progress bar clearly
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StickProgressBar(currentValue = 12f) // Example with 12 kg
        }
    }
}

