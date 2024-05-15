package com.jar.app.feature_lending.shared.ui.realtime_flow.bank_details

import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.AddBankDetailsState
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeUiStep
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.SuccessApiResponse
import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.shared.domain.ui_event.AddBankDetailsEvent
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateBankDetailUseCase
import com.jar.app.feature_lending.shared.domain.use_case.ValidateIfscCodeUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddBankDetailsViewModel constructor(
    private val ifscCodeUseCase: ValidateIfscCodeUseCase,
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val updateBankDetailUseCase: UpdateBankDetailUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _updateBankDetails =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<SuccessApiResponse?>>>()
    val updateBankDetails get() = _updateBankDetails.asSharedFlow().toCommonFlow()


    private val _uiState = MutableStateFlow(AddBankDetailsState())
    val uiState get() = _uiState.asStateFlow().toCommonStateFlow()

    fun uiEvent(event: AddBankDetailsEvent) {
        when (event) {
            is AddBankDetailsEvent.updateAccountNo -> {
                if (event.accountNo.length <= 1) {
                    analyticsApi.postEvent(
                        LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                        mapOf(
                            LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_DETAILS_SCREEN,
                            LendingEventKeyV2.action to LendingEventKeyV2.ACCOUNT_NUMBER_TYPED
                        )
                    )
                }
                updateAccountNo(event.accountNo)
            }

            is AddBankDetailsEvent.onButtonClick -> {
                analyticsApi.postEvent(
                    LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                    mapOf(
                        LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_DETAILS_SCREEN,
                        LendingEventKeyV2.action to LendingEventKeyV2.BANK_DETAILS_CONTINUE_CLICKED
                    )
                )
                submitBankDetails()
            }

            is AddBankDetailsEvent.updateIfscCode -> {
                if (event.ifscCode.length <= 1) {
                    analyticsApi.postEvent(
                        LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                        mapOf(
                            LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_DETAILS_SCREEN,
                            LendingEventKeyV2.action to LendingEventKeyV2.IFSC_TYPED
                        )
                    )
                }
                updateIfscCode(event.ifscCode)
                if (event.ifscCode.length == 11) {
                    verifyIfscCode(event.ifscCode)
                } else {
                    _uiState.update {
                        it.copy(
                            bankImageUrl = "",
                            bankAddress = "",
                            errorInIfscCode = false
                        )
                    }
                }
            }
        }

    }


    fun fetchSteps() {
        viewModelScope.launch {
            fetchStaticContentUseCase.fetchLendingStaticContent(
                null,
                LendingConstants.StaticContentType.REALTIME_LENDING_BANK_DETAILS_STEPS
            ).collect(
                onSuccess = {
                    it?.realTimeBankDetailSteps?.let { realTimeBankDetailSteps ->
                        _uiState.update {
                            it.copy(
                                realTimeBankDetailSteps = realTimeBankDetailSteps.steps.map {
                                    RealTimeUiStep(model = it.imageUrl, text = it.title)
                                },
                                uspText = realTimeBankDetailSteps.footerText
                            )
                        }
                    }
                }
            )
        }
    }

    fun updateAccountNo(string: String) {
        _uiState.update {
            it.copy(bankAccountNumber = string)
        }

    }

    fun submitBankDetails() {
        viewModelScope.launch {
            updateBankDetailUseCase.updateBankAccountDetails(
                BankAccount(
                    accountNumber = uiState.value.bankAccountNumber,
                    ifsc = uiState.value.ifscCode,
                    bankName = uiState.value.bankName,
                    bankLogo = uiState.value.bankImageUrl
                )
            ).collectUnwrapped(
                onLoading = {
                    _updateBankDetails.emit(RestClientResult.loading())
                },
                onSuccess = {
                    _updateBankDetails.emit(RestClientResult.success(it))
                },
                onError = { message, errorCode ->
                    _updateBankDetails.emit(
                        RestClientResult.error(
                            message = message,
                            errorCode = errorCode
                        )
                    )
                }
            )
        }
    }

    fun updateIfscCode(ifscCode: String) {
        _uiState.update { it.copy(ifscCode = ifscCode) }
    }

    fun verifyIfscCode(ifscCode: String) {
        viewModelScope.launch {
            ifscCodeUseCase.validateIfscCode(ifscCode).collect(
                onSuccess = { ifscResponse ->
                    _uiState.update {
                        it.copy(
                            bankImageUrl = ifscResponse?.bankLogo.orEmpty(),
                            bankName = ifscResponse?.BANK.orEmpty(),
                            bankAddress = "${ifscResponse?.BRANCH.orEmpty()}, ${ifscResponse?.ADDRESS.orEmpty()}",
                            errorInIfscCode = false
                        )
                    }
                },
                onError = { message, _ ->
                    _uiState.update {
                        it.copy(
                            ifscCodeErrorMessage = message,
                            errorInIfscCode = true,
                            bankAddress = "",
                            bankImageUrl = ""
                        )
                    }
                }
            )
        }
    }
}