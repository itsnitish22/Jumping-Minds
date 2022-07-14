package com.example.jumpingminds.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.jumpingminds.api.models.News

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: List<NewsEntity>)

    @Query("SELECT * FROM news ORDER BY id ASC")
    fun getAll(): LiveData<List<NewsEntity>>
}