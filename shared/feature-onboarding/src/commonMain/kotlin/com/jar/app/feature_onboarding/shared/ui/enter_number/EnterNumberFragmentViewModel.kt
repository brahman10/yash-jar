package com.jar.app.feature_onboarding.shared.ui.enter_number

import com.jar.app.core_base.domain.model.UserResponseData
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_onboarding.shared.domain.model.Language
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOtlUserInfoUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSupportedLanguagesUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.TruecallerLoginUseCase
import com.jar.app.feature_user_api.domain.model.DeviceDetails
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.orFalse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class EnterNumberFragmentViewModel constructor(
    private val deviceUtils: DeviceUtils,
    private val truecallerLoginUseCase: TruecallerLoginUseCase,
    private val fetchSupportedLanguagesUseCase: FetchSupportedLanguagesUseCase,
    private val fetchOtlUserInfoUseCase: FetchOtlUserInfoUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _truecallerLoginFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserResponseData>>>(RestClientResult.none())
    val truecallerLoginFlow: CFlow<RestClientResult<ApiResponseWrapper<UserResponseData>>>
        get() = _truecallerLoginFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    private val _languageFlow = MutableStateFlow<Language?>(null)
    val languageFlow: CFlow<Language?>
        get() = _languageFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    var payload: String = ""
    var signature: String = ""
    var signatureAlgorithm: String = ""
    var trueCallerAuthDone = false

    fun isNumberValid(number: String?): Boolean {
        return number.isNullOrBlank().not() &&
                number?.all { char -> char.isDigit() }.orFalse() &&
                number?.length.orZero() == 10
    }

    fun truecallerLoginSuccessful(
        payload: String,
        signature: String,
        signatureAlgorithm: String,
        logoutFromDevices: Boolean
    ) {
        this.payload = payload
        this.signature = signature
        this.signatureAlgorithm = signatureAlgorithm

        viewModelScope.launch {
            truecallerLoginUseCase.loginViaTruecaller(
                com.jar.app.feature_onboarding.shared.domain.model.TruecallerLoginRequest(
                    payload,
                    signature,
                    signatureAlgorithm,
                    DeviceDetails(
                        advertisingId = deviceUtils.getAdvertisingId(),
                        deviceId = deviceUtils.getDeviceId(),
                        os = deviceUtils.getOsName()
                    ),
                    logoutFromOtherDevices = logoutFromDevices
                )
            ).collect {
                _truecallerLoginFlow.emit(it)
            }
        }
    }

    fun getLanguageForCode(code: String) {
        viewModelScope.launch {
            fetchSupportedLanguagesUseCase.fetchSupportedLanguages()
                .collect(
                    onSuccess = {
                        val language = it.languages.find { it.code == code }
                        _languageFlow.emit(language)
                    }
                )
        }
    }

    fun fireEvent(
        eventName: String,
        eventParamsMap: MutableMap<String, Any>?
    ) {
        eventParamsMap?.let {
            analyticsApi.postEvent(eventName, it)
        } ?: kotlin.run {
            analyticsApi.postEvent(eventName)
        }
    }
}