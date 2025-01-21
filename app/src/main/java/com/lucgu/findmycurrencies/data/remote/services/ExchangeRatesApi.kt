package com.lucgu.findmycurrencies.data.remote.services

import com.lucgu.findmycurrencies.data.remote.model.CurrencyListResponse
import com.lucgu.findmycurrencies.data.remote.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRatesApi {

    @GET("latest.json")
    suspend fun getLatestRates(@Query("app_id") appId: String): Response<ExchangeRateResponse>

    @GET("currencies.json")
    suspend fun getCurrencies(@Query("app_id") appId: String): Response<CurrencyListResponse>
}