package com.jar.app.feature_daily_investment.impl.ui.onboarding_setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOnboardingFragmentDataUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.ui.DailyInvestmentOnboardingVariantsViewModel
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DailyInvestmentOnboardingVariantsViewModelAndroid @Inject constructor(
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    private val fetchDailyInvestmentOnboardingFragmentDataUseCase: FetchDailyInvestmentOnboardingFragmentDataUseCase,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase,
    ) : ViewModel() {


    private val viewModel by lazy {
        DailyInvestmentOnboardingVariantsViewModel(
            updateDailyInvestmentStatusUseCase,
            isAutoInvestResetRequiredUseCase,
            fetchUserSavingsDetailsUseCase,
            manageSavingPreferenceUseCase,
            fetchSavingsSetupInfoUseCase,
            fetchDailyInvestmentOnboardingFragmentDataUseCase,
            fetchExitSurveyQuestionsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}