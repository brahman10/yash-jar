package com.jar.app.feature_lending.shared.ui.choose_amount.emi

import com.jar.app.feature_lending.shared.domain.model.temp.CreditLineScheme
import com.jar.app.feature_lending.shared.domain.model.v2.CreditLineSchemeResponse
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchEmiPlansUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
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
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SelectEmiPlanViewModel constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    private val fetchEmiPlansUseCase: FetchEmiPlansUseCase,
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _drawDownFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val drawDownFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _drawDownFlow.toCommonFlow()

    private val _schemeFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CreditLineSchemeResponse?>>>(
            RestClientResult.none()
        )
    val schemeFlow: CStateFlow<RestClientResult<ApiResponseWrapper<CreditLineSchemeResponse?>>>
        get() = _schemeFlow.toCommonStateFlow()

    private val _schemeList = MutableStateFlow<List<CreditLineScheme>?>(emptyList())
    val schemeList: CStateFlow<List<CreditLineScheme>?>
        get() = _schemeList.toCommonStateFlow()

    private val _loanDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>(RestClientResult.none())
    val loanDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _loanDetailsFlow.toCommonStateFlow()

    var currentlySelectedScheme: CreditLineScheme? = null
    var roi: Float = 0f

    private val _recommendedIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val recommendedIndex: StateFlow<Int>
        get() = _recommendedIndex

    fun selectScheme(scheme: CreditLineScheme) {
        currentlySelectedScheme = scheme
        viewModelScope.launch(Dispatchers.IO) {
            _schemeList.emit(
                _schemeList.value?.map {
                    it.copy(isSelected = it.tenure == scheme.tenure)
                }
            )
        }
    }

    fun fetchCreditLineSchemes(amount: Float) {
        viewModelScope.launch {
            fetchEmiPlansUseCase.fetchEmiPlans(amount).collect {
                _schemeFlow.emit(it)

                it.data?.data?.emiCards?.let { creditSchemes ->
                    _schemeList.emit(creditSchemes)
                }
            }
        }
    }

    fun updateDrawDown(updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.DRAW_DOWN
            ).collect {
                _drawDownFlow.emit(it)
            }
        }
    }

    fun fetchLoanDetails(checkPoint: String, loanId: String) {
        viewModelScope.launch {
            fetchLoanDetailsV2UseCase.getLoanDetails(loanId, checkPoint).collect {
                _loanDetailsFlow.emit(it)
            }
        }
    }

    fun findRecommendedSchemeIndex(schemes: List<CreditLineScheme>) {
        viewModelScope.launch {
            for ((index, scheme) in schemes.withIndex()) {
                if (scheme.isRecommended) {
                    _recommendedIndex.value = index
                    break
                }
            }

        }
    }

}