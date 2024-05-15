package com.jar.app.core_utils.data

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkFlow @Inject constructor(
    private val connectivityManager: Lazy<ConnectivityManager>,
    private val networkUtil: NetworkUtil,
    appScope: CoroutineScope
) {

    val networkStatus = callbackFlow {
        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                trySend(false)
            }

            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                trySend(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.get().registerNetworkCallback(request, networkStatusCallback)

        awaitClose {
            connectivityManager.get().unregisterNetworkCallback(networkStatusCallback)
        }
    }
        .onStart {
            emit(networkUtil.isOnline())
        }
        .shareIn(scope = appScope, SharingStarted.WhileSubscribed(5000), replay = 1)
}