package com.jar.app.core_network.util

internal object NetworkConstants {

    internal const val LOG_TAG = "#NETWORK_API_LOG_TAG#"

    internal object RefreshToken {
        const val REFRESH_TOKEN_ENDPOINT = "v1/api/refresh/get/accessToken"
    }

    internal object DefaultRequestHeaders {
        const val DeviceId = "DeviceId"
        const val IsPlayStore = "isPlayStore"
        const val AppVer = "appVer"
        const val Accept_Language = "Accept-Language"
        const val User_Agent = "User-Agent"
        const val OS = "os"
    }
}