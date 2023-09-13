package com.example.githubuser.main

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.model.FavoriteViewModel
import com.example.githubuser.R
import com.example.githubuser.adapter.ListUserAdapter
import com.example.githubuser.databinding.ActivityFavoriteBinding
import com.example.githubuser.db.Favorite
import com.example.githubuser.response.SearchResponse
import java.util.ArrayList

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favViewModel: FavoriteViewModel

    lateinit var listAdapter: ListUserAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Favorite User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_back)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        listAdapter= ListUserAdapter(arrayListOf())
        listAdapter.notifyDataSetChanged()

        favViewModel = ViewModelProvider(this)
            .get(FavoriteViewModel::class.java)

        listAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback{
            override fun onItemClicked(items: SearchResponse.ItemsItem) {
                Intent(this@FavoriteActivity, MoveActivity::class.java).also {
                    it.putExtra(MoveActivity.KEY_USER, items.login)
                    it.putExtra(MoveActivity.KEY_ID, items.id)
                    it.putExtra(MoveActivity.KEY_AVA, items.avatarUrl)
                    startActivity(it)
                }
            }
        })

        binding.apply {
            rvFavorite.layoutManager = LinearLayoutManager(this@FavoriteActivity)
            rvFavorite.adapter = listAdapter
        }

        favViewModel.getFav()?.observe(this, {
            if(it!=null){
                val list = mapList(it)
                listAdapter.setData(list)
            }
        })
    }

    private fun mapList(fav: List<Favorite>): ArrayList<SearchResponse.ItemsItem> {
        val listUser = ArrayList<SearchResponse.ItemsItem>()
        for (user in fav){
            val userMapped = SearchResponse.ItemsItem(
                user.login.toString(),
                user.avatarUrl.toString(),
                user.id
            )
            listUser.add(userMapped)
        }
        return listUser
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}