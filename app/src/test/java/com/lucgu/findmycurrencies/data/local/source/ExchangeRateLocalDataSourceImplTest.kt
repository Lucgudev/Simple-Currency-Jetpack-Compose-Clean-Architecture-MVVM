package com.lucgu.findmycurrencies.data.local.source

import com.lucgu.findmycurrencies.data.local.dao.ExchangeRatesDao
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity
import com.lucgu.findmycurrencies.data.remote.model.DataState
import com.lucgu.findmycurrencies.utils.Constants
import com.lucgu.findmycurrencies.utils.ResourceProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class ExchangeRateLocalDataSourceImplTest {
    private lateinit var exchangeRateLocalDataSource: ExchangeRateLocalDataSourceImpl
    private lateinit var exchangeRatesDao: ExchangeRatesDao
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var resourceProvider: ResourceProvider

    @Before
    fun setup() {
        exchangeRatesDao = mock(ExchangeRatesDao::class.java)
        sharedPreferencesHelper = mock(SharedPreferencesHelper::class.java)
        resourceProvider = mock(ResourceProvider::class.java)
        exchangeRateLocalDataSource = ExchangeRateLocalDataSourceImpl(exchangeRatesDao, sharedPreferencesHelper, resourceProvider)
    }

    @Test
    fun `test getExchangeRates success`() = runTest {
        // Given
        val mockRates = listOf(ExchangeRateEntity("USD", 1.0, "USD", "USD"))

        //When
        `when`(exchangeRatesDao.getExchangeRates()).thenReturn(mockRates)

        // Then
        val result = exchangeRateLocalDataSource.getExchangeRates()

        result.collect { dataState ->
            assertTrue(dataState is DataState.Success)
            assertEquals(mockRates, (dataState as DataState.Success).data)
        }

        verify(exchangeRatesDao).getExchangeRates()
    }

    @Test
    fun `test getExchangeRates error`() = runTest {
        // Given
        `when`(exchangeRatesDao.getExchangeRates()).thenThrow(RuntimeException("Database error"))
        `when`(resourceProvider.getString(anyInt())).thenReturn("Failed to fetch from local database")

        // When
        val result = exchangeRateLocalDataSource.getExchangeRates()

        // Then
        result.collect { dataState ->
            assertTrue(dataState is DataState.Error)
            assertEquals("Failed to fetch from local database", (dataState as DataState.Error).apiError?.message)
        }
    }

    @Test
    fun `test getLastUpdateTimestamp success`() = runTest {
        // Given
        val mockTimestamp = 1630000000L
        `when`(sharedPreferencesHelper.getLong(Constants.SHARED_PREF_LAST_UPDATE_TIMESTAMP)).thenReturn(mockTimestamp)

        // When
        val result = exchangeRateLocalDataSource.getLastUpdateTimestamp()

        // Then
        result.collect { dataState ->
            assertTrue(dataState is DataState.Success)
            assertEquals(mockTimestamp, (dataState as DataState.Success).data)
        }

        verify(sharedPreferencesHelper).getLong(Constants.SHARED_PREF_LAST_UPDATE_TIMESTAMP)
    }

    @Test
    fun `test getLastUpdateTimestamp error`() = runTest {
        // Given
        `when`(sharedPreferencesHelper.getLong(Constants.SHARED_PREF_LAST_UPDATE_TIMESTAMP)).thenThrow(RuntimeException("Database error"))
        `when`(resourceProvider.getString(anyInt())).thenReturn("Failed to fetch from local database")

        // When
        val result = exchangeRateLocalDataSource.getLastUpdateTimestamp()

        // Then
        result.collect { dataState ->
            assertTrue(dataState is DataState.Error)
            assertEquals("Failed to fetch from local database", (dataState as DataState.Error).apiError?.message)
        }

        verify(sharedPreferencesHelper).getLong(Constants.SHARED_PREF_LAST_UPDATE_TIMESTAMP)
    }

    @Test
    fun `test insertExchangeRates success`() = runTest {
        // Given
        val mockRates = listOf(ExchangeRateEntity("USD", 1.0, "USD", "USD"))

        // When
        exchangeRateLocalDataSource.insertExchangeRates(mockRates)

        // Then
        verify(exchangeRatesDao).insertExchangeRates(mockRates)
    }

    @Test
    fun `test insertExchangeRates error`() = runTest {
        // Given
        val mockRates = listOf(ExchangeRateEntity("USD", 1.0, "USD", "USD"))
        doThrow(RuntimeException("Database error")).`when`(exchangeRatesDao).insertExchangeRates(mockRates)

        // When
        exchangeRateLocalDataSource.insertExchangeRates(mockRates)

        // Then
        verify(exchangeRatesDao).insertExchangeRates(mockRates) // We expect the method to be called, but error is handled silently.
    }
}