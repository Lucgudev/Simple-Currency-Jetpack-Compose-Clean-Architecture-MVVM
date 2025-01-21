package com.lucgu.findmycurrencies.presentation.feature.home

import androidx.lifecycle.viewModelScope
import com.lucgu.findmycurrencies.R
import com.lucgu.findmycurrencies.data.remote.model.DataState
import com.lucgu.findmycurrencies.domain.repositories.ExchangeRatesRepository
import com.lucgu.findmycurrencies.presentation.base.BaseViewModel
import com.lucgu.findmycurrencies.utils.Constants
import com.lucgu.findmycurrencies.utils.CurrencyMapper
import com.lucgu.findmycurrencies.utils.ResourceProvider
import com.lucgu.findmycurrencies.utils.TimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.RoundingMode

class HomeViewModel(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val resourceProvider: ResourceProvider,
    private val timeProvider: TimeProvider,
): BaseViewModel<HomeViewState>() {

    fun getExchangeRates() {
        viewModelScope.launch(Dispatchers.IO) {
            exchangeRatesRepository.getExchangeRates(timeProvider.getCurrentTimeSecond()).collect {
                when(it) {
                    is DataState.Error -> {
                        setState { currentState.copy(isLoading = false, errorMessage = it.apiError?.message ?: resourceProvider.getString(
                            R.string.home_view_model_generic_error
                        )) }
                    }
                    is DataState.Loading -> {
                        setState { currentState.copy(isLoading = true, errorMessage = "") }

                    }
                    is DataState.Success -> {
                        setState { currentState.copy(
                            isLoading = false,
                            exchangeRates = it.data,
                            originCurrency = it.data[0].base,
                            originCurrencyName = it.data.find { it.currency == it.base }?.currencyName ?: "",
                            originValueUi = "",
                            errorMessage = "",
                            )
                        }
                    }
                }
            }
        }
    }

    override fun createInitialState(): HomeViewState = HomeViewState()

    fun clickChangeOrigin() {
        setState { currentState.copy(showCurrencyPickDialog = true) }
    }

    fun onDismissCurrencyPickDialog(currencyPick: String) {
        var originCurrency = currentState.originCurrency
        if(currencyPick.isNotEmpty()) {
            originCurrency = currencyPick
        }
        setState {
            currentState.copy(
                showCurrencyPickDialog = false,
                originCurrency = originCurrency,
                originCurrencyName = currentState.exchangeRates.find { it.currency == originCurrency }?.currencyName ?: "")
        }
        convertCurrency(currentState.originValueUi)
    }

    fun convertCurrency(inputAmount: String) {
            val originAmount = inputAmount.toBigDecimalOrNull()?.setScale(2, RoundingMode.DOWN)

            if (originAmount != null) {

                //Update the text field to use String format of amount (to get .00)
                setState {
                    currentState.copy(originValueUi = originAmount?.toPlainString() ?: "")
                }

                if(currentState.originCurrency == Constants.BASE_USD) {
                    val updatedCurrencies = currentState.exchangeRates.map {
                        val convertedAmount = it.rate.toBigDecimal() * originAmount
                        it.copy(
                            amount = convertedAmount,
                            amountUi = CurrencyMapper.formatToCurrency(convertedAmount)
                        )
                    }
                    setState {
                        currentState.copy(exchangeRates = updatedCurrencies)
                    }
                } else {
                    val usdRate = currentState.exchangeRates.find { it.currency == currentState.originCurrency }?.rate ?: 1.0
                    val usdAmount = originAmount / usdRate.toBigDecimal()
                    val formattedUsdAmount = usdAmount.setScale(6, RoundingMode.DOWN)
                    val updatedCurrencies = currentState.exchangeRates.map {
                        val convertedAmount = formattedUsdAmount * it.rate.toBigDecimal()
                        it.copy(
                            amount = convertedAmount,
                            amountUi = CurrencyMapper.formatToCurrency(convertedAmount)
                        )
                    }
                    setState {
                        currentState.copy(exchangeRates = updatedCurrencies)
                    }
                }
            }
    }
}
