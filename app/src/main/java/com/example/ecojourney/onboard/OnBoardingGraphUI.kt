package com.example.ecojourney.onboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.example.ecojourney.R

@Composable
fun ParabolicBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF3F6B1B),
    title: String,
    description: String,
    imageRes: Int // Add this parameter to pass the image resource
) {
    Card(
        modifier = modifier
            .width(350.dp)
            .height(540.dp)
            .padding(16.dp), // Menambahkan padding untuk bayangan yang lebih baik
        shape = RoundedCornerShape(20.dp),
        elevation = 8.dp // Mengatur elevasi untuk bayangan
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
                    moveTo(0f, height * 0.60f) // Parabola dimulai dekat ke bawah
                    cubicTo(
                        width * 0.25f, height * 0.55f, // Titik kontrol pertama
                        width * 0.75f, height * 0.55f, // Titik kontrol kedua
                        width, height * 0.60f // Parabola berakhir di dekat bagian bawah
                    )
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = path,
                    color = Color.White, // Warna latar belakang parabola
                    style = Fill
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp), // Adjust padding to place text inside parabola
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageRes), // Display the image
                    contentDescription = null,
                    modifier = Modifier
                        .size(280.dp) // Adjust the image size as needed
                        .padding(bottom = 30.dp) // Add padding if necessary
                )

                Text(
                    text = title,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = description,
                    color = Color(0xFF787878),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = PJakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }
}

@Composable
fun OnboardingGraphUI(onboardingModel: OnboardingModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.size(40.dp))

            ParabolicBox(
                title = onboardingModel.title,
                description = onboardingModel.description,
                imageRes = onboardingModel.imageRes // Pass the image resource here
            )
        }

        // Wrap the Image in a Box and apply the alignment
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 65.dp)
        ) {
            Image(
                painter = painterResource(id = onboardingModel.backgroundImage),
                contentDescription = null,
                modifier = Modifier
                    .width(130.dp)
                    .height(40.dp)
                    .align(Alignment.TopStart) // Apply alignment within the Box
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
