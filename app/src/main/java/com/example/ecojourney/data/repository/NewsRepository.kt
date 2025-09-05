package com.example.ecojourney.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class NewsRepository {

    private val client = OkHttpClient()

    suspend fun fetchNews(): List<NewsItem> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://climate-change-news-live13.p.rapidapi.com/news")
                .get()
                .addHeader("x-rapidapi-key", "YOUR_API_KEY")
                .addHeader("x-rapidapi-host", "climate-change-news-live13.p.rapidapi.com")
                .build()

            val response = client.newCall(request).execute()
            val jsonString = response.body?.string() ?: return@withContext emptyList()

            parseNews(jsonString)
        }
    }

    private fun parseNews(jsonString: String): List<NewsItem> {
        val jsonArray = JSONArray(jsonString)
        val newsList = mutableListOf<NewsItem>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val title = jsonObject.getString("title")
            val imageUrl = jsonObject.getString("image") // Pastikan API menyediakan ini
            val publishedAt = jsonObject.getString("publishedAt")

            newsList.add(NewsItem(title, imageUrl, publishedAt))
        }
        return newsList
    }
}

data class NewsItem(
    val title: String,
    val imageUrl: String,
    val publishedAt: String
)
