package com.lucgu.findmycurrencies.data.model

import androidx.compose.runtime.Stable

@Stable
data class APIError(val code: Int, val message: String)
