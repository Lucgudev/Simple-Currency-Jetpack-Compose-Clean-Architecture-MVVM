package com.lucgu.findmycurrencies.data.remote.source

import com.lucgu.findmycurrencies.BuildConfig
import com.lucgu.findmycurrencies.data.remote.model.CurrencyListResponse
import com.lucgu.findmycurrencies.data.remote.model.DataState
import com.lucgu.findmycurrencies.data.remote.model.ExchangeRateResponse
import com.lucgu.findmycurrencies.data.remote.services.ExchangeRatesApi
import kotlinx.coroutines.flow.Flow

class ExchangeRateRemoteDataSourceImpl(private val exchangeRatesApi: ExchangeRatesApi) : BaseRemoteDataSource(), ExchangeRateRemoteDataSource {
    override suspend fun getExchangeRates(): Flow<DataState<ExchangeRateResponse>> {
        return getResult { exchangeRatesApi.getLatestRates(BuildConfig.exchangeRatesApiKey) }
    }

    override suspend fun getCurrencyList(): Flow<DataState<CurrencyListResponse>> {
        return getResult { exchangeRatesApi.getCurrencies(BuildConfig.exchangeRatesApiKey) }
    }
}