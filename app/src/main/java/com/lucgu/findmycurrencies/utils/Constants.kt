package com.lucgu.findmycurrencies.utils

class Constants {
    companion object {
        internal const val BASE_URL = "https://openexchangerates.org/api/"
        internal const val BASE_USD = "USD"
        internal const val SHARED_PREF_NAME = "findmycurrencies_app_pref"
        internal const val SHARED_PREF_LAST_UPDATE_TIMESTAMP = "last_update_timestamp"
    }
}

class ApiErrorConstants {
    companion object {
        internal const val GENERIC_ERROR = -1
    }
}