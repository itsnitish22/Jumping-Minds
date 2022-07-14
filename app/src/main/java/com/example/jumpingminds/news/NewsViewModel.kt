package com.example.jumpingminds.news

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.jumpingminds.api.RetrofitInstance
import com.example.jumpingminds.api.models.Articles
import com.example.jumpingminds.api.models.News
import com.example.jumpingminds.database.AppDatabase
import com.example.jumpingminds.database.NewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(app: Application) : AndroidViewModel(app) {
    private val database = AppDatabase.getInstance(app)

    //to update the progress dialog
    private val _receivedNews: MutableLiveData<News> = MutableLiveData()
    val receivedNews: LiveData<News>
        get() = _receivedNews

    //data received will be updated here
    private val _jobCompleted = MutableLiveData(false)
    val jobCompleted: LiveData<Boolean>
        get() = _jobCompleted


    //getAllNewsFromDB
    val newsListFromDB = database?.newsDao()?.getAll()

    //saving news into DB
    fun saveFetchedNewsToDB(news: ArrayList<Articles>) {
        Log.i("Inside saving", news.size.toString())
        val newsList: ArrayList<NewsEntity> = arrayListOf()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                for (i in 0 until news.size) {
                    newsList.add(
                        NewsEntity(
                            i,
                            news[i].title,
                            news[i].publishedAt,
                            news[i].source.name,
                            news[i].content,
                            news[i].urlToImage
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database?.newsDao()?.insertNews(newsList)
                Log.i("NewsViewModel", "Saved news to DB")
                Log.i("NewsViewModel", newsList.toString())
            }
        }
    }

    //function call to GET data from API
    fun getNews() {
        viewModelScope.launch {
            val fetchedNews = RetrofitInstance.api.getNews()
            _receivedNews.value = fetchedNews
            _jobCompleted.value = true
            Log.i("NewsViewModel", fetchedNews.toString())
        }
    }
}