package com.jar.app.feature_kyc.shared.ui.upload_selfie_failed

import com.jar.app.feature_kyc.shared.api.use_case.PostFaceMatchRequestUseCase
import com.jar.app.feature_kyc.shared.domain.model.KYCStatusDetails
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class UploadSelfieFailedViewModel constructor(
    private val postFaceMatchRequestUseCase: PostFaceMatchRequestUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _postFaceMatchRequestLiveData = MutableSharedFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>()
    val postFaceMatchRequestLiveData: CFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>
        get() = _postFaceMatchRequestLiveData.toCommonFlow()

    private var job: Job? = null

    fun postFaceMatchRequest(docType: String, byteArray: ByteArray) {
        job?.cancel()
        job = viewModelScope.launch {
            postFaceMatchRequestUseCase.postFaceMatchRequest(
                docType, byteArray
            ).collect {
                _postFaceMatchRequestLiveData.emit(it)
            }
        }
    }

}