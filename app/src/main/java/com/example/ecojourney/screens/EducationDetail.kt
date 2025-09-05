package com.example.ecojourney.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.ui.theme.EcoJourneyTheme
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.net.URLDecoder

// Data classes untuk Gemini API
data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    val content: GeminiContent
)

// Gemini API interface - PERBAIKAN
interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyAOtQtbJQnxmOE4NYyNk5x272Gil4q-QGg")
    suspend fun generateContent(
        @Body request: GeminiRequest
    ): GeminiResponse
}

// Gemini API client - PERBAIKAN
object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    fun create(): GeminiApiService { // HAPUS parameter apiKey
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
}

@Composable
fun EducationDetailScreen(
    navController: NavHostController,
    videoId: String,
    videoTitle: String,
    videoDescription: String = "",
    channelTitle: String = ""
) {
    // Decode URL-encoded parameters
    val decodedVideoTitle = URLDecoder.decode(videoTitle, "UTF-8")
    val decodedVideoDescription = URLDecoder.decode(videoDescription, "UTF-8")
    val decodedChannelTitle = URLDecoder.decode(channelTitle, "UTF-8")

    var videoSummary by remember { mutableStateOf<String?>(null) }
    var isLoadingSummary by remember { mutableStateOf(false) }
    var summaryError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(videoId, decodedVideoDescription) {
        if (decodedVideoDescription.isNotBlank()) {
            isLoadingSummary = true
            summaryError = null

            coroutineScope.launch {
                try {
                    val geminiService = GeminiApiClient.create() // HAPUS parameter apiKey

                    val prompt = """
                    Buat ringkasan informatif dan terstruktur berdasarkan informasi berikut:
                        
                        **Judul**: $decodedVideoTitle
                        **Deskripsi**: $decodedVideoDescription
                        
                        **Format Ringkasan**:
                        📋  Ringkasan Utama  
                        
                        Tulis 2-3 kalimat yang merangkum inti konten secara singkat dan jelas, tanpa menyebutkan kata "video" atau frasa seperti "berdasarkan deskripsi".

                        🎯  Poin-Poin Penting 
                         
                        • Poin penting 1  
                        • Poin penting 2  
                        • Poin penting 3  

                        💡  Insight Menarik  
                        
                        Jelaskan satu pembelajaran atau wawasan utama yang dapat diambil, ditulis dengan gaya yang inspiratif dan langsung.

                        🌱  Relevansi dengan Lingkungan  
                        
                        Kaitkan konten dengan isu lingkungan dan keberlanjutan jika relevan, menggunakan bahasa yang alami dan kontekstual.

                        **Instruksi Tambahan**:  
                        - Gunakan bahasa Indonesia yang formal, jelas, dan menarik, seperti gaya artikel informatif.  
                        - Jangan gunakan frasa seperti "berikut ringkasan", "berdasarkan deskripsi", "berikut adalah", atau pengantar lainnya; langsung tulis konten ringkasan.  
                        - Hindari kata "video" dalam ringkasan; fokus pada isi konten.  
                        - Pastikan teks terasa alami, ringkas, dan tanpa kalimat pembuka yang redundan.
                        - Jangan gunakan tanda ** untuk subjudul; tulis subjudul sebagai teks polos (contoh: 📋 Ringkasan Utama, bukan 📋 **Ringkasan Utama**).
                        - Berikan jarak 1 baris setelah subjudul.
                    """.trimIndent()

                    val request = GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(GeminiPart(prompt))
                            )
                        )
                    )

                    val response = geminiService.generateContent(request)
                    videoSummary = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: "Tidak dapat menghasilkan ringkasan untuk video ini."

                } catch (e: Exception) {
                    summaryError = "Gagal memuat ringkasan: ${e.message}"
                    e.printStackTrace()
                } finally {
                    isLoadingSummary = false
                }
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
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp)
                .verticalScroll(scrollState)
        ) {
            // YouTube Player
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(220.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                YouTubePlayerComposable(
                    videoId = videoId,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Combined Video Info and AI Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Video Title
                    Text(
                        text = decodedVideoTitle,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PJakartaFontFamily,
                            color = Color(0xFF3F6B1B)
                        )
                    )

                    if (decodedChannelTitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "oleh $decodedChannelTitle",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = PJakartaSansFontFamily,
                                color = Color.Gray
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // AI Summary Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ringkasan AI",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PJakartaFontFamily,
                                color = Color(0xFF3F6B1B)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Powered by Gemini",
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = PJakartaSansFontFamily,
                                color = Color.Gray
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        isLoadingSummary -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF3F6B1B),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Membuat ringkasan...",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontFamily = PJakartaSansFontFamily,
                                            color = Color.Gray
                                        )
                                    )
                                }
                            }
                        }

                        summaryError != null -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                            ) {
                                Text(
                                    text = summaryError!!,
                                    modifier = Modifier.padding(12.dp),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = PJakartaSansFontFamily,
                                        color = Color(0xFFD32F2F),
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }

                        videoSummary != null -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                            ) {
                                Text(
                                    text = videoSummary!!,
                                    modifier = Modifier.padding(16.dp),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = PJakartaSansFontFamily,
                                        color = Color.Black,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }

                        else -> {
                            Text(
                                text = "Tidak ada deskripsi video untuk diringkas.",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = PJakartaSansFontFamily,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Bottom spacing
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun YouTubePlayerComposable(
    videoId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                })
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun VideoDetailScreenPreview() {
    val navController = rememberNavController()
    EcoJourneyTheme {
        EducationDetailScreen(
            navController = navController,
            videoId = "dQw4w9WgXcQ",
            videoTitle = "Sample Educational Video About Environment",
            videoDescription = "This is a sample video description about environmental conservation and sustainability practices that we can implement in our daily lives.",
            channelTitle = "Eco Education Channel"
        )
    }
}