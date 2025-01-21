package com.lucgu.findmycurrencies.data.remote.source

import com.lucgu.findmycurrencies.data.model.APIError
import com.lucgu.findmycurrencies.data.remote.model.DataState
import com.lucgu.findmycurrencies.utils.ApiErrorConstants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import retrofit2.Response

open class BaseRemoteDataSource {

    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Flow<DataState<T>> {
        return flow<DataState<T>> {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) emit(DataState.Success(body))
                else {
                    val errorBody = response.errorBody()?.string()
                    val apiError: APIError? = moshi.adapter(APIError::class.java).fromJson(errorBody)
                    emit(DataState.Error(apiError))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val apiError: APIError? = moshi.adapter(APIError::class.java).fromJson(errorBody)
                emit(DataState.Error(apiError))
            }

        }
            .catch {
                emit(DataState.Error(APIError(ApiErrorConstants.GENERIC_ERROR, it.message ?: it.toString())))
            }
            .onStart { emit(DataState.Loading()) }
            .flowOn(Dispatchers.IO)
    }
}