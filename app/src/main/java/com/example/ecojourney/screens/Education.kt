package com.example.ecojourney.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.EcoJourneyTheme
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Education(navController: NavHostController) {
    var youtubeVideos by remember { mutableStateOf<List<YouTubeVideo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // API Key - GANTI DENGAN API KEY ANDA SENDIRI
    val apiKey = "AIzaSyDRp4Jx4xYdZ9gR1Ji4xD6YSOe-yxzvcpQ"

    // Query pencarian yang dapat disesuaikan
    val searchQuery = "lingkungan hidup sustainability climate change environmental education"

    LaunchedEffect(Unit) {
        isLoading = true
        coroutineScope.launch {
            try {
                val response = YouTubeApiClient.apiService.searchVideos(
                    query = searchQuery,
                    apiKey = apiKey,
                    maxResults = 20 // Tampilkan lebih banyak video
                )
                youtubeVideos = response.items
            } catch (e: Exception) {
                // Handle error - bisa tambahkan log atau toast
                e.printStackTrace()
            } finally {
                isLoading = false
            }
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

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 25.dp, top = 118.dp, end = 25.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Column {
                // YouTube Videos List
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF3F6B1B),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Memuat video edukasi...",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = PJakartaSansFontFamily,
                                    color = Color.Gray
                                )
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(youtubeVideos) { video ->
                            LargeVideoCard(
                                video = video,
                                navController = navController
                            )
                        }

                        // Bottom spacing untuk scroll
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
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
                text = "Edukasi",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                )
            )
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun LargeVideoCard(
    video: YouTubeVideo,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Encode parameters to handle special characters
                val encodedVideoId = video.id.videoId.encodeURLPath()
                val encodedVideoTitle = video.snippet.title.encodeURLPath()
                val encodedVideoDescription = video.snippet.description.encodeURLPath()
                val encodedChannelTitle = video.snippet.channelTitle.encodeURLPath()

                // Navigate to VideoDetailScreen
                navController.navigate(
                    "video_detail/$encodedVideoId/$encodedVideoTitle?videoDescription=$encodedVideoDescription&channelTitle=$encodedChannelTitle"
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // YouTube thumbnail - ukuran lebih besar
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.snippet.thumbnails.medium.url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            // Video information
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Video title
                Text(
                    text = video.snippet.title.removeHashtags(),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PJakartaFontFamily,
                        color = Color(0xFF3F6B1B)
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Channel and date info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Channel name
                    Text(
                        text = video.snippet.channelTitle,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = PJakartaSansFontFamily,
                            color = Color(0xFF3F6B1B)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Published date
                    Text(
                        text = formatDate(video.snippet.publishedAt),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = PJakartaFontFamily,
                            color = Color.Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

fun String.removeHashtags(): String {
    return this.replace(Regex("#\\w+"), "").trim().replace(Regex("\\s+"), " ")
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun EducationPreview() {
    val navController = rememberNavController()
    EcoJourneyTheme {
        Education(navController)
    }
}