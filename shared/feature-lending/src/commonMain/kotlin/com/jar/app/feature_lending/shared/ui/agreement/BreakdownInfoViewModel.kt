package com.jar.app.feature_lending.shared.ui.agreement

import com.jar.app.core_base.util.countDownTimer
import com.jar.app.core_base.util.milliSecondsToCountDown
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BreakdownInfoViewModel constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private var timerJob: Job? = null

    private val _loanDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>(RestClientResult.none())
    val loanDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _loanDetailsFlow.toCommonStateFlow()

    private val _staticContentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>(
            RestClientResult.none()
        )
    val staticContentFlow: CStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>
        get() = _staticContentFlow.toCommonStateFlow()


    private val _resendOtpTimerFlow = MutableStateFlow<Long>(-1L)
    val resendOtpTimerFlow: CFlow<Long>
        get() = _resendOtpTimerFlow.toCommonFlow()

     fun startOtpResendTimer(timeInSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.countDownTimer(
            totalMillis = timeInSeconds * 1000L,
            onInterval = {
                _resendOtpTimerFlow.emit(it)
            },
            onFinished = {
                _resendOtpTimerFlow.emit(0L)
            }
        )
    }

    fun fetchLoanDetails(
        checkPoint: String,
        shouldPassCheckpoint: Boolean = false,
        loanId: String
    ) {
        viewModelScope.launch {
            val cp = if (shouldPassCheckpoint) checkPoint else null
            fetchLoanDetailsV2UseCase.getLoanDetails(loanId, cp).collect {
                _loanDetailsFlow.emit(it)
            }
        }
    }

    fun fetchStaticContent(contentType: String, loanId: String) {
        viewModelScope.launch {
            fetchStaticContentUseCase.fetchLendingStaticContent(loanId, contentType).collect {
                _staticContentFlow.emit(it)
            }
        }
    }

    fun milliSecondsToCountDown(remainingTimeInMillis: Int): String {
        return remainingTimeInMillis.milliSecondsToCountDown()
    }
}