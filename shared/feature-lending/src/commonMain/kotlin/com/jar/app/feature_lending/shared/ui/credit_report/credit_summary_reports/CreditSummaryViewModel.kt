package com.jar.app.feature_lending.shared.ui.credit_report.credit_summary_reports

import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditSummaryDataResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchCreditReportSummaryDataUseCase
import com.jar.app.feature_lending.shared.domain.use_case.RefreshCreditReportSummaryDataUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreditSummaryViewModel constructor(
    private val fetchCreditSummaryUseCase: FetchCreditReportSummaryDataUseCase,
    private val refreshCreditSummaryDataUseCase: RefreshCreditReportSummaryDataUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _creditSummaryData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CreditSummaryDataResponse?>>>(
            RestClientResult.none()
        )
    val creditSummaryData = _creditSummaryData.asStateFlow()
    fun getCreditSummaryData() {
        viewModelScope.launch {
            fetchCreditSummaryUseCase.fetchCreditReportSummary().collect {
                _creditSummaryData.value = it
            }
        }
    }

    fun sendAnalyticsDetailedReportShown(shown: String) {
        val data: CreditSummaryDataResponse? = creditSummaryData.value.data?.data
        var monthlyRepaymentsValue: String = ""
        var creditLimitUsageValue: String = ""
        data?.performance?.map {
            if (it.name?.contains("repayments") == true) {
                monthlyRepaymentsValue = it.status.orEmpty()
            } else {
                creditLimitUsageValue = it.status.orEmpty()
            }
        }
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_DetailedReportScreenLaunched,
            mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.credit_report_main_screen,
                LendingEventKeyV2.action to shown,
                LendingEventKeyV2.credit_score to data?.creditScore.orZero(),
                LendingEventKeyV2.credit_score_text to data?.creditScoreResult.orEmpty(),
                LendingEventKeyV2.monthly_repayments_value to monthlyRepaymentsValue,
                LendingEventKeyV2.credit_limit_usage_value to creditLimitUsageValue,
                LendingEventKeyV2.report_update_avaliable to data?.refreshCreditReport?.refreshCreditScore.orFalse()
            )
        )
    }


    fun sendAnalyticsNeedHelpEvent() {
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_NeedHelpClicked,
            mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.credit_report_main_screen,
            )
        )
    }

    fun sendAnalyticsBackButtonEvent() {
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_BackButtonClicked,
            mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.credit_report_main_screen,
            )
        )
    }

    fun sendRequestForRefresh() {
        viewModelScope.launch {
            refreshCreditSummaryDataUseCase.refreshCreditReportSummary().collect {
               if (it.data?.success.orFalse()){
                   getCreditSummaryData()
               }
            }
        }
    }
}