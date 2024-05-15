package com.jar.app.feature_settings.impl.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_preferences.api.RetainedPrefsApi
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchGoalBasedSavingSettingUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_settings.domain.use_case.DailyInvestmentCancellationV2RedirectionDetailsUseCase
import com.jar.app.feature_settings.ui.SettingsV2ViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SettingsV2ViewModelAndroid @Inject constructor(
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val fetchGoalBasedSavingSettingUseCase: FetchGoalBasedSavingSettingUseCase,
    private val dailyInvestmentCancellationV2RedirectionDetailsUseCase: DailyInvestmentCancellationV2RedirectionDetailsUseCase,
    prefsApi: PrefsApi,
    retainedPrefsApi: RetainedPrefsApi,
    deviceUtils: DeviceUtils
): ViewModel() {

    private val viewModel by lazy {
        SettingsV2ViewModel(
            fetchGoldSipDetailsUseCase,
            fetchUserSavingsDetailsUseCase,
            fetchGoalBasedSavingSettingUseCase,
            dailyInvestmentCancellationV2RedirectionDetailsUseCase,
            prefsApi,
            retainedPrefsApi,
            deviceUtils,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}