package com.example.ecojourney.ui.onboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily

@Composable
fun ParabolicBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF3F6B1B),
    title: String,
    description: String,
    imageRes: Int
) {
    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Calculate responsive values
    val isSmallScreen = screenHeight < 700.dp
    val isTablet = screenWidth > 600.dp
    val isLandscape = screenWidth > screenHeight

    // Responsive dimensions
    val cardWidth = when {
        isTablet && !isLandscape -> screenWidth * 0.7f
        isTablet && isLandscape -> screenWidth * 0.5f
        isSmallScreen -> screenWidth * 0.9f
        else -> minOf(380.dp, screenWidth * 0.90f)
    }

    val cardHeight = when {
        isSmallScreen -> screenHeight * 0.60f
        isTablet && isLandscape -> screenHeight * 0.75f
        isTablet -> screenHeight * 0.55f
        else -> minOf(530.dp, screenHeight * 0.70f)
    }

    val imageSize = when {
        isTablet -> 320.dp
        isSmallScreen -> 200.dp
        else -> 260.dp
    }

    val topPadding = when {
        isSmallScreen -> 40.dp
        isTablet -> 80.dp
        else -> 60.dp
    }

    val horizontalPadding = when {
        isTablet -> 60.dp
        isSmallScreen -> 24.dp
        else -> 40.dp
    }

    val titleFontSize = when {
        isTablet -> 20.sp
        isSmallScreen -> 14.sp
        else -> 16.sp
    }

    val descriptionFontSize = when {
        isTablet -> 16.sp
        isSmallScreen -> 12.sp
        else -> 14.sp
    }

    Card(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .padding(if (isSmallScreen) 12.dp else 16.dp),
        shape = RoundedCornerShape(if (isTablet) 24.dp else 20.dp),
        elevation = if (isTablet) 12.dp else 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                val path = Path().apply {
                    moveTo(0f, height * 0.60f)
                    cubicTo(
                        width * 0.25f, height * 0.55f,
                        width * 0.75f, height * 0.55f,
                        width, height * 0.60f
                    )
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = path,
                    color = Color.White,
                    style = Fill
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .padding(horizontal = if (isTablet) 20.dp else 0.dp)
                )

                Spacer(modifier = Modifier.height(if (isSmallScreen) 20.dp else 10.dp))

                Text(
                    text = title,
                    fontSize = titleFontSize,
                    textAlign = TextAlign.Center,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(if (isSmallScreen) 12.dp else 16.dp))

                Text(
                    text = description,
                    color = Color(0xFF787878),
                    fontSize = descriptionFontSize,
                    textAlign = TextAlign.Center,
                    fontFamily = PJakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    lineHeight = if (isTablet) 22.sp else if (isSmallScreen) 18.sp else 20.sp,
                    modifier = Modifier.padding(horizontal = horizontalPadding)
                )

                Spacer(modifier = Modifier.height(if (isSmallScreen) 24.dp else 32.dp))
            }
        }
    }
}

@Composable
fun OnboardingGraphUI(onboardingModel: OnboardingModel) {
    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Calculate responsive values
    val isSmallScreen = screenHeight < 700.dp
    val isTablet = screenWidth > 600.dp
    val isLandscape = screenWidth > screenHeight

    val topSpacing = when {
        isSmallScreen -> 12.dp
        isTablet -> 32.dp
        else -> 20.dp
    }

    val logoTopPadding = when {
        isSmallScreen -> 24.dp
        isTablet -> 64.dp
        else -> 50.dp
    }

    val logoWidth = when {
        isTablet -> 160.dp
        isSmallScreen -> 110.dp
        else -> 100.dp
    }

    val logoHeight = when {
        isTablet -> 50.dp
        isSmallScreen -> 32.dp
        else -> 30.dp
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(topSpacing))

            ParabolicBox(
                title = onboardingModel.title,
                description = onboardingModel.description,
                imageRes = onboardingModel.imageRes
            )
        }

        // Logo overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = logoTopPadding,
                    start = if (isTablet) 32.dp else 16.dp
                )
        ) {
            Image(
                painter = painterResource(id = onboardingModel.backgroundImage),
                contentDescription = null,
                modifier = Modifier
                    .width(logoWidth)
                    .height(logoHeight)
                    .align(Alignment.TopStart)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun OnboardingGraphUIPreview1() {
    OnboardingGraphUI(OnboardingModel.FirstPage)
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun OnboardingGraphUIPreview2() {
    OnboardingGraphUI(OnboardingModel.SecondPage)
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun OnboardingGraphUIPreview3() {
    OnboardingGraphUI(OnboardingModel.ThirdPages)
}

@Preview(showBackground = true, widthDp = 600, heightDp = 800)
@Composable
fun OnboardingGraphUITabletPreview1() {
    OnboardingGraphUI(OnboardingModel.FirstPage)
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun OnboardingGraphUISmallPreview1() {
    OnboardingGraphUI(OnboardingModel.FirstPage)
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
fun OnboardingGraphUILandscapePreview1() {
    OnboardingGraphUI(OnboardingModel.FirstPage)
}