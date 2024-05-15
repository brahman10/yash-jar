package com.jar.app.feature_lending.shared.ui.reason

import com.jar.app.feature_lending.shared.domain.model.ReasonData
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
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

class LoanReasonViewModel constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _reasonFlow = MutableStateFlow<List<ReasonData>>(emptyList())
    val reasonFlow: CStateFlow<List<ReasonData>>
        get() = _reasonFlow.toCommonStateFlow()

    private val _updateReasonFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>(
            RestClientResult.none()
        )
    val updateReasonFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _updateReasonFlow.toCommonStateFlow()

    var selectedReason: String? = null

    fun submitData(shouldNotify: Boolean, updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch(Dispatchers.Default) {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.READY_CASH_DETAILS
            ).collect {
                if (shouldNotify) {
                    _updateReasonFlow.emit(it)
                }
            }
        }
    }

    fun setReasonData(reasonList: List<ReasonData>) {
        viewModelScope.launch { _reasonFlow.emit(reasonList) }
    }

    fun updateReasonList(reasonId: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            _reasonFlow.value?.let { loanReasonChips ->
                val newList = loanReasonChips.map {
                    it.copy(isSelected = reasonId == it.id)
                }
                _reasonFlow.emit(newList)
            }
        }
    }
}