package com.example.ecojourney.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.ui.progressbar.StickProgressBarWhite
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily

@Composable
fun Donate(navController: NavHostController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(id = R.color.white1100))) {
        // Fixed Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.white1100))
                .align(Alignment.TopCenter)
        ) {
            // Header Image
            Image(
                painter = painterResource(id = R.drawable.header_img),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 5.65f)
            )
        }

        // Back Button and Title
        Box(
            modifier = Modifier
                .padding(start = 30.dp, top = 35.dp)
                .align(Alignment.TopStart)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(48.dp)
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

            // Title
            Text(
                text = "Donasi",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = PJakartaFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .padding(end = 30.dp),
                textAlign = TextAlign.Center
            )
        }

        // Scrollable Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp) // Adjust based on header height
                .padding(horizontal = 40.dp)
                .padding(bottom = 40.dp), // Added bottom padding for navbar clearance
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                var isExpanded by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .border(
                            width = 2.dp,
                            color = colorResource(id = R.color.green1100),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    shape = RoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Image and Gradient Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.donate_bg),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                colorResource(id = R.color.green1100).copy(alpha = 0.6f),
                                                Color(0xFF3F6B1B).copy(alpha = 1f)
                                            ),
                                            startY = 0f,
                                            endY = 1500f
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 15.dp)
                            ) {
                                Text(
                                    text = "Bersama,",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontFamily = PJakartaFontFamily
                                    ),
                                    modifier = Modifier.padding(bottom = 0.dp)
                                )
                                Text(
                                    text = "Kita Hijaukan Dunia!",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontFamily = PJakartaSansFontFamily
                                    ),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                                Text(
                                    text = "#untukdunialebihbaik",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = colorResource(id = R.color.yellow1100),
                                        fontFamily = PJakartaSansFontFamily,
                                        fontStyle = FontStyle.Italic
                                    ),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                                Text(
                                    text = "Apakah Anda ingin berkontribusi untuk masa depan yang lebih hijau? Dengan berdonasi, Anda tidak hanya menanam pohon, tetapi juga harapan. Setiap donasi yang Anda berikan akan disalurkan untuk menanam pohon dan menciptakan hutan yang lebih lestari. Mari bersama-sama menjaga bumi ini untuk generasi mendatang!",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontFamily = PJakartaSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontStyle = FontStyle.Italic
                                    ),
                                    modifier = Modifier.padding(bottom = 15.dp),
                                    textAlign = TextAlign.Justify
                                )
                                StickProgressBarWhite(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.ecowhite),
                                contentDescription = "Eco White",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .size(20.dp)
                            )
                        }
                        // Expandable Content
                        if (isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Tanam Pohon, Tanam Harapan",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.black),
                                        fontFamily = PJakartaFontFamily
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Deskripsi Donasi:",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.black),
                                        fontFamily = PJakartaFontFamily
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Dengan berdonasi, Anda membantu menanam pohon yang akan ditanam di berbagai daerah oleh pihak berwenang dan organisasi lingkungan. Pohon-pohon ini tidak hanya memperindah alam sekitar, tetapi juga memainkan peran penting dalam menjaga keseimbangan ekosistem, menyerap karbon dioksida, dan menyediakan oksigen bagi kehidupan. Setiap pohon yang ditanam adalah langkah kecil menuju perubahan besar.",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontFamily = PJakartaSansFontFamily,
                                        textAlign = TextAlign.Justify
                                    ),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Text(
                                    text = "Apa yang akan Anda Dapatkan dengan Berdonasi?",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.black),
                                        fontFamily = PJakartaFontFamily
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Kontribusi Nyata untuk Alam: Setiap donasi Anda akan digunakan untuk membeli bibit pohon yang akan ditanam di daerah-daerah yang membutuhkan penghijauan.\n" +
                                            "Pembaruan Berkala: Anda akan mendapatkan pembaruan mengenai lokasi dan perkembangan pohon yang ditanam berkat donasi Anda.\n" +
                                            "Peluang untuk Terlibat Lebih Lanjut: Bergabunglah dengan kegiatan penanaman pohon dan lihat secara langsung dampak positif yang Anda buat!",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontFamily = PJakartaSansFontFamily,
                                        textAlign = TextAlign.Justify
                                    ),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Text(
                                    text = "Mengapa Ini Penting?",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.black),
                                        fontFamily = PJakartaFontFamily
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Pohon adalah paru-paru bumi. Mereka membantu mengurangi polusi udara, memberikan rumah bagi satwa liar, dan menjaga keseimbangan ekosistem kita. Dengan meningkatnya ancaman perubahan iklim dan deforestasi, langkah kecil seperti berdonasi untuk penanaman pohon dapat membawa perubahan besar. Bersama-sama, kita dapat membangun masa depan yang lebih hijau dan sehat.",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontFamily = PJakartaSansFontFamily,
                                        textAlign = TextAlign.Justify
                                    ),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Text(
                                    text = "Mari Mulai Perubahan Sekarang!",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.black),
                                        fontFamily = PJakartaFontFamily
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Jadilah bagian dari solusi. Donasi sekarang dan bantu kami menanam pohon untuk dunia yang lebih baik!",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontFamily = PJakartaSansFontFamily,
                                        textAlign = TextAlign.Justify
                                    )
                                )
                            }
                        }
                        // Toggle Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(Color.White)
                                .padding(horizontal = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpanded) "Sembunyikan" else "Lihat Selengkapnya",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 20.dp),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = colorResource(id = R.color.green1100),
                                    fontFamily = PJakartaSansFontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                            IconButton(onClick = { isExpanded = !isExpanded }) {
                                Image(
                                    painter = painterResource(id = if (isExpanded) R.drawable.uparrow else R.drawable.droparrow),
                                    contentDescription = "Toggle"
                                )
                            }
                        }
                    }
                }
                // Donate Button
                Button(
                    onClick = { navController.navigate("donate_detail") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F6B1B),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Donasi Sekarang",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PJakartaFontFamily
                        )
                    )
                }
            }
            // Bottom spacing for scroll
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Increased bottom spacing for navbar clearance
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun DonatePreview() {
    val navController = rememberNavController()
    Donate(navController)
}