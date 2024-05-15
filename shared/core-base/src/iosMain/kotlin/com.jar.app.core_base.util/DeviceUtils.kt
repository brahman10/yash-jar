package com.jar.app.core_base.util

actual class DeviceUtils {
    actual suspend fun getDeviceId(): String {
        return ""
    }

    actual suspend fun getAdvertisingId(): String? {
        return ""
    }

    actual suspend fun getOsName(): String {
        return "ios"
    }

    actual fun getManufacturer(): String {
        return ""
    }

    actual fun getProduct(): String {
        return ""
    }

    actual fun getModel(): String {
        return ""
    }

    actual suspend fun getRuntimeName(): String {
        return ""
    }

    actual suspend fun getRuntimeVersion(): String {
        return ""
    }
}