package com.lucgu.findmycurrencies.data.local.source

import com.lucgu.findmycurrencies.R
import com.lucgu.findmycurrencies.data.local.dao.ExchangeRatesDao
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity
import com.lucgu.findmycurrencies.data.model.APIError
import com.lucgu.findmycurrencies.data.remote.model.DataState
import com.lucgu.findmycurrencies.utils.ApiErrorConstants
import com.lucgu.findmycurrencies.utils.Constants
import com.lucgu.findmycurrencies.utils.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExchangeRateLocalDataSourceImpl(
    private val exchangeRatesDao: ExchangeRatesDao,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val resourceProvider: ResourceProvider,
): ExchangeRateLocalDataSource {

    override suspend fun getExchangeRates(): Flow<DataState<List<ExchangeRateEntity>>> = flow{
        try {
            val exchangeRates = exchangeRatesDao.getExchangeRates()
            emit(DataState.Success(exchangeRates))
        } catch (e: Exception) {
            emit(DataState.Error(APIError(ApiErrorConstants.GENERIC_ERROR,
                resourceProvider.getString(R.string.failed_to_fetch_from_local_database))))
        }
    }

    override suspend fun insertExchangeRates(rates: List<ExchangeRateEntity>) {
        try {
            exchangeRatesDao.insertExchangeRates( rates= rates)
        } catch (e: Exception) {
            //Do Nothing
        }
    }

    override suspend fun setLastUpdateTimestamp(timestamp: Long) {
        try {
            sharedPreferencesHelper.saveLong(Constants.SHARED_PREF_LAST_UPDATE_TIMESTAMP, timestamp)
        } catch (e: Exception) {
         //Do Nothing
        }
    }

    override suspend fun getLastUpdateTimestamp(): Flow<DataState<Long>> = flow{
        try {
            val timestamp = sharedPreferencesHelper.getLong(Constants.SHARED_PREF_LAST_UPDATE_TIMESTAMP)
            emit(DataState.Success(timestamp))
        } catch (e: Exception) {
            emit(DataState.Error(APIError(ApiErrorConstants.GENERIC_ERROR, resourceProvider.getString(R.string.failed_to_fetch_from_local_database))))
        }
    }

}