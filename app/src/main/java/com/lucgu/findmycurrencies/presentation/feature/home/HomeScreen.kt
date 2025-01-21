package com.lucgu.findmycurrencies.presentation.feature.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucgu.findmycurrencies.R
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity
import com.lucgu.findmycurrencies.presentation.view.HeaderHomeView
import com.lucgu.findmycurrencies.presentation.view.HomeErrorView
import com.lucgu.findmycurrencies.presentation.view.ListCurrency
import com.lucgu.findmycurrencies.presentation.view.ModalChangeCurrency
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {

    val viewState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.getExchangeRates()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "", color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = Color.White),
            ) {
                Content(
                    isLoading = viewState.isLoading,
                    data = viewState.exchangeRates,
                    originCurrency = viewState.originCurrency,
                    onAmountChange = { originAmount ->
                        viewModel.convertCurrency(originAmount)
                    },
                    originAmount = viewState.originValueUi,
                    originCurrencyName = viewState.originCurrencyName,
                    onClickChangeOrigin = {
                        viewModel.clickChangeOrigin()
                    },
                    showCurrencyPickDialog = viewState.showCurrencyPickDialog,
                    onDismiss = {
                        viewModel.onDismissCurrencyPickDialog(it)
                    },
                    onClickRefresh = {
                        viewModel.getExchangeRates()
                    },
                    errorMessage = viewState.errorMessage
                )
            }
        }
    )
}

@Composable
private fun Content(
    isLoading: Boolean,
    data: List<ExchangeRateEntity>,
    originCurrency: String,
    originCurrencyName: String,
    onAmountChange: (String) -> Unit,
    onClickChangeOrigin: () -> Unit,
    originAmount: String = "0.0",
    showCurrencyPickDialog: Boolean = false,
    onDismiss: (currencyPick: String) -> Unit = {},
    onClickRefresh: () -> Unit = {},
    errorMessage: String,
) {

    if(showCurrencyPickDialog) {
        ModalChangeCurrency(data = data, onDismiss = {
            onDismiss.invoke(it)
        })
    }

    Text(
        text = stringResource(R.string.home_title_screen),
        color = Color.Black,
        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
        style = MaterialTheme.typography.titleLarge
    )

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color.Red)
        }
    } else if (data.isNotEmpty()) {
        HeaderHomeView(
            originCurrency = originCurrency,
            originCurrencyName = originCurrencyName,
            onClickChangeOrigin = onClickChangeOrigin,
            onAmountChange = onAmountChange,
            originAmount = originAmount)
        ListCurrency(
            data = data,
            originCurrency = originCurrency)
    } else if(errorMessage.isNotEmpty()){
        HomeErrorView(onClickRefresh = onClickRefresh, errorMessage = errorMessage)
    }
}