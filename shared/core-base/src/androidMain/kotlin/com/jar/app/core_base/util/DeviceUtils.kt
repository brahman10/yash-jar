package com.jar.app.core_base.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class DeviceUtils constructor(
    private val context: Context
) {

    @SuppressLint("HardwareIds")
    actual suspend fun getDeviceId(): String {
        return Settings.Secure
            .getString(context.contentResolver, Settings.Secure.ANDROID_ID).orEmpty()
    }

    actual suspend fun getAdvertisingId(): String? {
        return withContext(Dispatchers.IO) {
            try {
                AdvertisingIdClient.getAdvertisingIdInfo(context).id.orEmpty()
            } catch (e: Exception) {
                null
            }
        }
    }

    actual suspend fun getOsName(): String {
        return withContext(Dispatchers.IO) {
            val fields = Build.VERSION_CODES::class.java.fields
            fields.firstOrNull { it.getInt(Build.VERSION_CODES::class) == Build.VERSION.SDK_INT }?.name
                ?: "UNKNOWN"
        }
    }

    actual fun getManufacturer() = Build.MANUFACTURER

    actual fun getProduct() = Build.PRODUCT

    actual fun getModel() = Build.MODEL

    actual suspend fun getRuntimeName(): String {
        val isArt = System.getProperty("java.vm.version")
            ?.firstOrNull()
            ?.digitToIntOrNull()
            .orZero() >= 2
        return if (isArt) "ART" else "Dalvik"
    }

    actual suspend fun getRuntimeVersion(): String {
        return System.getProperty("java.vm.version") ?: ""
    }
}