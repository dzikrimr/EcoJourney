package com.example.ecojourney.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.R
import com.example.ecojourney.screens.profile.Profile
import com.example.ecojourney.ui.theme.PJakartaFontFamily
import com.example.ecojourney.ui.theme.PJakartaSansFontFamily

@Composable
fun MainScreen(selectedItem: MutableState<Int>, navController: NavHostController, context: Context, userId: String) {
    val selectedItem = rememberSaveable { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Konten layar (Home, Explore, Donate, Profile)
        when (selectedItem.value) {
            0 -> Home(context = context, navController = navController)
            1 -> Explore(navController = navController)
            2 -> Donate(navController = navController)
            3 -> Profile(navController = navController, userId = userId)
        }

        // BottomNavigation ditempatkan di atas konten
        BottomNavigation(
            backgroundColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f) // Pastikan BottomNavigation selalu di atas
        ) {
            BottomNavigationItem(
                selected = selectedItem.value == 0,
                onClick = { selectedItem.value = 0 },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (selectedItem.value == 0) R.drawable.home_fill else R.drawable.home_ic
                        ),
                        contentDescription = "Home",
                        tint = if (selectedItem.value == 0) Color(0xFF3F6B1B) else Color(0xFF858586),
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = "Beranda",
                        fontFamily = if (selectedItem.value == 0) PJakartaFontFamily else PJakartaSansFontFamily,
                        fontWeight = if (selectedItem.value == 0) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (selectedItem.value == 0) Color(0xFF3F6B1B) else Color(0xFF858586)
                    )
                }
            )

            BottomNavigationItem(
                selected = selectedItem.value == 1,
                onClick = { selectedItem.value = 1 },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (selectedItem.value == 1) R.drawable.explore_fill else R.drawable.explore_ic
                        ),
                        contentDescription = "Search",
                        tint = if (selectedItem.value == 1) Color(0xFF3F6B1B) else Color(0xFF858586),
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = "Jelajah",
                        fontFamily = if (selectedItem.value == 1) PJakartaFontFamily else PJakartaSansFontFamily,
                        fontWeight = if (selectedItem.value == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (selectedItem.value == 1) Color(0xFF3F6B1B) else Color(0xFF858586)
                    )
                }
            )

            BottomNavigationItem(
                selected = selectedItem.value == 2,
                onClick = { selectedItem.value = 2 },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (selectedItem.value == 2) R.drawable.donate_fill else R.drawable.donate_ic
                        ),
                        contentDescription = "Example",
                        tint = if (selectedItem.value == 2) Color(0xFF3F6B1B) else Color(0xFF858586),
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = "Donasi",
                        fontFamily = if (selectedItem.value == 2) PJakartaFontFamily else PJakartaSansFontFamily,
                        fontWeight = if (selectedItem.value == 2) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (selectedItem.value == 2) Color(0xFF3F6B1B) else Color(0xFF858586)
                    )
                }
            )

            BottomNavigationItem(
                selected = selectedItem.value == 3,
                onClick = { selectedItem.value = 3 },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (selectedItem.value == 3) R.drawable.profil_fill else R.drawable.profil_ic
                        ),
                        contentDescription = "Profile",
                        tint = if (selectedItem.value == 3) Color(0xFF3F6B1B) else Color(0xFF858586),
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = "Profil",
                        fontFamily = if (selectedItem.value == 3) PJakartaFontFamily else PJakartaSansFontFamily,
                        fontWeight = if (selectedItem.value == 3) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (selectedItem.value == 3) Color(0xFF3F6B1B) else Color(0xFF858586)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val selectedItem = remember { mutableStateOf(0) } // Set default value as needed

    MainScreen(
        selectedItem = selectedItem,
        navController = navController,
        context = context,
        userId = "Test"
    )
}

