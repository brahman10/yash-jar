package com.jar.app.feature_lending.shared.ui.realtime_flow.bottom_sheet

import com.jar.app.core_base.util.isSpecialCharacters
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.FetchExperianReportRequest
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RefreshCreditScoreBottomSheetUiState
import com.jar.app.feature_lending.shared.domain.ui_event.RefreshCreditScoreBottomSheetEvent
import com.jar.app.feature_lending.shared.domain.use_case.FetchExperianReportUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchRealTimeCreditDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jar_core_network.api.util.orFalse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RealTimeRefreshCreditScoreBottomSheetViewModel constructor(
    private val fetchRealTimeCreditDetailsUseCase: FetchRealTimeCreditDetailsUseCase,
    private val fetchExperianReportUseCase: FetchExperianReportUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _isRefreshingCreditScore = MutableSharedFlow<RestClientResult<Boolean>>()
    val isRefreshingCreditScore get() = _isRefreshingCreditScore.asSharedFlow()

    private val _uiState = MutableStateFlow(RefreshCreditScoreBottomSheetUiState())
    val uiState get() = _uiState.asStateFlow()


    fun uiEvent(event: RefreshCreditScoreBottomSheetEvent) {
        when (event) {
            is RefreshCreditScoreBottomSheetEvent.OnNameUpdate -> {
                if (event.name.length <= 1) {
                    if (event.isCheckCreditScore) {
                        analyticsApi.postEvent(
                            LendingEventKeyV2.CreditReport_CreditScoreScreenLaunched,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.check_credit_score_screen,
                                LendingEventKeyV2.action to LendingEventKeyV2.details_bs_name_entered
                            )
                        )
                    } else {
                        analyticsApi.postEvent(
                            LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.ENTER_DETAILS_BS,
                                LendingEventKeyV2.action to LendingEventKeyV2.NAME_TYPED,
                                LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to LendingEventKeyV2.RLENDING_CREDIT_SCORE
                            )
                        )
                    }
                }
                val patternName = Regex("[a-zA-z\\s]*")
                val name = event.name.replace(Regex("_"), "")
                if (name.matches(patternName)) {
                    _uiState.update {
                        it.copy(
                            name = event.name.replace("\\s+".toRegex(), " "),
                            isButtonEnabled = uiState.value.shouldEnableButton(
                                event.name.replace(
                                    "\\s+".toRegex(),
                                    " "
                                )
                            )
                        )
                    }
                }
            }

            is RefreshCreditScoreBottomSheetEvent.OnPanUpdate -> {
                if (event.panNumber.length <= 1) {
                    if (event.isCheckCreditScore) {
                        analyticsApi.postEvent(
                            LendingEventKeyV2.CreditReport_CreditScoreScreenLaunched,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.check_credit_score_screen,
                                LendingEventKeyV2.action to LendingEventKeyV2.details_bs_pan_entered
                            )
                        )
                    } else {
                        analyticsApi.postEvent(
                            LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.ENTER_DETAILS_BS,
                                LendingEventKeyV2.action to LendingEventKeyV2.PAN_TYPED,
                                LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to LendingEventKeyV2.RLENDING_CREDIT_SCORE
                            )
                        )
                    }

                }
                val maxChar = 10
                val patternName = Regex("[a-zA-z0-9]*")
                val panValue = event.panNumber.replace(Regex("_"), "")
                if (panValue.matches(patternName) && panValue.length <= maxChar) {
                    _uiState.update {
                        it.copy(
                            panNo = event.panNumber.trim(),
                            isButtonEnabled = uiState.value.shouldEnableButtonForPan(event.panNumber)
                        )
                    }
                    checkForValidPAN(event.panNumber)
                }

            }

            RefreshCreditScoreBottomSheetEvent.OnClickSubmitButtonInRefreshScoreBottomSheet -> {
                requestCreditFetchWithoutConsent()
            }
        }

    }

    private fun requestCreditFetchWithoutConsent() {
        viewModelScope.launch {
            fetchExperianReportUseCase.fetchExperianReportUseCase(
                FetchExperianReportRequest(
                    name = uiState.value.name,
                    phoneNumber = uiState.value.mobileNo,
                    panNo = uiState.value.panNo.ifEmpty { null }
                )
            ).collectUnwrapped(
                onLoading = {
                    _isRefreshingCreditScore.emit(
                        RestClientResult.loading()
                    )
                },
                onSuccess = {
                    _isRefreshingCreditScore.emit(
                        RestClientResult.success(it.success.orFalse())
                    )
                },
                onError = { message, errorCode ->
                    _isRefreshingCreditScore.emit(
                        RestClientResult.error(message = message, errorCode = errorCode)
                    )
                }
            )
        }
    }

    init {
        viewModelScope.launch {
            fetchRealTimeCreditDetailsUseCase.fetchRealTimeCreditDetails().collect(
                onSuccess = { data ->
                    data?.let { response ->
                        _uiState.update {
                            it.copy(
                                mobileNo = response.phoneNo,
                                panNo = response.panNumber.orEmpty(),
                                isPanReadOnly = !response.panEditable,
                                experianConsentRequired = response.experianConsentRequired
                            )
                        }
                    }
                },
                onError = { _, _ ->

                }
            )
        }
    }

    private fun checkForValidPAN(
        value: String
    ) {
        var errorMessage: StringResource =
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_blank
        if (value.isNullOrEmpty()) {
            _uiState.update { it.copy(showPanError = false) }
        } else if (value.isSpecialCharacters()) {
            errorMessage =
                com.jar.app.feature_lending.shared.MR.strings.feature_lending_pan_cannot_have_special_character
            _uiState.update {
                it.copy(
                    panErrorMessageId = errorMessage,
                    isPanReadOnly = true
                )
            }
        } else if (value.length >= 4 && value[3].equals(Char(80/*ASCII for Char P*/), true).not()) {
            errorMessage =
                com.jar.app.feature_lending.shared.MR.strings.feature_lending_incorrect_pan_format
            _uiState.update {
                it.copy(
                    panErrorMessageId = errorMessage,
                    showPanError = true
                )
            }
        } else if (getRawText(value).length != 10) {
            errorMessage =
                com.jar.app.feature_lending.shared.MR.strings.feature_lending_pan_number_should_be_10_char
            _uiState.update {
                it.copy(
                    panErrorMessageId = errorMessage,
                    showPanError = true
                )
            }
        } else {
            _uiState.update { it.copy(showPanError = false) }
        }

        if (value.isNotEmpty()) {
            analyticsApi.postEvent(
                LendingEventKeyV2.RLENDING_ERRORSCREENEVENT,
                mapOf(
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.ENTER_DETAILS_BS,
                    LendingEventKeyV2.text_displayed to errorMessage
                )
            )
        }
    }

    private fun getRawText(value: String) = value.replace(" ", "")


}