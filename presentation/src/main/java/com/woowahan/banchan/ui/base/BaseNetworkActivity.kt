package com.woowahan.banchan.ui.base

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.view.View
import androidx.databinding.ViewDataBinding
import com.woowahan.banchan.R
import com.woowahan.banchan.extension.showTopSnackBar
import timber.log.Timber


abstract class BaseNetworkActivity<T: ViewDataBinding>: BaseActivity<T>(){

    abstract val snackBarView: View
    protected var networkState: Boolean = true

    private val connectivityManager: ConnectivityManager by lazy {
        getSystemService(ConnectivityManager::class.java)
    }

    private val networkCallback = object: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Timber.d("networkCallback => onAvailable")
            if(!networkState){
                networkState = true
                showNetworkSnackBar(networkState)
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Timber.d("networkCallback => onLost")
            if(networkState){
                networkState = false
                showNetworkSnackBar(networkState)
            }
        }
    }

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    override fun onStart() {
        super.onStart()
        networkState = isNetworkAvailable()
        showNetworkSnackBar(networkState)
        Timber.d("networkCallback => $networkState")

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            }
            else -> {
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun showNetworkSnackBar(networkConnect: Boolean){
        when(networkConnect){
            true -> showTopSnackBar(getString(R.string.network_available_state), snackBarView)
            else -> showTopSnackBar(getString(R.string.network_lost_state), snackBarView)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val nw = connectivityManager.activeNetwork
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}