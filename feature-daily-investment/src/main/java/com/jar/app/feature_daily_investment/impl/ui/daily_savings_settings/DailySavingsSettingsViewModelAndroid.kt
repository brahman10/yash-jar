package com.jar.app.feature_daily_investment.impl.ui.daily_savings_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchIsSavingPausedUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_daily_investment.shared.ui.DailySavingsSettingsViewModel
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DailySavingsSettingsViewModelAndroid @Inject constructor(
    private val fetchDailyInvestmentStatusUseCase: FetchDailyInvestmentStatusUseCase,
    private val fetchIsSavingsPausedUseCase: FetchIsSavingPausedUseCase,
    private val updateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        DailySavingsSettingsViewModel(
            fetchDailyInvestmentStatusUseCase,
            fetchIsSavingsPausedUseCase,
            updateSavingPauseDurationUseCase,
            fetchUserSavingsDetailsUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}