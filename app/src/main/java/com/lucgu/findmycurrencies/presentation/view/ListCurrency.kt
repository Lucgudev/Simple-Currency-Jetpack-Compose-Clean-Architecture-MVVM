package com.lucgu.findmycurrencies.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lucgu.findmycurrencies.data.local.entities.ExchangeRateEntity

@Composable
fun ListCurrency(data: List<ExchangeRateEntity>, originCurrency: String) {
    LazyVerticalGrid (
        columns = GridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        items(data) { data ->
            if(data.currency != originCurrency) {
                Card(
                    colors = CardDefaults.cardColors(Color.White),
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = data.currency, style = MaterialTheme.typography.bodyMedium)
                        Text(text = data.currencyName, style = MaterialTheme.typography.bodySmall)
                        Text(text = data.amountUi, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }

                }
            }
        }
    }
}