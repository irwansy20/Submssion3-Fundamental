package com.example.githubuser.settings

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

class SettingPreferences private constructor(private val datastore: DataStore<Preferences>) {

    private val THEME_KEY = booleanPreferencesKey("theme_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return datastore.data.map {preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean){
        datastore.edit { preference ->
            preference[THEME_KEY] = isDarkModeActive
        }
    }
    companion object{
        @Volatile
        private var INSTACE: SettingPreferences? = null

        fun getInstance(datastore: DataStore<Preferences>): SettingPreferences {
            return INSTACE ?: synchronized(this){
                val instance = SettingPreferences(datastore)
                INSTACE = instance
                instance
            }
        }
    }
}