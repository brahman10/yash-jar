package com.jar.app.feature_kyc.shared.ui.alternate_doc.choose_doc

import com.jar.app.feature_kyc.shared.api.use_case.PostKycOcrRequestUseCase
import com.jar.app.feature_kyc.shared.domain.model.KycOcrResponse
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class UploadKycDocViewModel constructor(
    private val postKycOcrRequestUseCase: PostKycOcrRequestUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _kycOcrRequestLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<KycOcrResponse?>>>()
    val kycOcrRequestLiveData: CFlow<RestClientResult<ApiResponseWrapper<KycOcrResponse?>>>
        get() = _kycOcrRequestLiveData.toCommonFlow()

    private var job: Job? = null

    fun postKycOcrRequest(docType: String, byteArray: ByteArray, isKyc:Boolean = false) {
        job?.cancel()
        job = viewModelScope.launch {
            postKycOcrRequestUseCase.postKycOcrRequest(docType, byteArray,isKyc)
                .collect {
                    _kycOcrRequestLiveData.emit(it)
                }
        }
    }

}