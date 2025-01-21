package com.lucgu.findmycurrencies.utils

class TimeProviderImpl : TimeProvider {
    override fun getCurrentTimeSecond(): Long {
        return System.currentTimeMillis()/1000
    }
}