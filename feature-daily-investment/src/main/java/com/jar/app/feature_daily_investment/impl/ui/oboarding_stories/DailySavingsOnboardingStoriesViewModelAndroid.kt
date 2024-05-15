package com.jar.app.feature_daily_investment.impl.ui.oboarding_stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOnboardingStoryUseCase
import com.jar.app.feature_daily_investment.shared.ui.DailySavingsOnboardingStoriesViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DailySavingsOnboardingStoriesViewModelAndroid @Inject constructor(
    private val fetchDailyInvestmentOnboardingStoryUseCase: FetchDailyInvestmentOnboardingStoryUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        DailySavingsOnboardingStoriesViewModel(
            fetchDailyInvestmentOnboardingStoryUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}