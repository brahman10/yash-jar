package com.jar.app.feature_lending.shared.ui.credit_report.check_credit_score

import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.creditReport.ReportDetails
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckCreditScoreViewModel constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {
    private var isFirstRequest: Boolean = false
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _creditStaticContent =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>(
            RestClientResult.none()
        )
    val creditStaticContent = _creditStaticContent.asStateFlow()


    fun getStaticCreditReportData() {
        viewModelScope.launch {
            fetchStaticContentUseCase.fetchLendingStaticContent(
                null,
                LendingConstants.StaticContentType.CREDIT_REPORT_EXISTENCE
            ).collect {
                _creditStaticContent.value = it
            }
        }
    }

    fun sendAnalyticsNeedHelpEvent() {
        val creditReportExist: Boolean = creditStaticContent.value.data?.data?.creditReport?.creditReportExist.orFalse()
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_NeedHelpClicked,
            mapOf(
                LendingEventKeyV2.screen_name to  if (!creditReportExist && isFirstRequest) LendingEventKeyV2.credit_score_not_found_screen else LendingEventKeyV2.check_credit_score_screen,
            )
        )
    }

    fun sendAnalyticsCreditsScreenLaunchedEvent(action: String, creditScoreStatus: String) {
        val creditReportExist: Boolean = creditStaticContent.value.data?.data?.creditReport?.creditReportExist.orFalse()
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_CreditScoreScreenLaunched,
            mapOf(
                LendingEventKeyV2.screen_name to  if (!creditReportExist && isFirstRequest) LendingEventKeyV2.credit_score_not_found_screen else LendingEventKeyV2.check_credit_score_screen,
                LendingEventKeyV2.action to action,
                LendingEventKeyV2.credit_score_status to creditScoreStatus
            )
        )
    }

    fun sendAnalyticsCreditsScreenBSLaunchedEvent(action: String) {
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_CreditScoreScreenLaunched,
            mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.check_credit_score_screen,
                LendingEventKeyV2.action to action
            )
        )
    }

    fun sendAnalyticsCreditsShownEvent(
        action: String,
        creditScoreStatus: String
    ) {
        val data: ReportDetails? = creditStaticContent.value.data?.data?.creditReport?.reportDetails
        val creditReportExist: Boolean = creditStaticContent.value.data?.data?.creditReport?.creditReportExist.orFalse()
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_CreditScoreScreenLaunched,
            mapOf(
                LendingEventKeyV2.screen_name to if (!creditReportExist && isFirstRequest) LendingEventKeyV2.credit_score_not_found_screen else LendingEventKeyV2.check_credit_score_screen ,
                LendingEventKeyV2.action to action,
                LendingEventKeyV2.credit_score_status to creditScoreStatus,
                LendingEventKeyV2.credit_score to data?.creditScore.orZero(),
                LendingEventKeyV2.credit_score_text to data?.creditScoreResult.orEmpty()
            )
        )
    }

    fun sendAnalyticsBackButtonEvent() {
        val creditReportExist: Boolean = creditStaticContent.value.data?.data?.creditReport?.creditReportExist.orFalse()
        analyticsApi.postEvent(
            LendingEventKeyV2.CreditReport_BackButtonClicked,
            mapOf(
                LendingEventKeyV2.screen_name to if (!creditReportExist && isFirstRequest) LendingEventKeyV2.credit_score_not_found_screen else LendingEventKeyV2.check_credit_score_screen,
            )
        )
    }

    fun setFlagForRefresh(b: Boolean) {
        isFirstRequest = b
    }

    fun getFlagForRefresh(): Boolean {
        return isFirstRequest
    }
}