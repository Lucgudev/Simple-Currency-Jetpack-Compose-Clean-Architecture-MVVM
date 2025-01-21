package com.lucgu.findmycurrencies.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lucgu.findmycurrencies.data.local.dao.ExchangeRatesDao
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity

@Database(entities = [ExchangeRateEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exchangeRatesDao(): ExchangeRatesDao
}