package com.jar.app.core_network.di.module

import android.content.Context
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_network.BuildConfig
import com.jar.app.core_network.CoreNetworkBuildKonfig
import com.jar.app.core_network.di.CommonKtorNetworkModule
import com.jar.app.core_network.di.qualifier.*
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
import com.jar.internal.library.jar_core_network.impl.HttpEngineProviderAndroid
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class KtorNetworkModule {

    @Provides
    @Singleton
    internal fun provideCommonKtorNetworkModule(
        prefs: PrefsApi,
        retainedPrefs: RetainedPrefsApi,
        deviceUtils: DeviceUtils,
        httpEngineProviderAndroid: HttpEngineProviderAndroid,
    ): CommonKtorNetworkModule {
        return CommonKtorNetworkModule(
            shouldEnabledLogs = BuildConfig.DEBUG,
            isDebugBuild = BuildConfig.DEBUG,
            prefs = prefs,
            retainedPrefs = retainedPrefs,
            deviceUtils = deviceUtils,
            httpEngineProvider = httpEngineProviderAndroid
        )
    }

    @Provides
    @Singleton
    @AppNetworkApi
    internal fun provideAppNetworkApi(
        commonKtorNetworkModule: CommonKtorNetworkModule
    ): NetworkApi {
        return commonKtorNetworkModule.appNetworkApi
    }

    @Provides
    @Singleton
    @AuthNetworkApi
    internal fun provideAuthNetworkApi(
        commonKtorNetworkModule: CommonKtorNetworkModule
    ): NetworkApi {
        return commonKtorNetworkModule.authNetworkApi
    }

    @Provides
    @Singleton
    internal fun provideHttpEngine(
        @ApplicationContext context: Context
    ): HttpEngineProviderAndroid {
        return HttpEngineProviderAndroid(
            context = context,
            pathsToSkipInChuckerLog = {
                arrayOf(
                    "/v2/api/user/update/profilePic",
                    "/v1/api/kyc/ocr",
                    "/v1/api/kyc/faceMatch",
                    "/v2/api/kyc/selfieMatch",
                    "/v2/api/loan/realTime/uploadBankStatement"
                )
            },
            // We're using Ktor for api call which uses okhttp4 and firebase performance sdk
            // doesn't record the network call with okhttp4, so we're using an interceptor to record that call by ourself
            interceptor = listOf(FirebasePerformanceInterceptor())
        )
    }

    @Provides
    @Singleton
    @AppHttpClient
    internal fun provideAppHttpClient(commonKtorNetworkModule: CommonKtorNetworkModule): HttpClient {
        return commonKtorNetworkModule.appHttpClient
    }

    @Provides
    @Singleton
    @AuthHttpClient
    internal fun provideAuthHttpClient(commonKtorNetworkModule: CommonKtorNetworkModule): HttpClient {
        return commonKtorNetworkModule.authHttpClient
    }

    @Provides
    @Singleton
    internal fun provideAuthDataSource(commonKtorNetworkModule: CommonKtorNetworkModule): AuthDataSource {
        return commonKtorNetworkModule.authDataSource
    }

    @Provides
    @Singleton
    internal fun provideJson(commonKtorNetworkModule: CommonKtorNetworkModule): Json {
        return commonKtorNetworkModule.json
    }

    @Provides
    @Singleton
    internal fun provideSerializer(commonKtorNetworkModule: CommonKtorNetworkModule): Serializer {
        return commonKtorNetworkModule.serializer
    }
}