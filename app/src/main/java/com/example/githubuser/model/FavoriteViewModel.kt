package com.example.githubuser.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.githubuser.db.Favorite
import com.example.githubuser.db.FavoriteDao
import com.example.githubuser.db.FavoriteRoomDatabase

class FavoriteViewModel(application: Application): AndroidViewModel(application) {
    private var favDao: FavoriteDao?
    private var db: FavoriteRoomDatabase?

    init {
        db = FavoriteRoomDatabase.getDatabase(application)
        favDao = db?.favoriteDao()
    }

    fun getFav() : LiveData<List<Favorite>>?{
        return favDao?.getAllFavorite()
    }
}