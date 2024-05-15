package com.jar.app.feature_lending.shared.ui.eligibility.loading


import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LendingEligibilityLoadingViewModel constructor(
    private val fetchLendingV2PreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _preApprovedFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>(RestClientResult.none())
    val preApprovedFlow: CStateFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>
        get() = _preApprovedFlow.toCommonStateFlow()

    fun fetchLendingEligibility() {
        viewModelScope.launch {
            fetchLendingV2PreApprovedDataUseCase.fetchPreApprovedData().collect {
                _preApprovedFlow.emit(it)
            }
        }
    }
}