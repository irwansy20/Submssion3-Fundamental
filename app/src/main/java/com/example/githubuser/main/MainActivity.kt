package com.example.githubuser.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubuser.model.MainViewModel
import com.example.githubuser.adapter.ListUserAdapter
import com.example.githubuser.R
import com.example.githubuser.settings.SettingViewModel
import com.example.githubuser.settings.ViewModelFactory
import com.example.githubuser.databinding.ActivityMainBinding
import com.example.githubuser.response.SearchResponse
import com.example.githubuser.settings.SettingPreferences
import com.google.android.material.switchmaterial.SwitchMaterial

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvUser: RecyclerView
    private lateinit var mainViewModel: MainViewModel
    private lateinit var settingViewModel: SettingViewModel

    lateinit var listAdapter: ListUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvUser = findViewById(R.id.rv_user)
        rvUser.setHasFixedSize(true)

        val pref = SettingPreferences.getInstance(dataStore)
        settingViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(
            SettingViewModel::class.java
        )
        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(MainViewModel::class.java)

        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this,layoutManager.orientation)
        binding.rvUser.addItemDecoration(itemDecoration)

        mainViewModel.message.observe(this,{
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })

        mainViewModel.isLoading.observe(this,{
            showLoading(it)
        })

        mainViewModel.user.observe(this, {
            if (it != null){
                listAdapter.setData(it)
                showLoading(false)
            }
        })

        setupRecycleView()
    }

    private fun setupRecycleView() {
        listAdapter = ListUserAdapter(arrayListOf())
        rvUser.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = listAdapter
        }
        listAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback{
            override fun onItemClicked(items: SearchResponse.ItemsItem) {
                showSelectedUser(items.login, items.id, items.avatarUrl)
            }
        })
    }

    private fun showSelectedUser(username: String, id: Int, avatarUrl: String) {
        val moveIntent = Intent(this@MainActivity, MoveActivity::class.java)
        moveIntent.putExtra(MoveActivity.KEY_USER,username)
        moveIntent.putExtra(MoveActivity.KEY_ID, id)
        moveIntent.putExtra(MoveActivity.KEY_AVA, avatarUrl)
        startActivity(moveIntent)
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    //searchview
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val switchTheme = menu.findItem(R.id.app_bar_switch).actionView as SwitchMaterial


        settingViewModel.getThemeSettings().observe(this,
            {isDarkModeActive: Boolean ->
                if (isDarkModeActive){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchTheme.isChecked = true
                } else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            })
        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
           settingViewModel.saveThemeSetting(isChecked)
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(querry: String): Boolean {
                mainViewModel.findSearch(querry)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.favorite ->
                Intent(this, FavoriteActivity::class.java).also {
                    startActivity(it)
                }
        }
        return super.onOptionsItemSelected(item)
    }
}