package com.lucgu.findmycurrencies.data.local.source

import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity
import com.lucgu.findmycurrencies.data.remote.model.DataState
import kotlinx.coroutines.flow.Flow

interface ExchangeRateLocalDataSource {
    suspend fun getExchangeRates(): Flow<DataState<List<ExchangeRateEntity>>>
    suspend fun insertExchangeRates(rates: List<ExchangeRateEntity>)
    suspend fun setLastUpdateTimestamp(timestamp: Long)
    suspend fun getLastUpdateTimestamp(): Flow<DataState<Long>>
}