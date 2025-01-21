package com.lucgu.findmycurrencies.data.remote.source

import app.cash.turbine.test
import com.lucgu.findmycurrencies.data.remote.model.CurrencyListResponse
import com.lucgu.findmycurrencies.data.remote.model.DataState
import com.lucgu.findmycurrencies.data.remote.model.ExchangeRateResponse
import com.lucgu.findmycurrencies.data.remote.services.ExchangeRatesApi
import com.lucgu.findmycurrencies.utils.ApiErrorConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class ExchangeRateRemoteDataSourceImplTest {

    private val exchangeRatesApi: ExchangeRatesApi = mock(ExchangeRatesApi::class.java)
    private val dataSource = ExchangeRateRemoteDataSourceImpl(exchangeRatesApi)

    @Test
    fun `getExchangeRates emits Success when API call is successful`() = runTest {
        // Given
        val mockResponse = ExchangeRateResponse(
            base = "USD",
            rates = mapOf("EUR" to 0.85, "JPY" to 110.0),
        )
        // When
        `when`(exchangeRatesApi.getLatestRates(anyString())).thenReturn(Response.success(mockResponse))

        // Then
        dataSource.getExchangeRates().test {
            val loadingResult = awaitItem()
            assert(loadingResult is DataState.Loading)

            // Then, it should emit Success
            val successResult = awaitItem()
            assert(successResult is DataState.Success)
            assertEquals(mockResponse, (successResult as DataState.Success).data)

            awaitComplete()
        }
    }

    @Test
    fun `getExchangeRates emits Error when API call fails`() = runTest {
        // Given
        val errorResponse = Response.error<ExchangeRateResponse>(
            500,
            ResponseBody.create(null, "Internal Server Error")
        )
        val exception = HttpException(errorResponse)

        // When
        `when`(exchangeRatesApi.getLatestRates(anyString())).thenThrow(exception)

        // Then
        dataSource.getExchangeRates().test {
            val result = awaitItem()
            assert(result is DataState.Loading)

            val errorResult = awaitItem()
            assertEquals(ApiErrorConstants.GENERIC_ERROR, (errorResult as DataState.Error).apiError?.code)
            awaitComplete()
        }
    }

    @Test
    fun `getCurrencyList emits Error when API call fails`() = runTest {
        // Given
        val errorResponse = Response.error<CurrencyListResponse>(
            404,
            ResponseBody.create(null, "Not Found")
        )
        val exception = HttpException(errorResponse)

        // When
        `when`(exchangeRatesApi.getCurrencies(anyString())).thenThrow(exception)

        // Then
        dataSource.getCurrencyList().test {
            val result = awaitItem()
            assert(result is DataState.Loading)

            val errorResult = awaitItem()
            assert(errorResult is DataState.Error)
            assertEquals(ApiErrorConstants.GENERIC_ERROR, (errorResult as DataState.Error).apiError?.code)
            awaitComplete()
        }
    }
}

