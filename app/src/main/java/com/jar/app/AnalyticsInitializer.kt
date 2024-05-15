package com.jar.app

import com.appsflyer.AppsFlyerLib
import com.clevertap.android.sdk.CleverTapAPI
import com.jar.app.core_analytics.*
import com.jar.app.feature.home.domain.usecase.FetchActiveAnalyticsListUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.internal.library.jarcoreanalytics.api.IAppAnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AnalyticsInitializer @Inject constructor(
    private val appScope: CoroutineScope,
    private val analyticsApi: AnalyticsApi,
    private val cleverTapAPI: CleverTapAPI,
    private val appsFlyerLib: AppsFlyerLib,
    private val fetchActiveAnalyticsListUseCase: FetchActiveAnalyticsListUseCase,
    private val appsFlyerAnalyticsService: dagger.Lazy<AppsFlyerAnalyticsService>,
    private val clevertapAnalyticsService: dagger.Lazy<ClevertapAnalyticsService>,
    private val firebaseAnalyticsService: dagger.Lazy<FirebaseAnalyticsService>,
    private val plotlineAnalyticsService: dagger.Lazy<PlotlineAnalyticsService>,
    private val userExperiorAnalyticsService: dagger.Lazy<UserExperiorAnalyticsService>,
) {

    fun initializeAnalyticsSdk() {
        appScope.launch(Dispatchers.IO) {
            fetchActiveAnalyticsListUseCase.fetchActiveAnalyticsList().collect(
                onSuccess = {
                    val enabledServices = mutableListOf<AnalyticsService>()
                    it.activeAnalyticsProvider?.forEach { serviceName ->
                        val service = AnalyticsService.values().find { it.name == serviceName }
                        if (service != null) {
                            enabledServices.add(service)
                        }
                    }
                    analyticsApi.init(getEnabledServiceImplFromEnum(enabledServices))
                }, onError = { errorMessage, errorCode ->
                    val defaultServices = listOf(
                        AnalyticsService.CLEVERTAP,
                        AnalyticsService.FIREBASE,
                        AnalyticsService.APPSFLYER,
                        AnalyticsService.PLOT_LINE
                    )
                    analyticsApi.init(getEnabledServiceImplFromEnum(defaultServices))
                })
        }
        updateDeviceId()
    }

    private fun getEnabledServiceImplFromEnum(enabledServicesEnum: List<AnalyticsService>): List<IAppAnalyticsService> {
        val enabledServicesImplList = mutableListOf<IAppAnalyticsService>()
        enabledServicesEnum.forEach { service ->
            when (service) {
                AnalyticsService.CLEVERTAP -> {
                    enabledServicesImplList.add(clevertapAnalyticsService.get())
                }
                AnalyticsService.FIREBASE -> {
                    enabledServicesImplList.add(firebaseAnalyticsService.get())
                }
                AnalyticsService.APPSFLYER -> {
                    enabledServicesImplList.add(appsFlyerAnalyticsService.get())
                }
                AnalyticsService.PLOT_LINE -> {
                    enabledServicesImplList.add(plotlineAnalyticsService.get())
                }
                AnalyticsService.USEREXPERIOR -> {
                    enabledServicesImplList.add(userExperiorAnalyticsService.get())
                }
            }
        }
        return enabledServicesImplList
    }

    private fun updateDeviceId() {
        appScope.launch(Dispatchers.IO) {
            try {
                val metaDataMap = mutableMapOf<String, Any>()
                delay(500)
                val cleverTapId = cleverTapAPI.getCleverTapID()
                metaDataMap["\$clevertap_id"] = cleverTapId.orEmpty()
                appsFlyerLib.setAdditionalData(metaDataMap)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}