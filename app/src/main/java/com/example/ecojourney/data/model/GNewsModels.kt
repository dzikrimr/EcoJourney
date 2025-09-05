package com.example.ecojourney.data.model

data class GNewsResponse(
    val articles: List<GNewsArticle>
)

data class GNewsArticle(
    val title: String,
    val description: String,
    val content: String?,
    val url: String,
    val image: String?,
    val publishedAt: String,
    val source: GNewsSource
)

data class GNewsSource(
    val name: String,
    val url: String
)