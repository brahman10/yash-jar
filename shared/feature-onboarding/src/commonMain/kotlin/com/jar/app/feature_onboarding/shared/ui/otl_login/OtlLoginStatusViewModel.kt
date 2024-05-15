package com.jar.app.feature_onboarding.shared.ui.otl_login

import com.jar.app.core_base.domain.model.UserResponseData
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_base.util.formatNumber
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOtlUserInfoUseCase
import com.jar.app.feature_user_api.domain.model.DeviceDetails
import com.jar.app.feature_user_api.domain.model.OTLLoginRequest
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class OtlLoginStatusViewModel constructor(
    private val fetchOtlUserInfoUseCase: FetchOtlUserInfoUseCase,
    private val deviceUtils: DeviceUtils,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _otlLoginFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserResponseData?>>>(RestClientResult.none())
    val otlLoginFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UserResponseData?>>>
        get() = _otlLoginFlow.toCommonStateFlow()

    fun fetchOTLUserInfo(
        hasExperianConsent: Boolean,
        phoneNumber: String,
        countryCode: String,
        correlationId: String,
        logoutFromOtherDevices: Boolean = false,
    ) {
        viewModelScope.launch {
            val otpLoginRequest = OTLLoginRequest(
                phoneNumber.formatNumber,
                countryCode,
                DeviceDetails(
                    advertisingId = deviceUtils.getAdvertisingId(),
                    deviceId = deviceUtils.getDeviceId(),
                    os = deviceUtils.getOsName()
                ),
                logoutFromOtherDevices = logoutFromOtherDevices,
                hasExperianConsent = hasExperianConsent,
                correlationId = correlationId
            )
            fetchOtlUserInfoUseCase.fetchOtlUserInfo(otpLoginRequest).collect {
                _otlLoginFlow.emit(it)
            }
        }
    }

}