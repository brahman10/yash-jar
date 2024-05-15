package com.jar.app.feature_lending_kyc.shared.ui.selfie

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifySelfieUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import io.ktor.util.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class SelfieCheckViewModel constructor(
    private val verifySelfieUseCase: VerifySelfieUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _selfieUploadRequestFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val selfieUploadRequestFlow: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _selfieUploadRequestFlow.toCommonFlow()


    fun uploadSelfie(byteArray: ByteArray, kycFeatureFlowType: KycFeatureFlowType, loanApplicationId: String? = null) {
        viewModelScope.launch {
            verifySelfieUseCase.verifySelfie(byteArray, kycFeatureFlowType, loanApplicationId)
                .collect {
                _selfieUploadRequestFlow.emit(it)
            }
        }
    }
}