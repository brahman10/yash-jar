package com.jar.app.feature_daily_investment.shared.ui

import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchIsSavingPausedUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DailySavingsSettingsViewModel constructor(
    private val fetchDailyInvestmentStatusUseCase: FetchDailyInvestmentStatusUseCase,
    private val fetchIsSavingsPausedUseCase: FetchIsSavingPausedUseCase,
    private val updateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    var isSavingsEnabled = false
    var isSavingPaused = false

    private val _updatePauseDurationFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val updatePauseDurationFlow: CFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _updatePauseDurationFlow.toCommonFlow()

    private val _dailySavingsDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val dailySavingsDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _dailySavingsDetailsFlow.toCommonFlow()

    fun fetchUserDailySavingsDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS).collect {
                _dailySavingsDetailsFlow.emit(it)
            }
        }
    }

    fun updateAutoInvestPauseDuration(pause: Boolean, pauseDuration: String?) {
        viewModelScope.launch {
            updateSavingPauseDurationUseCase.updateSavingPauseDuration(
                pause, pauseDuration, SavingsType.DAILY_SAVINGS
            ).collectLatest { _updatePauseDurationFlow.emit(it) }
        }
    }

}