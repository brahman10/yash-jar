package com.jar.app.feature_daily_investment.impl.ui.pause_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_daily_investment.shared.ui.PauseDailySavingsViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PauseDailySavingsViewModelAndroid @Inject constructor(
    private val updateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        PauseDailySavingsViewModel(
            updateSavingPauseDurationUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}