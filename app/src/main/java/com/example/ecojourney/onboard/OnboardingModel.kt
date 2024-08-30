package com.example.ecojourney.onboard

import androidx.annotation.DrawableRes
import com.example.ecojourney.R

sealed class OnboardingModel(
    @DrawableRes val image: Int,
    @DrawableRes val backgroundImage: Int,
    val imageRes: Int,
    val title: String,
    val description: String,
) {

    data object FirstPage : OnboardingModel(
        image = R.drawable.ecowhite,
        backgroundImage = R.drawable.ecowhite,
        imageRes = R.drawable.mockup1,
        title = "Selamat datang di EcoJourney!",
        description = "EcoJourney adalah Aplikasi untuk Mengurangi Jejak Karbon dan Melawan Perubahan Iklim"
    )

    data object SecondPage : OnboardingModel(
        image = R.drawable.ecowhite,
        imageRes = R.drawable.mockup2,
        title = "Pantau CO2 setiap harinya!",
        backgroundImage = R.drawable.ecowhite,
        description = "Ketahui CO2 yang kamu hasilnya dari kendaraanmu setiap harinya untuk mencegah penghasilan Karbon yang berlebih!"
    )

    data object ThirdPages : OnboardingModel(
        image = R.drawable.ecowhite,
        imageRes = R.drawable.mockup3,
        title = "Donasi untuk Dunia yang lebih baik!",
        backgroundImage = R.drawable.ecowhite,
        description = "Donasi penanaman tumbuhan sebagai pencegahan Karbon yang berlebih untuk Dunia yang lebih baik!"
    )


}