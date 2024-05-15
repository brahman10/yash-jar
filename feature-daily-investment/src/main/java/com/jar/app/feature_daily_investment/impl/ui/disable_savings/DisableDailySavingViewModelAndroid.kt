package com.jar.app.feature_daily_investment.impl.ui.disable_savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_daily_investment.shared.ui.DisableDailySavingViewModel
import com.jar.app.feature_savings_common.shared.domain.use_case.DisableUserSavingsUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DisableDailySavingViewModelAndroid @Inject constructor(
    private val disableUserSavingsUseCase: DisableUserSavingsUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        DisableDailySavingViewModel(
            disableUserSavingsUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}