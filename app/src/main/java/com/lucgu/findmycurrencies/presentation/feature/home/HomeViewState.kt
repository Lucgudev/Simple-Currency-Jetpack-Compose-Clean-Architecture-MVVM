package com.lucgu.findmycurrencies.presentation.feature.home

import androidx.compose.runtime.Stable
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity
import com.lucgu.findmycurrencies.domain.viewstate.IViewState

@Stable
data class HomeViewState(
    val isLoading: Boolean = false,
    val exchangeRates: List<ExchangeRateEntity> = listOf(),
    val originCurrencyName: String = "",
    val originCurrency: String = "",
    val originValueUi: String = "0.0",
    val originValue: Double = 0.0,
    val showCurrencyPickDialog: Boolean = false,
    val errorMessage: String = "",
): IViewState