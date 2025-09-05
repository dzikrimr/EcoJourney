package com.example.ecojourney.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.ecojourney.data.model.GNewsArticle
import com.example.ecojourney.data.remote.GNewsApiClient
import com.google.gson.Gson
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

// Data classes untuk YouTube API response
data class YouTubeSearchResponse(
    val items: List<YouTubeVideo>
)

data class YouTubeVideo(
    val id: VideoId,
    val snippet: VideoSnippet
)

data class VideoId(
    val videoId: String
)

data class VideoSnippet(
    val title: String,
    val description: String,
    val publishedAt: String,
    val thumbnails: VideoThumbnails,
    val channelTitle: String
)

data class VideoThumbnails(
    val medium: VideoThumbnail
)

data class VideoThumbnail(
    val url: String
)

// Retrofit interface untuk YouTube API
interface YouTubeApiService {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): YouTubeSearchResponse
}

// YouTube API client
object YouTubeApiClient {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    val apiService: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApiService::class.java)
    }
}

@Composable
fun Explore(navController: NavHostController) {
    var youtubeVideos by remember { mutableStateOf<List<YouTubeVideo>>(emptyList()) }
    var newsArticles by remember { mutableStateOf<List<GNewsArticle>>(emptyList()) }
    var isLoadingVideos by remember { mutableStateOf(false) }
    var isLoadingNews by remember { mutableStateOf(false) }
    var newsError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // API Keys
    val youtubeApiKey = "AIzaSyDRp4Jx4xYdZ9gR1Ji4xD6YSOe-yxzvcpQ"
    val gnewsApiKey = "20f80665687d51d982dfbf898a56e57b"

    // Query pencarian
    val youtubeSearchQuery = "lingkungan hidup sustainability climate change"
    val newsSearchQuery = "climate change OR warming"

    // Fetch YouTube videos
    LaunchedEffect(Unit) {
        isLoadingVideos = true
        coroutineScope.launch {
            try {
                val response = YouTubeApiClient.apiService.searchVideos(
                    query = youtubeSearchQuery,
                    apiKey = youtubeApiKey
                )
                youtubeVideos = response.items
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingVideos = false
            }
        }
    }

    // Fetch GNews articles
    LaunchedEffect(Unit) {
        isLoadingNews = true
        coroutineScope.launch {
            try {
                val response = GNewsApiClient.apiService.searchNews(
                    query = newsSearchQuery,
                    apiKey = gnewsApiKey
                )
                newsArticles = response.articles
            } catch (e: Exception) {
                newsError = "Gagal memuat berita: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoadingNews = false
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
                .padding(start = 25.dp, top = 130.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Column {
                // Berita Terkini Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Berita Terkini",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PJakartaFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // LazyRow for news
                if (isLoadingNews) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF3F6B1B))
                    }
                } else if (newsError != null) {
                    Text(
                        text = newsError!!,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PJakartaSansFontFamily
                        )
                    )
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(newsArticles) { article ->
                            NewsCard(article = article, navController = navController) // Fixed: Pass navController
                        }
                    }
                }

                // Edukasi Section Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, end = 25.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Edukasi",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PJakartaFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Lihat Semua",
                        modifier = Modifier.clickable {
                            navController.navigate("education")
                        },
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PJakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3F6B1B),
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }

                // YouTube Videos List
                if (isLoadingVideos) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF3F6B1B))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(youtubeVideos) { video ->
                            VideoCard(
                                video = video,
                                navController = navController
                            )
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
                text = "Jelajah",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun NewsCard(article: GNewsArticle, navController: NavHostController) {
    Card(
        modifier = Modifier
            .height(160.dp)
            .width(270.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable {
                // Serialize GNewsArticle to JSON and encode for navigation
                val gson = Gson()
                val articleJson = gson.toJson(article)
                val encodedArticle = URLEncoder.encode(articleJson, "UTF-8")
                navController.navigate("news_detail/$encodedArticle")
            },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                val hasValidImage = !article.image.isNullOrBlank() &&
                        article.image.startsWith("http") &&
                        !article.image.contains("placeholder")

                if (hasValidImage) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(article.image)
                            .crossfade(true)
                            .error(R.drawable.placeholder_img)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onError = {
                            println("Failed to load image: ${article.image}")
                        }
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

            // Text content section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = article.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = PJakartaSansFontFamily
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(article.publishedAt),
                        style = TextStyle(
                            fontSize = 8.sp,
                            color = Color.Gray,
                            fontFamily = PJakartaSansFontFamily
                        )
                    )

                    Button(
                        onClick = {
                            // Serialize and navigate on button click as well
                            val gson = Gson()
                            val articleJson = gson.toJson(article)
                            val encodedArticle = URLEncoder.encode(articleJson, "UTF-8")
                            navController.navigate("news_detail/$encodedArticle")
                        },
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F6B1B)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(22.dp)
                    ) {
                        Text(
                            text = "Baca",
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontFamily = PJakartaSansFontFamily,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCard(
    video: YouTubeVideo,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 25.dp)
            .clickable {
                val encodedVideoId = video.id.videoId.encodeURLPath()
                val encodedVideoTitle = video.snippet.title.encodeURLPath()
                val encodedVideoDescription = video.snippet.description.encodeURLPath()
                val encodedChannelTitle = video.snippet.channelTitle.encodeURLPath()
                navController.navigate(
                    "video_detail/$encodedVideoId/$encodedVideoTitle?videoDescription=$encodedVideoDescription&channelTitle=$encodedChannelTitle"
                )
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.snippet.thumbnails.medium.url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = video.snippet.title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PJakartaFontFamily
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.snippet.channelTitle,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = PJakartaSansFontFamily,
                        color = Color.Gray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = formatDate(video.snippet.publishedAt),
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = PJakartaFontFamily,
                        color = Color.Black
                    )
                )
            }
        }
    }
}

fun String.encodeURLPath(): String = URLEncoder.encode(this, "UTF-8")

fun formatDate(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(isoDate)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        isoDate.substring(0, 10)
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun ExplorePreview() {
    val navController = rememberNavController()
    Explore(navController)
}