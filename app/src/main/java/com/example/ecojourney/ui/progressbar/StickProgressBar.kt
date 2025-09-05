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
import androidx.compose.foundation.layout.BoxWithConstraints

@Composable
fun StickProgressBar(
    modifier: Modifier = Modifier.fillMaxWidth(), // Default to fillMaxWidth
    height: Dp = 22.dp,
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
    iconHeight: Dp = 34.dp
) {
    val percent = (currentValue / maxValue * 100).coerceIn(0f, 100f).toInt()

    BoxWithConstraints(
        modifier = modifier
            .wrapContentHeight()
    ) {
        val maxWidth = maxWidth // Access maxWidth from BoxWithConstraints scope

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .height(height)
                .background(backgroundColor)
                .fillMaxWidth() // Fill the parent width
        ) {
            if (isShownText) {
                Text(
                    text = "$currentValue kg",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(y = height / 2 - 16.dp)
                        .padding(end = 6.dp),
                    fontSize = 14.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp,
                    color = Color.White
                )
            }
            // Progress bar foreground with dynamic width based on percent
            Box(
                modifier = Modifier
                    .background(foregroundColor)
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = percent / 100f) // Use fraction for progress
            )
        }

        // Position the icon based on the progress
        Box(
            modifier = Modifier
                .offset(x = maxWidth * percent / 100 - iconHeight / 2, y = -iconHeight / 8)
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
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StickProgressBar(currentValue = 12f)
        }
    }
}