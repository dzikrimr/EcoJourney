package com.example.ecojourney.ui.imageslider

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecojourney.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.delay

@Composable
fun CustomPagerIndicator(
    currentPage: Int,
    pageSize: Int,
    selectedColor: Color = Color(0xFF3F6B1B),
    unselectedColor: Color = Color(0xFFBBBBBB),
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageSize) { index ->
            Spacer(modifier = Modifier.size(2.5.dp))

            Box(
                modifier = Modifier
                    .height(if (index == currentPage) 12.dp else 8.dp) // Tinggi indikator
                    .width(if (index == currentPage) 26.dp else 8.dp) // Lebar indikator
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (index == currentPage) selectedColor else unselectedColor),
            )

            Spacer(modifier = Modifier.size(6.dp))
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider(
    imageResIds: List<Int>,
    modifier: Modifier = Modifier,
    selectedColor: Color = Color(0xFF3F6B1B),
    unselectedColor: Color = Color(0xFFBBBBBB),
) {
    val pagerState = rememberPagerState()

    // Auto-scroll every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // Delay for 5 seconds
            pagerState.animateScrollToPage(
                (pagerState.currentPage + 1) % imageResIds.size,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }

    Column(
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState,
            count = imageResIds.size,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.34f)
        ) { page ->
            Image(
                painter = painterResource(id = imageResIds[page]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CustomPagerIndicator(
            currentPage = pagerState.currentPage,
            pageSize = imageResIds.size,
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageSliderPreview() {
    ImageSlider(
        imageResIds = listOf(
            R.drawable.banner1,
            R.drawable.banner2
        ),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2.34f)
    )
}

@Preview(showBackground = true)
@Composable
fun CustomPagerIndicatorPreview() {

    CustomPagerIndicator(
        currentPage = 0,
        pageSize = 2
    )
}



