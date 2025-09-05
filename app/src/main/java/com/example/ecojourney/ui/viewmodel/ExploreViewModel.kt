package com.example.ecojourney.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecojourney.data.repository.NewsItem
import com.example.ecojourney.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList())
    val news = _news.asStateFlow()

    fun loadNews() {
        viewModelScope.launch {
            _news.value = repository.fetchNews()
        }
    }
}
