package com.lucgu.findmycurrencies.utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class CurrencyMapper {
    companion object {
        fun formatToCurrency(value: BigDecimal?): String {
            try {
                if (value == null) {
                    return "0"
                }
                val decimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
                decimalFormat.applyPattern("#,##0.0000")
                return decimalFormat.format(value)
            } catch (exc: Exception) {
                return "Error to format"
            }

        }
    }
}

