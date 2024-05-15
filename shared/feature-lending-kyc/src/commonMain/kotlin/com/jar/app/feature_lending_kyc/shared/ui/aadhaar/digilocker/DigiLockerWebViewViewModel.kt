package com.jar.app.feature_lending_kyc.shared.ui.aadhaar.digilocker

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerVerificationStatus
import com.jar.app.feature_lending_kyc.shared.domain.model.DigilockerRedirectionData
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerVerificationStatusUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.UpdateDigiLockerRedirectionDataUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DigiLockerWebViewViewModel(
    private val digiLockerVerificationStatusUseCase: FetchDigiLockerVerificationStatusUseCase,
    private val updateDigiLockerRedirectionDataUseCase: UpdateDigiLockerRedirectionDataUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _digiLockerVerificationStatus =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<DigiLockerVerificationStatus?>>>()
    val digiLockerVerificationStatus = _digiLockerVerificationStatus.asSharedFlow()

    fun getDigiLockerVerificationStatus(kycFeatureFlowType: KycFeatureFlowType,shouldEnablePinless: Boolean = false) {
        viewModelScope.launch {
            digiLockerVerificationStatusUseCase.fetchDigiLockerVerificationStatus(kycFeatureFlowType,shouldEnablePinless).collect {
                _digiLockerVerificationStatus.emit(it)
            }
        }

    }

    fun updateRedirectData(kycFeatureFlowType: KycFeatureFlowType, state: String, code: String) {
        viewModelScope.launch {
            updateDigiLockerRedirectionDataUseCase.updateDigiLockerRedirection(
                kycFeatureFlowType,
                DigilockerRedirectionData(state = state, code = code)
            ).collect {

            }
        }
    }
}