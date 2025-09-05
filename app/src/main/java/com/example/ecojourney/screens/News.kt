package com.example.ecojourney.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.ecojourney.data.model.GNewsArticle
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsDetail(
    navController: NavHostController,
    encodedArticle: String?
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Deserialize GNewsArticle from encoded JSON
    val article = encodedArticle?.let {
        try {
            val decodedJson = URLDecoder.decode(it, "UTF-8")
            Gson().fromJson(decodedJson, GNewsArticle::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white1100))
    ) {
        // Header image
        Image(
            painter = painterResource(id = R.drawable.header_img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 5.65f)
                .align(Alignment.TopCenter)
        )

        // Back button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, top = 35.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 0.dp),
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
            }
        }

        // Title
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 45.dp)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Berita Terkini",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (article == null) {
                // Display error message if article is null
                Text(
                    text = "Berita ini tidak tersedia",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = PJakartaSansFontFamily,
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                // News Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp)) // Rounded corners
                ) {
                    val hasValidImage = !article.image.isNullOrBlank() &&
                            article.image.startsWith("http") &&
                            !article.image.contains("placeholder")

                    if (hasValidImage) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(article.image)
                                .crossfade(true)
                                .error(R.drawable.placeholder_img)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_img),
                            contentDescription = "Placeholder Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Article Title
                Text(
                    text = article.title,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = PJakartaFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date and Source
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(article.publishedAt),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PJakartaSansFontFamily,
                            color = Color.Gray
                        )
                    )

                    article.source?.let { source ->
                        Text(
                            text = source.name,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PJakartaSansFontFamily,
                                color = Color(0xFF3F6B1B),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Article Description
                if (!article.description.isNullOrBlank()) {
                    Text(
                        text = article.description.cleanEllipsis(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PJakartaSansFontFamily,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Article Content
                if (!article.content.isNullOrBlank()) {
                    Text(
                        text = article.content.cleanEllipsis(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PJakartaSansFontFamily,
                            color = Color.Black,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Justify
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                } else if (!article.description.isNullOrBlank()) {
                    Text(
                        text = article.description.cleanEllipsis(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PJakartaSansFontFamily,
                            color = Color.Black,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Justify
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Read More Button
                if (!article.url.isNullOrBlank()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F6B1B)),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Baca Selengkapnya",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PJakartaSansFontFamily,
                                color = Color.White
                            )
                        )
                    }
                }

                // Bottom spacing
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// Helper function to clean various ellipsis patterns
fun String.cleanEllipsis(): String {
    return this
        .replace(Regex("\\[\\+?\\d+ chars\\]"), "") // Matches [+X chars] or [X chars]
        .trim()
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun NewsDetailPreview() {
    val navController = rememberNavController()
    NewsDetail(navController, encodedArticle = null)
}