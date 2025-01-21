package com.lucgu.findmycurrencies.data.repository

import app.cash.turbine.test
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity
import com.lucgu.findmycurrencies.data.local.source.ExchangeRateLocalDataSource
import com.lucgu.findmycurrencies.data.model.APIError
import com.lucgu.findmycurrencies.data.remote.model.DataState
import com.lucgu.findmycurrencies.data.remote.model.ExchangeRateResponse
import com.lucgu.findmycurrencies.data.remote.source.ExchangeRateRemoteDataSource
import com.lucgu.findmycurrencies.utils.ResourceProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class ExchangeRateRepositoryImplTest {

    private lateinit var mockRemoteDataSource: ExchangeRateRemoteDataSource
    private lateinit var mockLocalDataSource: ExchangeRateLocalDataSource
    private lateinit var mockResourceProvider: ResourceProvider

    private lateinit var repository: ExchangeRateRepositoryImpl

    @Before
    fun setup() {
        mockRemoteDataSource = mock(ExchangeRateRemoteDataSource::class.java)
        mockLocalDataSource = mock(ExchangeRateLocalDataSource::class.java)
        mockResourceProvider = mock(ResourceProvider::class.java)
        repository = ExchangeRateRepositoryImpl(mockRemoteDataSource, mockLocalDataSource, mockResourceProvider)
    }

    @Test
    fun `getExchangeRates fetches from remote if shouldFetchFromNetwork is true`() = runTest {
        // Given
        val lastFetch = 1734330026L
        val currentTime = 1734340026L
        val mockRemoteRates = DataState.Success(
            ExchangeRateResponse(
                base = "USD",
                rates = mapOf("EUR" to 0.85, "JPY" to 110.0),
            )
        )
        val mockCurrencyList = DataState.Success(
            mapOf(
                "USD" to "United States Dollar",
                "EUR" to "Euro"
            )
        )
        val mockLocalRates = DataState.Success(listOf<ExchangeRateEntity>())

        //When
        `when`(mockRemoteDataSource.getExchangeRates()).thenReturn(flowOf(mockRemoteRates))
        `when`(mockRemoteDataSource.getCurrencyList()).thenReturn(flowOf(mockCurrencyList))
        `when`(mockLocalDataSource.getExchangeRates()).thenReturn(flowOf(mockLocalRates))
        `when`(mockLocalDataSource.getLastUpdateTimestamp()).thenReturn(flowOf(DataState.Success(lastFetch)))

        // Then
        repository.getExchangeRates(currentTime).test {
            val successState = awaitItem()
            assert(successState is DataState.Success)
            val resultData = (successState as DataState.Success).data
            assertEquals(2, resultData.size)
            awaitComplete()
        }
    }

    @Test
    fun `getExchangeRates fetches from local if shouldFetchFromNetwork is false`() = runTest {
        // Given
        val lastFetch = 1734330026L
        val currentTime = 1733340029L
        val mockLocalRates = DataState.Success(
            listOf(
                ExchangeRateEntity(
                    currency = "IDR",
                    rate = 1.0,
                    base = "USD",
                    currencyName = "Indonesian Rupiah",
                )
            )
        )

        // When
        `when`(mockLocalDataSource.getExchangeRates()).thenReturn(flowOf(mockLocalRates))
        `when`(mockLocalDataSource.getLastUpdateTimestamp()).thenReturn(flowOf(DataState.Success(lastFetch)))

        // Then
        repository.getExchangeRates(currentTime).test {
            val successState = awaitItem()
            assert(successState is DataState.Success)
            val resultData = (successState as DataState.Success).data

            assertEquals(1, resultData.size)
            assertEquals("IDR", resultData[0].currency)

            awaitComplete()
        }
    }

    @Test
    fun `getExchangeRates emits error when both remote and local fail`() = runTest {
        // Given
        val currentTime = 1633046400L

        // When
        `when`(mockRemoteDataSource.getExchangeRates()).thenReturn(flowOf(DataState.Error(APIError(-1, "Remote fetch failed"))))
        `when`(mockRemoteDataSource.getCurrencyList()).thenReturn(flowOf(DataState.Error(APIError(-1, "Remote fetch failed"))))
        `when`(mockLocalDataSource.getExchangeRates()).thenReturn(flowOf(DataState.Error(APIError(-1, "Local fetch failed"))))
        `when`(mockLocalDataSource.getLastUpdateTimestamp()).thenReturn(flowOf(DataState.Error(
            APIError(-1, "No timestamp found")
        )))

        // Then
        repository.getExchangeRates(currentTime).test {
            val errorState = awaitItem()
            assert(errorState is DataState.Error)
            assertEquals("Local fetch failed", (errorState as DataState.Error).apiError?.message)

            awaitComplete()
        }
    }
}
