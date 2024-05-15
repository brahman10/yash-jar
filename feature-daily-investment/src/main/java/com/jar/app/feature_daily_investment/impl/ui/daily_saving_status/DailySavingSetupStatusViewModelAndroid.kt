package com.jar.app.feature_daily_investment.impl.ui.daily_saving_status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_daily_investment.shared.ui.DailySavingSetupStatusViewModel
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DailySavingSetupStatusViewModelAndroid @Inject constructor(
    private val fetchMandatePaymentStatusUseCase: FetchMandatePaymentStatusUseCase,
    private val fetchWeeklyChallengeMetaUseCase: FetchWeeklyChallengeMetaDataUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        DailySavingSetupStatusViewModel(
            fetchMandatePaymentStatusUseCase,
            fetchWeeklyChallengeMetaUseCase,
            fetchUserSavingsDetailsUseCase,
            isAutoInvestResetRequiredUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}