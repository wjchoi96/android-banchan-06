package com.woowahan.banchan.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.woowahan.domain.model.NoConnectivityIOException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor(
    private val context: Context
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return when(isNetworkAvailable(context)){
            true -> {
                chain.request().newBuilder().run {
                    chain.proceed(build())
                }
            }
            else -> throw NoConnectivityIOException()
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val nw = connectivityManager.activeNetwork
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}