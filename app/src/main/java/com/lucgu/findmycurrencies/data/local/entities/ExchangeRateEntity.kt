package com.lucgu.findmycurrencies.data.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "exchange_rate")
data class ExchangeRateEntity(
    @PrimaryKey val currency: String,
    val rate: Double,
    val base: String,
    val currencyName: String,
    @Ignore val amount: BigDecimal = BigDecimal.ZERO,
    @Ignore val amountUi: String = "0"
) {
    constructor(currency: String, rate: Double, base: String, currencyName: String) :
            this(currency, rate, base, currencyName,BigDecimal.ZERO, "")
}
