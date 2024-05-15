package com.jar.app.feature_lending_kyc.shared.ui.faq

import com.jar.app.feature_lending_kyc.shared.domain.model.FaqDetails
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchLendingKycFaqListUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LendingKycFaqViewModel constructor(
    private val fetchLendingKycFaqListUseCase: FetchLendingKycFaqListUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _faqDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FaqDetails?>>>(RestClientResult.none())
    val faqDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<FaqDetails?>>>
        get() = _faqDetailsFlow.toCommonStateFlow()

    fun getLendingKycFaqList() {
        viewModelScope.launch {
            fetchLendingKycFaqListUseCase.fetchKycFaqList().collect {
                _faqDetailsFlow.emit(it)
            }
        }
    }
}