package com.woowahan.data.util

import com.woowahan.data.entity.ApiBodyIsNull
import com.woowahan.data.entity.ApiIsNotSuccessful
import com.woowahan.data.entity.ApiStatusCodeNotOk
import retrofit2.Response

object RetrofitResponseConvertUtil {

    fun <T>getData(response: Response<T>, statusCode: Int?): T {
        return getDataOrError(response, statusCode).body()!!
    }

    fun <T>getData(response: Response<T>): T {
        return getDataOrError(response).body()!!
    }

    private fun <T>getDataOrError(response: Response<T>): Response<T> {
        val error: Throwable? = when {
            !response.isSuccessful -> ApiIsNotSuccessful(response.message())
            response.body() == null -> ApiBodyIsNull()
            else -> null
        }
        return if(error == null){
            response
        }else{
            throw error
        }
    }

    private fun <T>getDataOrError(response: Response<T>, statusCode: Int?): Response<T> {
        val error: Throwable? = when {
            !response.isSuccessful -> ApiIsNotSuccessful(response.message())
            response.body() == null -> ApiBodyIsNull()
            statusCode == null || statusCode !in 200 until 300 -> ApiStatusCodeNotOk(statusCode)
            else -> null
        }
        return if(error == null){
            response
        }else{
            throw error
        }
    }
}