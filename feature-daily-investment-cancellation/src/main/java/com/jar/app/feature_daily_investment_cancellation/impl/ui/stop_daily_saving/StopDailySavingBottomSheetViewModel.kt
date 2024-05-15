package com.jar.app.feature_daily_investment_cancellation.impl.ui.stop_daily_saving

import androidx.compose.runtime.MutableState
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_analytics.EventKey.is_Permanently_Cancel_flow
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEnum
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentPauseKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStopKey
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentConfirmActionDetails
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.StatisticsContent
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentConfirmActionDataUseCase
import com.jar.app.feature_daily_investment_cancellation.shared.util.DailyInvestmentCancellationConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.DisableUserSavingsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StopDailySavingBottomSheetViewModel @Inject constructor(
    private val disableUserSavingsUseCase: DisableUserSavingsUseCase,
    private val confirmActionDataUseCase: FetchDailyInvestmentConfirmActionDataUseCase,
    private val analyticsHandler: AnalyticsApi
) : ViewModel() {

    private val _disableDailySavingFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>(RestClientResult.none())
    val disableDailySavingFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _disableDailySavingFlow.toCommonStateFlow()

    private val _confirmActionDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentConfirmActionDetails>>>(
            RestClientResult.none()
        )
    val confirmActionDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentConfirmActionDetails>>>
        get() = _confirmActionDetailsFlow.toCommonStateFlow()

    fun disableDailySavings(version: String? = null) {
        if (version == DailyInvestmentCancellationEnum.V3.name) {
            analyticsHandler.postEvent(
                DailyInvestmentCancellationEventKey.DSCancellation_StopDSpopup_Clicked, mapOf(
                    DailyInvestmentCancellationEventKey.Button_type to DailyInvestmentCancellationEventKey.Stop_Now,
                    DailyInvestmentPauseKey.Final_selected_days to DailyInvestmentCancellationEventKey.Infinite,
                    is_Permanently_Cancel_flow to true
                )
            )
        }
        viewModelScope.launch {
            disableUserSavingsUseCase.disableSavings(SavingsType.DAILY_SAVINGS).collectLatest {
                _disableDailySavingFlow.emit(it)
            }
        }
    }

    fun cancellationKnowledgeBottomSheetData() {
        viewModelScope.launch {
            confirmActionDataUseCase.fetchDailyInvestmentConfirmActionData(
                DailyInvestmentCancellationConstants.KNOWLEDGE
            ).collectLatest {
                _confirmActionDetailsFlow.emit(it)
            }
        }
    }

    fun cancellationStatisticsBottomSheetData() {
        viewModelScope.launch {
            confirmActionDataUseCase.fetchDailyInvestmentConfirmActionData(
                DailyInvestmentCancellationConstants.STATISTICS
            ).collectLatest {
                _confirmActionDetailsFlow.emit(it)
            }
        }
    }
}