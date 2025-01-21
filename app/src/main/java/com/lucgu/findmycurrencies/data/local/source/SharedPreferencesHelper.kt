package com.lucgu.findmycurrencies.data.local.source

interface SharedPreferencesHelper {
    fun saveLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0): Long

}