package com.jar.app.feature_daily_investment_cancellation.impl.ui.intro_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEnum
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentPauseKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentSettingsData
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentSettingsDataUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsSubscriptionType
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyInvestmentSettingsV2ViewModel @Inject constructor(
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val fetchDailyInvestmentSettingsDataUseCase: FetchDailyInvestmentSettingsDataUseCase,
    private val updateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase,
    private val analyticsHandler: AnalyticsApi
) : ViewModel() {
    private val _dsSeekBarFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>()
    val dsSeekBarFlow: MutableSharedFlow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
        get() = _dsSeekBarFlow

    private val _userSavingDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>(RestClientResult.none())
    val userSavingDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _userSavingDetailsFlow.toCommonStateFlow()

    private val _settingsScreenDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentSettingsData?>>>(
            RestClientResult.none()
        )
    val settingsScreenDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentSettingsData?>>>
        get() = _settingsScreenDataFlow.toCommonStateFlow()


    private val _updatePauseDurationFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>(RestClientResult.none())
    val updatePauseDurationFlow: CStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _updatePauseDurationFlow.toCommonStateFlow()

    var pauseState = ""
    var setupVersion = MutableStateFlow<String>("")

    fun fetchSeekBarDataFlow() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                SavingsSubscriptionType.DEFAULT, SavingsType.DAILY_SAVINGS
            )
                .collectLatest {
                    _dsSeekBarFlow.emit(it)
                }
        }
    }

    fun updateAutoInvestPauseDurationFlow(pause: Boolean, pauseDuration: String?, whichClick: String) {
        viewModelScope.launch {
            updateSavingPauseDurationUseCase.updateSavingPauseDuration(
                pause,
                pauseDuration,
                SavingsType.DAILY_SAVINGS
            )
                .collectLatest { data ->
                    analyticsHandler.postEvent(
                        DailyInvestmentCancellationEventKey.DSCancellation_PageClicked,
                        mapOf(
                            DailyInvestmentCancellationEventKey.Button_type to whichClick,
                            DailyInvestmentCancellationKey.State to pauseState
                        )
                    )
                    _updatePauseDurationFlow.emit(data)
                }
        }
    }

    fun fetchUserDSDetailsFlow() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS)
                .collectLatest {
                    it.let {
                        _userSavingDetailsFlow.emit(it)
                    }
                }
        }
    }

    fun fetchSettingsFragmentDataFlow(fromScreen: String? = null) {
        viewModelScope.launch {
            fetchDailyInvestmentSettingsDataUseCase.fetchDailyInvestmentSettingsData()
                .collectLatest {
                    if (fromScreen == DailyInvestmentCancellationEnum.POST_CANCELLATION.name &&
                        it.data?.data?.setupDetails?.status == DailyInvestmentCancellationEnum.DISABLED.name) {
                        analyticsHandler.postEvent(
                            DailyInvestmentCancellationEventKey.Shown_Success_DisableDailySavingsPopUp
                        )
                    }
                    pauseState = it.data?.data?.setupDetails?.status.toString()
                    _settingsScreenDataFlow.emit(it)
                }
        }
    }
}