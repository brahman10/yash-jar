package com.jar.app.feature_lending_kyc.shared.ui.faq.details

import com.jar.app.feature_lending_kyc.shared.domain.model.FaqTypeDetails
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchLendingKycFaqDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LendingKycFaqDetailsViewModel constructor(
    private val fetchLendingKycFaqDetailsUseCase: FetchLendingKycFaqDetailsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _faqDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FaqTypeDetails?>>>(RestClientResult.none())
    val faqDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<FaqTypeDetails?>>>
        get() = _faqDetailsFlow.toCommonStateFlow()

    fun getFaqTypeDetails(param: String) {
        viewModelScope.launch {
            fetchLendingKycFaqDetailsUseCase.fetchLendingKycFaqDetails(param).collect {
                _faqDetailsFlow.emit(it)
            }
        }
    }
}