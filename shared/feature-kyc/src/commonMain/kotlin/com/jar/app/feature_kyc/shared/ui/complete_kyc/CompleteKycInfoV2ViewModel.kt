package com.jar.app.feature_kyc.shared.ui.complete_kyc

import com.jar.app.feature_kyc.shared.domain.model.KYCStatusDetails
import com.jar.app.feature_kyc.shared.domain.model.KycContext
import com.jar.app.feature_kyc.shared.domain.model.KycPanOcrResponse
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.PostPanOcrRequestUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class CompleteKycInfoV2ViewModel constructor(
    private val postPanOcrRequestUseCase: PostPanOcrRequestUseCase,
    private val postManualKycRequestUseCase: PostManualKycRequestUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _panOcrRequestLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<KycPanOcrResponse?>>>()
    val panOcrRequestLiveData: CFlow<RestClientResult<ApiResponseWrapper<KycPanOcrResponse?>>>
        get() = _panOcrRequestLiveData.toCommonFlow()

    private val _manualKycRequestLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>()
    val manualKycRequestLiveData: CFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>
        get() = _manualKycRequestLiveData.toCommonFlow()

    private var job: Job? = null

    fun postKycOcrRequest(byteArray: ByteArray) {
        job?.cancel()
        job = viewModelScope.launch {
            postPanOcrRequestUseCase.postKycOcrRequest(byteArray)
                .collect {
                    _panOcrRequestLiveData.emit(it)
                }
        }
    }

    fun postManualKycRequest(manualKycRequest: ManualKycRequest) {
        viewModelScope.launch {
            postManualKycRequestUseCase.postManualKycRequest(
                manualKycRequest = manualKycRequest,
                kycContext = KycContext.SELL_FLOW.name
            ).collect {
                _manualKycRequestLiveData.emit(it)
            }
        }
    }
}