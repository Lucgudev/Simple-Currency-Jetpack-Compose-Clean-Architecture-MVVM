package com.lucgu.findmycurrencies.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity

@Dao
interface ExchangeRatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(rates: List<ExchangeRateEntity>)

    @Query("SELECT * FROM exchange_rate")
    suspend fun getExchangeRates(): List<ExchangeRateEntity>
}