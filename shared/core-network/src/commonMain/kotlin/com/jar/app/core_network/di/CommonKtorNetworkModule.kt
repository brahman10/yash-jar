package com.jar.app.core_network.di

import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_network.CoreNetworkBuildKonfig
import com.jar.app.core_network.impl.AuthDataSource
import com.jar.app.core_network.util.NetworkConstants
import com.jar.app.core_network.util.checkErrorCode
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_preferences.api.RetainedPrefsApi
import com.jar.internal.library.jar_core_network.api.data.NetworkApi
import com.jar.internal.library.jar_core_network.api.model.BearerToken
import com.jar.internal.library.jar_core_network.api.model.HTTPS
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.impl.HttpEngineProvider
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
class CommonKtorNetworkModule(
    private val shouldEnabledLogs: Boolean,
    private val isDebugBuild: Boolean,
    private val prefs: PrefsApi,
    private val retainedPrefs: RetainedPrefsApi,
    private val deviceUtils: DeviceUtils,
    private val httpEngineProvider: HttpEngineProvider

) {

    val appNetworkApi: NetworkApi by lazy {
        NetworkApi.Builder()
            .setBaseUrl(baseUrl)
            .setLogTag(NetworkConstants.LOG_TAG)
            .shouldEnabledLogging(shouldEnabledLogs)
            .setProtocol(HTTPS)
            .setHttpEngineProvider(httpEngineProvider)
            .setDefaultRequestHeaders {
                val map = mutableMapOf<String, String>()

                map[NetworkConstants.DefaultRequestHeaders.AppVer] =
                    CoreNetworkBuildKonfig.VERSION_CODE.toString()

                val os = runBlocking { deviceUtils.getOsName() }
                map[NetworkConstants.DefaultRequestHeaders.OS] = os

                val languageCode = prefs.getCurrentLanguageCode()
                map[NetworkConstants.DefaultRequestHeaders.Accept_Language] = languageCode

                val deviceId = runBlocking { deviceUtils.getDeviceId() }
                map[NetworkConstants.DefaultRequestHeaders.DeviceId] = deviceId

                runBlocking {
                    map[NetworkConstants.DefaultRequestHeaders.User_Agent] =
                        "JarApp/${CoreNetworkBuildKonfig.VERSION_CODE} ${deviceUtils.getRuntimeName()}/${deviceUtils.getRuntimeVersion()} (Linux; U; Android ${deviceUtils.getOsName()}; ${deviceUtils.getManufacturer()} ${deviceUtils.getModel()} Build/${deviceUtils.getProduct()})"
                }
                map
            }.setFetchTokenListener {
                val accessToken = prefs.getAccessToken()
                val refreshToken = prefs.getRefreshToken()
                if (accessToken.isNullOrBlank().not() && refreshToken.isNullOrBlank().not()) {
                    BearerToken(
                        accessToken = accessToken!!, refreshToken = refreshToken!!
                    )
                } else null
            }.setRefreshTokenListener {
                val result = authDataSource.refreshToken(prefs.getRefreshToken().orEmpty())
                val data = result.data?.data
                if (result.status == RestClientResult.Status.SUCCESS && data != null) {
                    prefs.setRefreshToken(data.refreshToken)
                    prefs.setAccessToken(data.accessToken)
                    BearerToken(
                        accessToken = data.accessToken, refreshToken = data.refreshToken
                    )
                } else if (result.status == RestClientResult.Status.ERROR && checkErrorCode(result.errorCode)) {
                    val accessToken = prefs.getAccessToken()
                    val refreshToken = prefs.getRefreshToken()
                    BearerToken(
                        accessToken = accessToken!!, refreshToken = refreshToken!!
                    )
                } else {
                    prefs.setRefreshToken("")
                    prefs.setAccessToken("")
                    null
                }
            }.build()
    }


    val authNetworkApi: NetworkApi by lazy {
        NetworkApi.Builder()
            .setBaseUrl(baseUrl)
            .setLogTag(NetworkConstants.LOG_TAG)
            .shouldEnabledLogging(shouldEnabledLogs)
            .setHttpEngineProvider(httpEngineProvider)
            .setProtocol(HTTPS).setFetchTokenListener {
                val accessToken = prefs.getAccessToken()
                val refreshToken = prefs.getRefreshToken()
                if (accessToken.isNullOrBlank().not() && refreshToken.isNullOrBlank().not()) {
                    BearerToken(
                        accessToken = accessToken!!,
                        refreshToken = refreshToken!!
                    )
                } else null
            }.build()
    }

    val baseUrl: String by lazy {
        val url = if (isDebugBuild) {
            retainedPrefs.getApiBaseUrl().ifEmpty { CoreNetworkBuildKonfig.BASE_URL_KTOR }
        } else {
            CoreNetworkBuildKonfig.BASE_URL_KTOR
        }
        url
    }

    val appHttpClient: HttpClient by lazy {
        appNetworkApi.getHttpClient()
    }

    val authHttpClient: HttpClient by lazy {
        authNetworkApi.getHttpClient()
    }

    val authDataSource: AuthDataSource by lazy {
        AuthDataSource(authHttpClient)
    }

    val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    val serializer: Serializer by lazy {
        Serializer()
    }
}