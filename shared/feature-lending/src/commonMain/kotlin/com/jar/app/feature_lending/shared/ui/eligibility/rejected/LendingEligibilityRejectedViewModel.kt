package com.jar.app.feature_lending.shared.ui.eligibility.rejected

import com.jar.app.feature_lending.shared.domain.use_case.AcknowledgeOneTimeCardUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
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

class LendingEligibilityRejectedViewModel constructor(
    private val acknowledgeOneTimeCardUseCase: AcknowledgeOneTimeCardUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _ackFlow = MutableStateFlow<RestClientResult<ApiResponseWrapper<Boolean?>>>(RestClientResult.none())
    val ackFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Boolean?>>>
        get() = _ackFlow.toCommonStateFlow()

    fun acknowledgeRejection() {
        viewModelScope.launch {
            acknowledgeOneTimeCardUseCase.acknowledgeOneTimeCard(LendingConstants.OneTimeCardType.BANK_VERIFICATION)
                .collect {
                    _ackFlow.emit(it)
                }
        }
    }
}