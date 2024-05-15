package com.jar.app.feature_lending_kyc.shared.ui.aadhaar.digilocker

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerRedirectionUrlData
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerScreenContent
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerVerificationStatus
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerRedirectionUrlUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerScreenContentUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerVerificationStatusUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChooseKYCOptionViewModel (
    private val digiLockerScreenContentUseCase: FetchDigiLockerScreenContentUseCase,
    private val digiLockerRedirectionUrlUseCase: FetchDigiLockerRedirectionUrlUseCase,
    private val digiLockerVerificationStatusUseCase: FetchDigiLockerVerificationStatusUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _screenData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<DigiLockerScreenContent?>>>()
    val screenData: CFlow<RestClientResult<ApiResponseWrapper<DigiLockerScreenContent?>>>
        get() = _screenData.toCommonFlow()

    private val _digiLockerRedirectionUrl =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<DigiLockerRedirectionUrlData?>>>()
    val digiLockerRedirectionUrl: CFlow<RestClientResult<ApiResponseWrapper<DigiLockerRedirectionUrlData?>>>
        get() = _digiLockerRedirectionUrl.toCommonFlow()

    private val _digiLockerVerificationStatus =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<DigiLockerVerificationStatus?>>>()
    val digiLockerVerificationStatus : CFlow<RestClientResult<ApiResponseWrapper<DigiLockerVerificationStatus?>>>
        get() = _digiLockerVerificationStatus.toCommonFlow()

    private val _isDigiLockerEnabled = MutableStateFlow(true)
    val isDigiLockerEnabled: CStateFlow<Boolean>
        get() = _isDigiLockerEnabled.toCommonStateFlow()

    private val _isManualKycEnabled = MutableStateFlow(true)
    val isManualKycEnabled: CStateFlow<Boolean>
        get() = _isManualKycEnabled.toCommonStateFlow()

    private val _isConsentAvailable = MutableStateFlow(true)
    val isConsentAvailable: CStateFlow<Boolean>
        get() = _isConsentAvailable.toCommonStateFlow()

    fun fetchContent(applicationId: String, kycFeatureFlowType: KycFeatureFlowType) {
        viewModelScope.launch {
            digiLockerScreenContentUseCase.fetchDigiLockerScreenContent(applicationId, kycFeatureFlowType).collect {
                _screenData.emit(it)
            }

        }

    }

    fun updateConsentStatus(isConsentAvailable: Boolean) {
        _isConsentAvailable.value = isConsentAvailable
    }

    fun updateDigiLockerStatus(digiLockerStatus: Boolean) {

        _isDigiLockerEnabled.value = digiLockerStatus


    }

    fun updateManualKycStatus(manualKycStatus: Boolean) {
        _isManualKycEnabled.value = manualKycStatus
    }

    fun fetchRedirectionUrl(
        kycFeatureFlowType: KycFeatureFlowType,
        shouldEnablePinless: Boolean = false
    ) {
        viewModelScope.launch {
            digiLockerRedirectionUrlUseCase.fetchDigiLockerRedirectionUrl(
                kycFeatureFlowType,
                shouldEnablePinless
            ).collect {
                _digiLockerRedirectionUrl.emit(it)
            }
        }
    }

    fun fetchDigiLockerVerificationStatus(
        kycFeatureFlowType: KycFeatureFlowType,
        shouldEnablePinless: Boolean = false
    ) {
        viewModelScope.launch {
            digiLockerVerificationStatusUseCase.fetchDigiLockerVerificationStatus(
                kycFeatureFlowType,
                shouldEnablePinless
            ).collect {
                _digiLockerVerificationStatus.emit(it)
            }
        }
    }
}