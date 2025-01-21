package com.lucgu.findmycurrencies.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lucgu.findmycurrencies.R
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalChangeCurrency(data: List<ExchangeRateEntity>, onDismiss: (currencyPick: String) -> Unit = {}) {
    ModalBottomSheet(
        containerColor = Color.White,
        onDismissRequest = {
            onDismiss.invoke("")
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.modal_change_currency_title),
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            style = MaterialTheme.typography.titleMedium)

        LazyVerticalGrid (
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            horizontalArrangement = Arrangement.Center,
        ) {
            items(data) { data ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                        .clickable {
                            onDismiss.invoke(data.currency)
                        }

                ) {
                    Text(
                        text = data.currency + " - " + data.currencyName,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}