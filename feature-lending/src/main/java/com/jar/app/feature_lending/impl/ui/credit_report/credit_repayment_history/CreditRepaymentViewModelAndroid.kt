package com.jar.app.feature_lending.impl.ui.credit_report.credit_repayment_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.feature_lending.shared.domain.use_case.FetchCreditDetailedReportUseCase
import com.jar.app.feature_lending.shared.ui.credit_report.credit_repayment_history.CreditRepaymentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
internal class CreditRepaymentViewModelAndroid @Inject constructor(
    private val fetchCreditDetailedReportUseCase: FetchCreditDetailedReportUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        CreditRepaymentViewModel(
            fetchCreditDetailedReportUseCase=fetchCreditDetailedReportUseCase,
            analyticsApi = analyticsApi,
            coroutineScope = viewModelScope
        )
    }
    fun getInstance() = viewModel
}