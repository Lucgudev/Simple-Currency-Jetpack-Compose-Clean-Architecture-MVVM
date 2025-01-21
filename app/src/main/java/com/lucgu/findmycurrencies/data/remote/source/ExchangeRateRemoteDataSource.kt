package com.lucgu.findmycurrencies.data.remote.source

import com.lucgu.findmycurrencies.data.remote.model.CurrencyListResponse
import com.lucgu.findmycurrencies.data.remote.model.ExchangeRateResponse
import com.lucgu.findmycurrencies.data.remote.model.DataState
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRemoteDataSource {
    suspend fun getExchangeRates(): Flow<DataState<ExchangeRateResponse>>
    suspend fun getCurrencyList(): Flow<DataState<CurrencyListResponse>>
}