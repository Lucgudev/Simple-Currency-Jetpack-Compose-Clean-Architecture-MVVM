package com.lucgu.findmycurrencies.domain.repositories

import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity
import com.lucgu.findmycurrencies.data.remote.model.DataState
import kotlinx.coroutines.flow.Flow

interface ExchangeRatesRepository {
    suspend fun getExchangeRates(currentTime: Long): Flow<DataState<List<ExchangeRateEntity>>>
}