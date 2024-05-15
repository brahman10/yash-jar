package com.jar.app.feature_lending.shared.ui.personal_details.employment

import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class LendingEmploymentDetailsViewModel constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _updateEmploymentDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val updateEmploymentDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _updateEmploymentDetailsFlow.toCommonFlow()

    fun updateEmploymentDetails(updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.EMPLOYMENT_DETAILS
            ).collect {
                _updateEmploymentDetailsFlow.emit(it)
            }
        }
    }
}