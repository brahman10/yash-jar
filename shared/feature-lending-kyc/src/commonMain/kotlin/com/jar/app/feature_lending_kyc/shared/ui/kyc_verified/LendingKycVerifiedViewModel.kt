package com.jar.app.feature_lending_kyc.shared.ui.kyc_verified

import com.jar.app.core_base.domain.mapper.toKycProgressResponse
import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LendingKycVerifiedViewModel constructor(
    private val fetchKycProgressUseCase: FetchKycProgressUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _userLendingKycProgressFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>(
            RestClientResult.none()
        )
    val userLendingKycProgressFlow: CStateFlow<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>
        get() = _userLendingKycProgressFlow.toCommonStateFlow()

    private var kycProgressResponse: KycProgressResponse? = null


    fun fetchUserLendingKycProgress() {
        viewModelScope.launch {
            fetchKycProgressUseCase.fetchKycProgress()
                .mapToDTO {
                    it?.toKycProgressResponse()
                }
                .collect {
                    _userLendingKycProgressFlow.emit(it)
                }
        }
    }

    fun setKycResponse(kycProgressResponse: KycProgressResponse) {
        this.kycProgressResponse = kycProgressResponse
    }

    fun getKycProgressResponse() = kycProgressResponse
}