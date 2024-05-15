package com.jar.app.feature_lending.shared.ui.credit_report.credit_repayment_history

import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditDetailedReportResponse
import com.jar.app.feature_lending.shared.domain.model.creditReport.Performance
import com.jar.app.feature_lending.shared.domain.use_case.FetchCreditDetailedReportUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreditRepaymentViewModel constructor(
    private val fetchCreditDetailedReportUseCase: FetchCreditDetailedReportUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _creditDetailedReportData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CreditDetailedReportResponse?>>>(
            RestClientResult.none()
        )
    val creditDetailedReportData = _creditDetailedReportData.asStateFlow()
    fun getCreditDetailedReport(type: String) {
        viewModelScope.launch {
            fetchCreditDetailedReportUseCase.fetchCreditDetailedReport(type).collect {
                _creditDetailedReportData.value = it
            }
        }
    }

    fun sendAnalyticsTabClickedLaunchedEvent(isCreditTab: Boolean,event: Performance) {
        val responseData: CreditDetailedReportResponse? = creditDetailedReportData.value.data?.data
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_DetailedReportScreenLaunched,
            mapOf(
                LendingEventKeyV2.screen_name to event.type?.lowercase() + "_screen",
                LendingEventKeyV2.action to if (!isCreditTab)  LendingEventKeyV2.credit_card_tab_clicked else LendingEventKeyV2.loan_account_tab_clicked,
                LendingEventKeyV2.monthly_repayments_value to responseData?.title.orEmpty()
            )
        )
    }


    fun sendAnalyticsScreenLaunchedEvent(
        isCreditTab: Boolean,
        event: Performance
    ) {
        val responseData: CreditDetailedReportResponse? = creditDetailedReportData.value.data?.data
        val totalAccountCount = responseData?.loanAccountsList?.size.orZero() + responseData?.creditCardsList?.size.orZero()
        val totalCreditAccountCount = responseData?.creditCardsList?.size.orZero()
        val totalLoanAccountCount = responseData?.loanAccountsList?.size.orZero()
        val totalLoanActiveAccountCount =
            responseData?.loanAccountsList?.filter { it.accountDetails?.isActive.orFalse() }?.size.orZero()
        val totalLoanClosedAccountCount = totalLoanAccountCount - totalLoanActiveAccountCount

        val totalCreditActiveAccountCount =
            responseData?.creditCardsList?.filter { it.accountDetails?.isActive.orFalse() }?.size.orZero()
        val totalCreditClosedAccountCount = totalCreditAccountCount - totalCreditActiveAccountCount

        val totalActiveCount = totalLoanActiveAccountCount + totalCreditActiveAccountCount
       // val totalClosedCount = totalLoanClosedAccountCount + totalCreditClosedAccountCount

        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_DetailedReportScreenLaunched,
            mapOf(
                LendingEventKeyV2.screen_name to event.type?.lowercase() + "_screen",
                LendingEventKeyV2.action to if (!isCreditTab) LendingEventKeyV2.credit_card_tab_shown else LendingEventKeyV2.loan_account_tab_shown,
                LendingEventKeyV2.loan_account_count to totalLoanAccountCount,
                LendingEventKeyV2.total_account_count to totalAccountCount,
                LendingEventKeyV2.active_loan_account_count to totalLoanActiveAccountCount,
                LendingEventKeyV2.active_credit_card_count to totalCreditActiveAccountCount,
                LendingEventKeyV2.closed_credit_card_count to totalCreditClosedAccountCount,
                LendingEventKeyV2.closed_loan_account_count to totalLoanClosedAccountCount,
                LendingEventKeyV2.total_active_account_count to totalActiveCount,
                LendingEventKeyV2.credit_card_count to totalCreditAccountCount,
                LendingEventKeyV2.monthly_repayments_value to responseData?.title.orEmpty()
            )
        )
    }
    fun sendAnalyticsNeedHelpEvent(event: String) {
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_NeedHelpClicked,
            mapOf(LendingEventKeyV2.screen_name to event.lowercase() + "_screen")
        )
    }
    fun sendAnalyticsBackButtonEvent(event: String) {
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_BackButtonClicked,
            mapOf(LendingEventKeyV2.screen_name to event.lowercase() + "_screen")
        )
    }
}