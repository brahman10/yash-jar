package com.jar.app.feature_kyc.shared.ui.alternate_doc.choose_doc

import com.jar.app.feature_kyc.shared.domain.model.KycDocListResponse
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycDocumentsListUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChooseKycDocViewModel constructor(
    private val fetchKycDocumentsListUseCase: FetchKycDocumentsListUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _kycDocListFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<KycDocListResponse?>>>(RestClientResult.none())
    val kycDocListFlow: CStateFlow<RestClientResult<ApiResponseWrapper<KycDocListResponse?>>>
        get() = _kycDocListFlow.toCommonStateFlow()

    private var job: Job? = null

    init {
    }
    fun fetchData(){
        fetchKycDocumentsList()
    }


    private fun fetchKycDocumentsList() {
        job?.cancel()
        job = viewModelScope.launch {
            fetchKycDocumentsListUseCase.fetchKycDocumentsList().collect {
                _kycDocListFlow.emit(it)
            }
        }
    }

}