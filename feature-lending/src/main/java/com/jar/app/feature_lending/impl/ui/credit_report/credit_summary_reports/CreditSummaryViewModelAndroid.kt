package com.jar.app.feature_lending.impl.ui.credit_report.credit_summary_reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.feature_lending.shared.domain.use_case.FetchCreditReportSummaryDataUseCase
import com.jar.app.feature_lending.shared.domain.use_case.RefreshCreditReportSummaryDataUseCase
import com.jar.app.feature_lending.shared.ui.credit_report.credit_summary_reports.CreditSummaryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CreditSummaryViewModelAndroid @Inject constructor(
    private val fetchCreditSummaryUseCase: FetchCreditReportSummaryDataUseCase,
    private val refreshCreditSummaryDataUseCase: RefreshCreditReportSummaryDataUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        CreditSummaryViewModel(
            fetchCreditSummaryUseCase = fetchCreditSummaryUseCase,
            refreshCreditSummaryDataUseCase = refreshCreditSummaryDataUseCase,
            analyticsApi = analyticsApi,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}