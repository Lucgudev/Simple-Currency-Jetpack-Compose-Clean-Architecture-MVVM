package com.lucgu.findmycurrencies.data.repository

import com.lucgu.findmycurrencies.R
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity
import com.lucgu.findmycurrencies.data.local.source.ExchangeRateLocalDataSource
import com.lucgu.findmycurrencies.data.model.APIError
import com.lucgu.findmycurrencies.data.remote.model.DataState
import com.lucgu.findmycurrencies.data.remote.source.ExchangeRateRemoteDataSource
import com.lucgu.findmycurrencies.domain.repositories.ExchangeRatesRepository
import com.lucgu.findmycurrencies.utils.ApiErrorConstants
import com.lucgu.findmycurrencies.utils.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class ExchangeRateRepositoryImpl(
    private val exchangeRateRemoteDataSource: ExchangeRateRemoteDataSource,
    private val exchangeRateLocalDataSource: ExchangeRateLocalDataSource,
    private val resourceProvider: ResourceProvider
) : ExchangeRatesRepository {

    override suspend fun getExchangeRates(currentTime: Long): Flow<DataState<List<ExchangeRateEntity>>> =
        flow {
            if (shouldFetchFromNetwork(currentTime)) {
                //fetch from api
                val data = exchangeRateRemoteDataSource.getExchangeRates()
                val currencyList = exchangeRateRemoteDataSource.getCurrencyList()

                //TODO (Tech Debt): This can be improved by store list currencies in local, If the currency List failed to load, next time user fetch data, handle to fetch list currency
                combine(data, currencyList) { data, currencyList ->
                    when {
                        //Both currency and exchange rates success
                        data is DataState.Success && currencyList is DataState.Success -> {
                            val response = data.data
                            val listData = data.data.rates.map {
                                ExchangeRateEntity(
                                    it.key,
                                    it.value,
                                    response.base,
                                    currencyList.data[it.key] ?: "",
                                    0.toBigDecimal(),
                                    "0.0000"
                                )
                            }
                            DataState.Success(listData)
                        }

                        //Handle if currencies failed but Exchange Rates success
                        data is DataState.Success && currencyList is DataState.Error -> {
                            val response = data.data
                            val listData = data.data.rates.map {
                                ExchangeRateEntity(
                                    it.key,
                                    it.value,
                                    response.base,
                                    "",
                                    0.toBigDecimal(),
                                    "0.0000"
                                )
                            }
                            DataState.Success(listData)
                        }

                        //Exchange Rates Error
                        data is DataState.Error -> {
                            DataState.Error(data.apiError)
                        }

                        else -> {
                            DataState.Loading()
                        }

                    }
                }.collect {
                    when(it) {
                        is DataState.Error -> {
                            exchangeRateLocalDataSource.getExchangeRates().collect {
                                when(it) {
                                    is DataState.Error -> {
                                        emit(DataState.Error(it.apiError))
                                    }
                                    is DataState.Loading -> {
                                        emit(DataState.Loading())
                                    }
                                    is DataState.Success -> {
                                        if (it.data.isNotEmpty()) {
                                            emit(DataState.Success(it.data))
                                        } else {
                                            emit(DataState.Error(APIError(ApiErrorConstants.GENERIC_ERROR,
                                                resourceProvider.getString(
                                                    R.string.no_data_found
                                                ))))
                                        }
                                    }
                                }
                            }
                        }
                        is DataState.Loading -> {
                            emit(DataState.Loading())
                        }
                        is DataState.Success -> {
                            exchangeRateLocalDataSource.insertExchangeRates(it.data)
                            exchangeRateLocalDataSource.setLastUpdateTimestamp(currentTime)
                            emit(DataState.Success(it.data))
                        }
                    }
                }
            } else {
                exchangeRateLocalDataSource.getExchangeRates().collect {
                    when(it) {
                        is DataState.Error -> {
                            emit(DataState.Error(APIError(ApiErrorConstants.GENERIC_ERROR,
                                resourceProvider.getString(
                                    R.string.failed_to_fetch_database
                                ))))
                        }
                        is DataState.Loading -> {
                            emit(DataState.Loading())
                        }
                        is DataState.Success -> {
                            val listData = it.data.map {
                                it.copy(
                                    amount = it.rate.toBigDecimal(),
                                    amountUi = "0.0000"
                                )
                            }
                            emit(DataState.Success(listData))
                        }
                    }
                }
            }
        }


    private suspend fun shouldFetchFromNetwork(currentTime: Long): Boolean {
        var result = false
        exchangeRateLocalDataSource.getLastUpdateTimestamp().collect {
            when(it) {
                is DataState.Error -> {
                    result = true
                }
                is DataState.Loading -> {
                    //Do Nothing
                }
                is DataState.Success -> {
                    val lastFetch = it.data
                    val range = 30*60
                    val expiryTime = lastFetch + range.toLong()
                    val needFetch = currentTime > expiryTime
                    result = needFetch
                }
            }
        }

        return result
    }
}