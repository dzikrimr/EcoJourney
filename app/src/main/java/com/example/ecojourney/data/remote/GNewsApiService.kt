package com.example.ecojourney.data.remote

import com.example.ecojourney.data.model.GNewsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GNewsApiService {
    @GET("search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("lang") lang: String = "en",
        @Query("country") country: String = "us",
        @Query("max") max: Int = 10,
        @Query("apikey") apiKey: String
    ): GNewsResponse
}

object GNewsApiClient {
    private const val BASE_URL = "https://gnews.io/api/v4/"

    val apiService: GNewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GNewsApiService::class.java)
    }
}