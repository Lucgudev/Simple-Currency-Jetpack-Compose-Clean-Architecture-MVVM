package com.lucgu.findmycurrencies

import android.app.Application
import com.lucgu.findmycurrencies.di.allModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level


class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            androidLogger(Level.DEBUG)
            modules(allModule)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}