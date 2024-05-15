package com.jar.app.core_utils.data

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.jar.app.core_base.util.orFalse
import dagger.Lazy
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkUtil @Inject constructor(
    private val connectivityManager: Lazy<ConnectivityManager>
    ) {

    fun isOnline(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager.get().getNetworkCapabilities(connectivityManager.get().activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            return connectivityManager.get().activeNetworkInfo?.isConnected.orFalse()
        }
        return false
    }

    fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val enumIpAddress = en.nextElement().inetAddresses
                while (enumIpAddress.hasMoreElements()) {
                    val inetAddress = enumIpAddress.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }

}