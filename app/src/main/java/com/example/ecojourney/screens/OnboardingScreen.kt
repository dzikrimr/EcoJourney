package com.example.ecojourney.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.ui.CButton
import com.example.ecojourney.ui.CButtonOutlined
import com.example.ecojourney.ui.onboard.IndicatorUI
import com.example.ecojourney.ui.onboard.OnboardingGraphUI
import com.example.ecojourney.ui.onboard.OnboardingModel
import com.example.ecojourney.ui.theme.EcoJourneyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavHostController,
    onFinished: () -> Unit
) {
    val pages = listOf(
        OnboardingModel.FirstPage, OnboardingModel.SecondPage, OnboardingModel.ThirdPages
    )

    val pagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }

    Surface (
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ){
    }

    Scaffold(bottomBar = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(20.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
            }
            Spacer(modifier = Modifier.height(30.dp))
            CButton(
                text = "Daftar",
                onClick = {
                    navController.navigate("signup")
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            CButtonOutlined(
                text = "Masuk",
                onClick = {
                    navController.navigate("login")
                }
            )
        }
    }, content = {
        Column(Modifier.padding(it)) {
            HorizontalPager(state = pagerState) { index ->
                OnboardingGraphUI(onboardingModel = pages[index])
            }
        }
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun OnboardingScreenPreview() {
    EcoJourneyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Create a NavController for preview
            val navController = rememberNavController()

            OnboardingScreen(
                navController = navController,
                onFinished = {  }
            )
        }
    }
}
