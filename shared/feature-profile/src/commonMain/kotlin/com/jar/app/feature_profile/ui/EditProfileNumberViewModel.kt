package com.jar.app.feature_profile.ui

import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.feature_profile.domain.use_case.FetchDashboardStaticContentUseCase
import com.jar.app.feature_profile.domain.use_case.RequestOtpUseCase
import com.jar.app.feature_user_api.domain.model.DeviceDetails
import com.jar.app.feature_user_api.domain.model.OTPLoginRequest
import com.jar.app.feature_user_api.domain.model.PhoneNumberWithCountryCode
import com.jar.app.feature_user_api.domain.model.RequestOtpData
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase
import com.jar.app.feature_user_api.domain.use_case.GetUserSavedAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserPhoneNumberUseCase
import com.jar.app.feature_user_api.domain.use_case.VerifyNumberUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.util.Serializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper as LibraryApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult as LibraryRestClientResult

class EditProfileNumberViewModel(
    private val updatePhoneNumberUseCase: UpdateUserPhoneNumberUseCase,
    private val verifyNumberUseCase: VerifyNumberUseCase,
    private val requestOtpUseCase: RequestOtpUseCase,
    private val deviceUtils: DeviceUtils,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _updatePhoneNumberLiveData =
        MutableSharedFlow<LibraryRestClientResult<LibraryApiResponseWrapper<RequestOtpData>>>()
    val updatePhoneNumberLiveData: CFlow<LibraryRestClientResult<LibraryApiResponseWrapper<RequestOtpData>>>
        get() = _updatePhoneNumberLiveData.toCommonFlow()

    private val _verifyNumberLiveData =
        MutableSharedFlow<LibraryRestClientResult<LibraryApiResponseWrapper<String>>>()
    val verifyNumberLiveData: CFlow<LibraryRestClientResult<LibraryApiResponseWrapper<String>>>
        get() = _verifyNumberLiveData.toCommonFlow()

    private val _requestOtpLiveData =
        MutableSharedFlow<LibraryRestClientResult<LibraryApiResponseWrapper<RequestOtpData?>>>()
    val requestOtpLiveData: CFlow<LibraryRestClientResult<LibraryApiResponseWrapper<RequestOtpData?>>>
        get() = _requestOtpLiveData.toCommonFlow()

    fun updatePhoneNumber(phoneNumberWithCountryCode: PhoneNumberWithCountryCode) {
        viewModelScope.launch {
            updatePhoneNumberUseCase.updateUserPhoneNumber(phoneNumberWithCountryCode).collect {
                _updatePhoneNumberLiveData.emit(it)
            }
        }
    }

    fun verifyNumber(
        phoneNumber: String,
        countryCode: String,
        otp: String,
        reqId: String
    ) {
        viewModelScope.launch {
            val otpLoginRequest = OTPLoginRequest(
                phoneNumber,
                countryCode,
                otp,
                reqId,
                DeviceDetails(
                    advertisingId = deviceUtils.getAdvertisingId(),
                    deviceId = deviceUtils.getDeviceId(),
                    os = deviceUtils.getOsName()
                )
            )
            verifyNumberUseCase.verifyPhoneNumber(otpLoginRequest).collect {
                _verifyNumberLiveData.emit(it)
            }
        }
    }

    fun requestOtp(phoneNumber: String, countryCode: String) {
        viewModelScope.launch {
            requestOtpUseCase.requestOtp(phoneNumber, countryCode).collect {
                _requestOtpLiveData.emit(it)
            }
        }
    }

    fun requestOtpViaCall(phoneNumber: String, countryCode: String) {
        viewModelScope.launch {
            requestOtpUseCase.requestOtpViaCall(phoneNumber, countryCode).collect {
                _requestOtpLiveData.emit(it)
            }
        }
    }

}
