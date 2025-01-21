package com.lucgu.findmycurrencies.data.local.source

import android.content.SharedPreferences

class SharedPreferencesHelperImpl(private val sharedPreferences: SharedPreferences) : SharedPreferencesHelper {

    override fun saveLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }
}