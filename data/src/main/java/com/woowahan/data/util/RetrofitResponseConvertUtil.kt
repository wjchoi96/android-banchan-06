package com.woowahan.data.util

import retrofit2.Response

object RetrofitResponseConvertUtil {

    fun <T>getData(response: Response<T>, statusCode: Int?): T {
        return getDataOrError(response, statusCode).body()!!
    }

    fun <T>getData(response: Response<T>): T {
        return getDataOrError(response).body()!!
    }

    private fun <T>getDataOrError(response: Response<T>): Response<T> {
        val errorMessage: String? = when {
            !response.isSuccessful -> response.errorBody()?.string() ?: response.message()
            response.body() == null -> "api response body is null"
            else -> null
        }
        return if(errorMessage == null){
            response
        }else{
            throw Throwable(errorMessage)
        }
    }

    private fun <T>getDataOrError(response: Response<T>, statusCode: Int?): Response<T> {
        val errorMessage: String? = when {
            !response.isSuccessful -> response.errorBody()?.string() ?: response.message()
            response.body() == null -> "api response body is null"
            statusCode == null || statusCode != 200 -> "api response status code is not 200[$statusCode]"
            else -> null
        }
        return if(errorMessage == null){
            response
        }else{
            throw Throwable(errorMessage)
        }
    }
}