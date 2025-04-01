package com.project.nasalibrary.utils

import retrofit2.Response

open class NetworkResponse<T>(private val response: Response<T>) {

    fun getNetworkResponse(): NetworkRequest<T> {
        return when {
            response.message().contains("timeout") -> NetworkRequest.Error("Timeout")
            response.code() == 401 -> NetworkRequest.Error("Unauthorized")
            response.isSuccessful -> NetworkRequest.Success(response.body()!!)
            else -> NetworkRequest.Error(response.message())
        }
    }
}