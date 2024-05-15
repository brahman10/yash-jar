package com.jar.app.feature_lending_kyc.shared.ui.aadhaar.ckyc

import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaar
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaarRequest
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchKycAadhaarDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SearchCkycAadhaarDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CkycFetchViewModel constructor(
    private val searchCkycAadhaarDetailsUseCase: SearchCkycAadhaarDetailsUseCase,
    private val fetchAadhaarDetailsUseCase: FetchKycAadhaarDetailsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _ckycRecordSearchFlow = MutableStateFlow<Boolean>(false)
    val ckycRecordSearchFlow: CStateFlow<Boolean>
        get() = _ckycRecordSearchFlow.toCommonStateFlow()

    private val _aadharInCkycFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<KycAadhaar?>>>(RestClientResult.none())
    val aadharInCkycFlow: CStateFlow<RestClientResult<ApiResponseWrapper<KycAadhaar?>>>
        get() = _aadharInCkycFlow.toCommonStateFlow()

    fun searchKycRecord(kycAadhaarRequest: KycAadhaarRequest) {
        viewModelScope.launch {
            searchCkycAadhaarDetailsUseCase.searchCKycAadhaarDetails(kycAadhaarRequest)
                .collectUnwrapped(
                    onSuccess = {
                        _ckycRecordSearchFlow.emit(it.success)
                    },
                    onError = { _, _ ->
                        _ckycRecordSearchFlow.emit(false)
                    },
                    onLoading = {},
                )
        }
    }

    fun searchAadharInCkyc() {
        viewModelScope.launch {
            fetchAadhaarDetailsUseCase.fetchKycAadhaarDetails().collect {
                _aadharInCkycFlow.emit(it)
            }
        }
    }
}