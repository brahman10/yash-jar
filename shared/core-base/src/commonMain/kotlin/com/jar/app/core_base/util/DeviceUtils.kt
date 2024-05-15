package com.jar.app.core_base.util

expect class DeviceUtils {

    suspend fun getDeviceId(): String

    suspend fun getAdvertisingId(): String?

    suspend fun getOsName(): String

    fun getManufacturer(): String       //Ex : Google

    fun getProduct(): String            //Ex : bluejay

    fun getModel(): String              //Ex : Pixel 6a

    suspend fun getRuntimeName(): String              //Ex : Dalvik

    suspend fun getRuntimeVersion(): String              //Ex : 1.0
}