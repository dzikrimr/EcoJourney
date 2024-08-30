package com.example.ecojourney.progressbar

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ecojourney.R

@Composable
fun AnimatedCircularProgressBar(
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    radius: Dp = 70.dp,
    strokeWidth: Dp = 20.dp,
    backgroundColor: Color = Color(0xFF3F6B1B),
    progressColor: Color = Color(0xFFFFBA00),
    imageRes: Int = R.drawable.ecoonly // Default colored image
) {
    // Animation specs
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
    val rotation by animateFloatAsState(
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(radius * 2f)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation
            )
    ) {
        Canvas(
            modifier = Modifier
                .size(radius * 2f)
        ) {
            // Draw background circle (unfilled part)
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth.toPx())
            )

            // Draw progress arc (filled part)
            drawArc(
                color = progressColor,
                startAngle = -90f, // Start from the top
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(strokeWidth.toPx())
            )
        }

        // Add the image in the center
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(radius * 1.2f) // Ensure the image size is proportional to the radius
                .align(Alignment.Center)
        )
    }
}

@Composable
fun AnimatedCircularProgressBarBase(
    modifier: Modifier = Modifier,
    radius: Dp = 70.dp,
    strokeWidth: Dp = 20.dp,
    imageRes: Int = R.drawable.ecoonly2 // Default grey image
) {
    AnimatedCircularProgressBar(
        progress = 1.0f, // Full progress
        modifier = modifier,
        radius = radius,
        strokeWidth = strokeWidth,
        backgroundColor = Color.Gray,
        progressColor = Color.Gray,
        imageRes = imageRes
    )
}

@Composable
fun SmallCircle(
    modifier: Modifier = Modifier,
    diameter: Dp = 24.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(diameter)
            .background(color = Color(0xFF3F6B1B), shape = CircleShape)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.arrow),
            contentDescription = null,
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.Center)
                .clickable { onClick() }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AnimatedCircularProgressBarPreview() {
    Surface(
        modifier = Modifier.size(160.dp), // Adjust size to fit the circular progress bar
        color = Color.White
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp) // Match the size of the Surface
        ) {
            // Animated Progress Bar with color
            AnimatedCircularProgressBar(
                progress = 0.15f, // 15% progress
                radius = 70.dp,
                strokeWidth = 20.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedCircularProgressBarBasePreview() {
    Surface(
        modifier = Modifier.size(160.dp), // Adjust size to fit the circular progress bar
        color = Color.White
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp) // Match the size of the Surface
        ) {
            // Animated Full grey progress bar
            AnimatedCircularProgressBarBase(
                radius = 70.dp,
                strokeWidth = 20.dp
            )
        }
    }
}

@Preview
@Composable
fun SmallCirclePreview() {
    SmallCircle(onClick = {})
    
}
