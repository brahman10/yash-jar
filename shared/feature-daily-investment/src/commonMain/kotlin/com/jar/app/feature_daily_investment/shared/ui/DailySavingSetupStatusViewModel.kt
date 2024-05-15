package com.jar.app.feature_daily_investment.shared.ui

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper as LibraryApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult as LibraryRestClientResult

class DailySavingSetupStatusViewModel constructor(
    private val fetchMandatePaymentStatusUseCase: FetchMandatePaymentStatusUseCase,
    private val fetchWeeklyChallengeMetaUseCase: FetchWeeklyChallengeMetaDataUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {


    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _weeklyChallengeMetaFlow =
        MutableSharedFlow<LibraryRestClientResult<LibraryApiResponseWrapper<WeeklyChallengeMetaData?>>>()
    val weeklyChallengeMetaFlow: CFlow<LibraryRestClientResult<LibraryApiResponseWrapper<WeeklyChallengeMetaData?>>>
        get() = _weeklyChallengeMetaFlow.toCommonFlow()

    private val _mandateStatusFlow =
        MutableSharedFlow<LibraryRestClientResult<LibraryApiResponseWrapper<FetchMandatePaymentStatusResponse?>>>()
    val mandateStatusFlow: CFlow<LibraryRestClientResult<LibraryApiResponseWrapper<FetchMandatePaymentStatusResponse?>>>
        get() = _mandateStatusFlow.toCommonFlow()

    private val _dailySavingDetailFlow =
        MutableSharedFlow<LibraryRestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val dailySavingDetailFlow: CFlow<LibraryRestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _dailySavingDetailFlow.toCommonFlow()

    private val _isAutoPayResetRequiredFlow =
        MutableSharedFlow<LibraryRestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredFlow: CFlow<LibraryRestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredFlow.toCommonFlow()

    fun fetchUserDSDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS).collect {
                _dailySavingDetailFlow.emit(it)
            }
        }
    }

    fun fetchAutoInvestStatus(mandatePaymentResultFromSDK: MandatePaymentResultFromSDK) {
        viewModelScope.launch {
            fetchMandatePaymentStatusUseCase.fetchMandatePaymentStatus(mandatePaymentResultFromSDK)
                .collect {
                    _mandateStatusFlow.emit(it)
                }
        }
    }

    fun fetchWeeklyChallengeMetaData() {
        viewModelScope.launch {
            fetchWeeklyChallengeMetaUseCase.fetchWeeklyChallengeMetaData(false).collect {
                _weeklyChallengeMetaFlow.emit(it)
            }
        }
    }

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.DAILY_SAVINGS.name
            ).collect {
                _isAutoPayResetRequiredFlow.emit(it)
            }
        }
    }

}