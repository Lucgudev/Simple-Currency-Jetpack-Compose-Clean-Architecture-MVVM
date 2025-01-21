package com.lucgu.findmycurrencies.data.remote.model

data class ExchangeRateResponse(
    val rates: Map<String, Double>,
    val base: String,
)
