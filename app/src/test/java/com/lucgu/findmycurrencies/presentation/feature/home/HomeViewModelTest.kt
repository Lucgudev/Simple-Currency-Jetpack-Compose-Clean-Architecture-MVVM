package com.lucgu.findmycurrencies.presentation.feature.home

import app.cash.turbine.test
import com.lucgu.findmycurrencies.R
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity
import com.lucgu.findmycurrencies.data.model.APIError
import com.lucgu.findmycurrencies.data.remote.model.DataState
import com.lucgu.findmycurrencies.domain.repositories.ExchangeRatesRepository
import com.lucgu.findmycurrencies.utils.ApiErrorConstants
import com.lucgu.findmycurrencies.utils.ResourceProvider
import com.lucgu.findmycurrencies.utils.TimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val exchangeRatesRepository: ExchangeRatesRepository = mock()
    private val resourceProvider: ResourceProvider = mock()
    private val timeProvider: TimeProvider = mock()

    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Use test dispatcher for coroutines
        viewModel = HomeViewModel(exchangeRatesRepository, resourceProvider, timeProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset dispatcher after tests
    }

    @Test
    fun `getExchangeRates should emit loading and success states`() = runTest {

        val fakeExchangeRates = listOf(
            ExchangeRateEntity("IDR", 15000.0, "USD",  "Indonesian Rupiah"),
            ExchangeRateEntity("AED", 3.673, "USD", "United Arab Emirates Dirham"),
            ExchangeRateEntity("AFN", 69.982, "USD", "Afghan Afghani")
        )

        val currentTime = 1633046400L
        `when`(timeProvider.getCurrentTimeSecond()).thenReturn(currentTime)

        `when`(exchangeRatesRepository.getExchangeRates(currentTime)).thenReturn(flow {
            emit(DataState.Loading())
            emit(DataState.Success(fakeExchangeRates))
        })

        viewModel.uiState.test {
            awaitItem()
            viewModel.getExchangeRates()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(fakeExchangeRates.size, successState.exchangeRates.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getExchangeRates should emit error state on failure`() = runTest {

        val currentTime = 1633046400L
        `when`(timeProvider.getCurrentTimeSecond()).thenReturn(currentTime)

        `when`(exchangeRatesRepository.getExchangeRates(currentTime)).thenReturn(flow {
            emit(DataState.Loading())
            emit(DataState.Error(APIError(ApiErrorConstants.GENERIC_ERROR, "Generic Error")))
        })
        `when`(resourceProvider.getString(R.string.home_view_model_generic_error))
            .thenReturn("Generic Error")

        viewModel.uiState.test {
            awaitItem()
            viewModel.getExchangeRates()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Generic Error", errorState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clickChangeOrigin should update showCurrencyPickDialog to true`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.clickChangeOrigin()
            val initialState = awaitItem()
            assertTrue(initialState.showCurrencyPickDialog)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `convertCurrency should update exchangeRates with converted amounts`() = runTest {

        val fakeExchangeRates = listOf(
            ExchangeRateEntity("IDR", 15000.0, "USD",  "Indonesian Rupiah"),
            ExchangeRateEntity("AED", 3.673, "USD", "United Arab Emirates Dirham"),
            ExchangeRateEntity("AFN", 69.982, "USD", "Afghan Afghani")
        )

        val currentTime = 1633046400L
        `when`(timeProvider.getCurrentTimeSecond()).thenReturn(currentTime)

        `when`(exchangeRatesRepository.getExchangeRates(currentTime)).thenReturn(flow {
            emit(DataState.Loading())
            emit(DataState.Success(fakeExchangeRates))
        })

        viewModel.uiState.test {
            awaitItem()
            viewModel.getExchangeRates()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(fakeExchangeRates.size, successState.exchangeRates.size)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.uiState.test {
            awaitItem()
            viewModel.convertCurrency("10000")
            val initialState = awaitItem()
            assertEquals("10000.00", initialState.originValueUi)
            val updatedState = awaitItem()
            assertEquals(updatedState.exchangeRates[0].amountUi, "150,000,000.0000")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissCurrencyPickDialog should update originCurrency and originCurrencyName`() = runTest {

        val fakeExchangeRates = listOf(
            ExchangeRateEntity("IDR", 15000.0, "USD", "Indonesian Rupiah"),
            ExchangeRateEntity("AED", 3.673, "USD", "United Arab Emirates Dirham"),
            ExchangeRateEntity("AFN", 69.982, "USD", "Afghan Afghani")
        )

        val currentTime = 1633046400L
        `when`(timeProvider.getCurrentTimeSecond()).thenReturn(currentTime)

        `when`(exchangeRatesRepository.getExchangeRates(currentTime)).thenReturn(flow {
            emit(DataState.Loading())
            emit(DataState.Success(fakeExchangeRates))
        })

        viewModel.uiState.test {
            awaitItem()
            viewModel.getExchangeRates()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(fakeExchangeRates.size, successState.exchangeRates.size)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.uiState.test {
            awaitItem()
            viewModel.onDismissCurrencyPickDialog("IDR")
            val updatedState = awaitItem()
            assertEquals("IDR", updatedState.originCurrency)
            assertEquals("Indonesian Rupiah", updatedState.originCurrencyName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `convertCurrency should update exchangeRates with converted amounts for non USD`() = runTest {
        val fakeExchangeRates = listOf(
            ExchangeRateEntity("IDR", 15000.0, "USD", "Indonesian Rupiah"),
            ExchangeRateEntity("AED", 3.673, "USD", "United Arab Emirates Dirham"),
            ExchangeRateEntity("AFN", 69.982, "USD", "Afghan Afghani")
        )

        val currentTime = 1633046400L
        `when`(timeProvider.getCurrentTimeSecond()).thenReturn(currentTime)

        `when`(exchangeRatesRepository.getExchangeRates(currentTime)).thenReturn(flow {
            emit(DataState.Loading())
            emit(DataState.Success(fakeExchangeRates))
        })

        viewModel.uiState.test {
            awaitItem()
            viewModel.getExchangeRates()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(fakeExchangeRates.size, successState.exchangeRates.size)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.uiState.test {
            awaitItem()
            viewModel.onDismissCurrencyPickDialog("IDR")
            val updatedState = awaitItem()
            assertEquals("IDR", updatedState.originCurrency)
            assertEquals("Indonesian Rupiah", updatedState.originCurrencyName)
            viewModel.convertCurrency("150000000.00")
            awaitItem()
            val updatedCurrencyState = awaitItem()
            assertEquals(updatedCurrencyState.exchangeRates[1].amountUi, "36,730.0000")
            cancelAndIgnoreRemainingEvents()
        }
    }

}