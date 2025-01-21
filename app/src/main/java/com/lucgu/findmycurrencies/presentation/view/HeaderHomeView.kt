package com.lucgu.findmycurrencies.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lucgu.findmycurrencies.R

@Composable
fun HeaderHomeView(originCurrency: String, originCurrencyName: String, onClickChangeOrigin: () -> Unit, onAmountChange: (String) -> Unit, originAmount: String) {

    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = originCurrency,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = originCurrencyName, style = MaterialTheme.typography.bodySmall
                    )
                }
                Button(
                    onClick = {
                        onClickChangeOrigin.invoke()
                    }
                ) {
                    Text(text = "Change", style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedTextField(
                value = originAmount,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = { newText ->
                    onAmountChange.invoke(newText)
                },
                placeholder = {
                    Text(text = stringResource(R.string.header_home_view_hint), color = Color.LightGray)
                },
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {  }
        }
    }
}