package com.jar.app.core_network.di.module

import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_network.di.CommonKtorNetworkModule
import com.jar.app.core_network.impl.AuthDataSource
import com.jar.app.core_preferences.di.CommonPreferencesModule
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.impl.HttpEngineProviderIOS
import kotlinx.serialization.json.Json

class KtorNetworkModule(
    shouldEnableLogging: Boolean
) {

    private val preferencesModule by lazy {
        CommonPreferencesModule()
    }

    private val prefs by lazy {
        preferencesModule.prefApi
    }

    private val retainedPrefs by lazy {
        preferencesModule.retainedPrefApi
    }

    private val commonKtorNetworkModule by lazy {
        CommonKtorNetworkModule(
            shouldEnabledLogs = shouldEnableLogging,
            isDebugBuild = false,
            prefs = prefs,
            retainedPrefs = retainedPrefs,
            deviceUtils = deviceUtils,
            httpEngineProvider = httpEngineProviderIOS
        )
    }

    private val deviceUtils by lazy {
        DeviceUtils()
    }

    private val httpEngineProviderIOS by lazy {
        HttpEngineProviderIOS()
    }

    private val appNetworkApi by lazy {
        commonKtorNetworkModule.appNetworkApi
    }

    private val authNetworkApi by lazy {
        commonKtorNetworkModule.authNetworkApi
    }

    val appHttpClient by lazy {
        appNetworkApi.getHttpClient()
    }


    private val authHttpClient by lazy {
        authNetworkApi.getHttpClient()
    }


    private val authDataSource by lazy {
        AuthDataSource(authHttpClient)
    }

    val json by lazy {
        commonKtorNetworkModule.json
    }


    val serializer by lazy {
        commonKtorNetworkModule.serializer
    }
}