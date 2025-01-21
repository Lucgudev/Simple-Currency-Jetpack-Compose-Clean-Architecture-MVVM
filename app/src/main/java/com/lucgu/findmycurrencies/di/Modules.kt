package com.lucgu.findmycurrencies.di

import android.content.Context
import androidx.room.Room
import com.lucgu.findmycurrencies.data.local.database.AppDatabase
import com.lucgu.findmycurrencies.data.local.source.ExchangeRateLocalDataSource
import com.lucgu.findmycurrencies.data.local.source.ExchangeRateLocalDataSourceImpl
import com.lucgu.findmycurrencies.data.local.source.SharedPreferencesHelper
import com.lucgu.findmycurrencies.data.local.source.SharedPreferencesHelperImpl
import com.lucgu.findmycurrencies.data.remote.services.ExchangeRatesApi
import com.lucgu.findmycurrencies.data.remote.source.ExchangeRateRemoteDataSource
import com.lucgu.findmycurrencies.data.remote.source.ExchangeRateRemoteDataSourceImpl
import com.lucgu.findmycurrencies.data.repository.ExchangeRateRepositoryImpl
import com.lucgu.findmycurrencies.domain.repositories.ExchangeRatesRepository
import com.lucgu.findmycurrencies.presentation.feature.home.HomeViewModel
import com.lucgu.findmycurrencies.utils.Constants
import com.lucgu.findmycurrencies.utils.ResourceProvider
import com.lucgu.findmycurrencies.utils.TimeProvider
import com.lucgu.findmycurrencies.utils.TimeProviderImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private val moshiModule = module {
    single { Moshi.Builder().build()}
}

private val resourcesModule = module {
    single { ResourceProvider(androidContext()) }
}

private val serviceModule = module {
    single { provideOkHttpClient(androidContext()) }
    single {
        provideCustomRetrofit(androidContext())
    }
    single { get<Retrofit>().create(ExchangeRatesApi::class.java) }
}

private fun provideOkHttpClient(context: Context): OkHttpClient {
    return OkHttpClient.Builder().writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS).build()
}

private fun provideCustomRetrofit(context: Context): Retrofit {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    return Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(Constants.BASE_URL)
        .client(provideOkHttpClient(context)).build()
}

private val repoModule = module {
    single<ExchangeRatesRepository> { ExchangeRateRepositoryImpl(get(), get(), get()) }
}

private val remoteDataSourceModule = module {
    single<ExchangeRateRemoteDataSource> { ExchangeRateRemoteDataSourceImpl(get()) }
}

private val localDataSourceModule = module {
    single<ExchangeRateLocalDataSource> { ExchangeRateLocalDataSourceImpl(get(), get(), get()) }
}


private val persistModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database.db").build()
    }
    single { get<AppDatabase>().exchangeRatesDao() }
    single<SharedPreferencesHelper> { SharedPreferencesHelperImpl(androidContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)) }
}

private val utilsModule = module {
    single<TimeProvider> { TimeProviderImpl() }
}

private val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
}

val allModule = listOf(
    moshiModule,
    resourcesModule,
    serviceModule,
    persistModule,
    remoteDataSourceModule,
    localDataSourceModule,
    repoModule,
    utilsModule,
    viewModelModule,
)