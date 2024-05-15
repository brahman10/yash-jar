package com.jar.app.feature_lending.shared.ui.repayments.history

import com.jar.app.feature_lending.shared.domain.model.repayment.EmiTxnCommonData
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchEmiTxnHistoryUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
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

class RepaymentEmiScheduleViewModel constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val fetchEmiTxnHistoryUseCase: FetchEmiTxnHistoryUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _staticContentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>(
            RestClientResult.none()
        )
    val staticContentFlow: CStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>
        get() = _staticContentFlow.toCommonStateFlow()

    private val _emiListFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<List<EmiTxnCommonData>?>>>(
            RestClientResult.none()
        )
    val emiListFlow: CStateFlow<RestClientResult<ApiResponseWrapper<List<EmiTxnCommonData>?>>>
        get() = _emiListFlow.toCommonStateFlow()

    fun fetchStaticContent(loanId: String) {
        viewModelScope.launch {
            fetchStaticContentUseCase.fetchLendingStaticContent(
                loanId,
                LendingConstants.StaticContentType.REPAYMENT_EMI_SCREEN
            ).collect {
                _staticContentFlow.emit(it)
            }
        }
    }

    fun fetchEmiList(loanId: String) {
        viewModelScope.launch {
            fetchEmiTxnHistoryUseCase.getEmiTxnHistory(loanId, LendingConstants.TransactionType.EMI)
                .collect {
                    _emiListFlow.emit(it)
                }
        }
    }
}