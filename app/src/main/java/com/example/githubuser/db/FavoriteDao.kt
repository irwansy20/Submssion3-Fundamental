package com.example.githubuser.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDao {
    @Insert
    suspend fun addFav(favorite: Favorite)

    @Query("SELECT * FROM favorite")
    fun getAllFavorite(): LiveData<List<Favorite>>

    @Query("SELECT count(*) FROM favorite WHERE favorite.id = :id")
    suspend fun checkUser(id: Int): Int

    @Query("DELETE FROM favorite WHERE favorite.id = :id")
    suspend fun  removeFromFavorite(id: Int): Int
}