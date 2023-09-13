package com.example.githubuser.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.githubuser.db.Favorite
import com.example.githubuser.db.FavoriteDao
import com.example.githubuser.db.FavoriteRoomDatabase
import com.example.githubuser.response.DetailResponse
import com.example.githubuser.retrofit.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoveViewModel(application: Application) : AndroidViewModel(application) {
    private var favDao: FavoriteDao?
    private var db: FavoriteRoomDatabase?

    init {
        db = FavoriteRoomDatabase.getDatabase(application)
        favDao = db?.favoriteDao()
    }

    private val _detailUser = MutableLiveData<DetailResponse>()
    val detailUser: LiveData<DetailResponse> = _detailUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "DetailUser"
    }

    fun findUser(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetail(username)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detailUser.value = (response.body()!!)
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun addFav(username: String, id: Int, avatarUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var user = Favorite(
                id,
                username,
                avatarUrl
            )
            favDao?.addFav(user)
        }
    }

    suspend fun checkUser(id: Int) = favDao?.checkUser(id)

    fun removeFromFavorite(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            favDao?.removeFromFavorite(id)
        }
    }
}