package com.jar.app.feature_lending.impl.ui.credit_report.check_credit_score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.ui.credit_report.check_credit_score.CheckCreditScoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
internal class CheckCreditScoreViewModelAndroid @Inject constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {
    private val viewModel by lazy {
        CheckCreditScoreViewModel(
            fetchStaticContentUseCase = fetchStaticContentUseCase,
            analyticsApi= analyticsApi,
            coroutineScope = viewModelScope
        )
    }
    fun getInstance() = viewModel
}