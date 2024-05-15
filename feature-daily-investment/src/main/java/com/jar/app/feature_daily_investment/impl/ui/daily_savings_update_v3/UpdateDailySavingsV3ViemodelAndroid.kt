package com.jar.app.feature_daily_investment.impl.ui.daily_savings_update_v3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchUpdateDailyInvestmentStaticDataUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.ui.DailySavingSetupStatusViewModel
import com.jar.app.feature_daily_investment.shared.ui.UpdateDailySavingsV3ViewModel
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdateDailySavingsV3ViemodelAndroid @Inject constructor(
    private val fetchUpdateDailyInvestmentStaticDataUseCase: FetchUpdateDailyInvestmentStaticDataUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        UpdateDailySavingsV3ViewModel(
            fetchUpdateDailyInvestmentStaticDataUseCase,
            manageSavingPreferenceUseCase,
            isAutoInvestResetRequiredUseCase,
            updateDailyInvestmentStatusUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}