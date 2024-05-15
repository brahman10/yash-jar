package com.jar.app.feature_lending.shared.ui.choose_amount.amount

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

class SelectLoanAmountViewModel constructor(
    private val fetchPreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _preApprovedDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>(RestClientResult.none())
    val preApprovedDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>
        get() = _preApprovedDataFlow.toCommonStateFlow()

    var preApprovedData: PreApprovedData? = null

    fun fetchPreApproved() {
        viewModelScope.launch {
            fetchPreApprovedDataUseCase.fetchPreApprovedData().collect {
                _preApprovedDataFlow.emit(it)
            }
        }
    }
}