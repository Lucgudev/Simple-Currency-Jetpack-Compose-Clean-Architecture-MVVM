package com.lucgu.findmycurrencies.data.remote.model

import com.lucgu.findmycurrencies.data.model.APIError

sealed class DataState<T> {
    class Success<T>(val data: T) : DataState<T>()
    class Loading<T> : DataState<T>()
    class Error<T>(val apiError: APIError?) : DataState<T>()
}