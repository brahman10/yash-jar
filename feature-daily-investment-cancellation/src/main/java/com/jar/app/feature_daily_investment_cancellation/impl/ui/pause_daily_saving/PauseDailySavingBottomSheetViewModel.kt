package com.jar.app.feature_daily_investment_cancellation.impl.ui.pause_daily_saving

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_analytics.EventKey.is_Permanently_Cancel_flow
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_daily_investment.shared.domain.model.PauseDailySavingData
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEnum
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Button_type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentPauseKey
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDetails
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentPauseDataUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
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
internal class PauseDailySavingBottomSheetViewModel @Inject constructor(
    private val updateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase,
    private val pauseDetailsUseCase: FetchDailyInvestmentPauseDataUseCase,
    private val analyticsHandler: AnalyticsApi
) : ViewModel() {

    private val _updatePauseDurationFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>(RestClientResult.none())
    val updatePauseDurationFlow: CStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _updatePauseDurationFlow.toCommonStateFlow()

    private val _selectedDaysFlow = MutableStateFlow<Int?>(null)
    val selectedDaysFlow: CStateFlow<Int?> = _selectedDaysFlow.toCommonStateFlow()

    private val _differenceDaysFlow = MutableStateFlow<Int?>(null)
    val differenceDaysFlow: CStateFlow<Int?> = _differenceDaysFlow.toCommonStateFlow()

    private val _customDaysFlow = MutableStateFlow<Long?>(null)
    val customDaysFlow: CStateFlow<Long?> = _customDaysFlow.toCommonStateFlow()

    private val _estimatedDaysFlow = MutableStateFlow<String?>(null)
    val estimatedDaysFlow: CStateFlow<String?> = _estimatedDaysFlow.toCommonStateFlow()

    private val _pauseDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentPauseDetails>>>(RestClientResult.none())
    val pauseDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentPauseDetails>>>
        get() = _pauseDetailsFlow.toCommonStateFlow()

    fun setEstimatedDate(date: String) {
        viewModelScope.launch {
            _estimatedDaysFlow.emit(date)
        }
    }

    fun setTotalDay(days: Int, version: String? = null) {
        viewModelScope.launch {
            _selectedDaysFlow.emit(days)
            _customDaysFlow.emit(null)
        }
    }

    fun setDifferenceDays(days: Int) {
        viewModelScope.launch {
            _differenceDaysFlow.emit(days)
        }
    }

    fun setCustomDate(customDate: Long) {
        viewModelScope.launch {
            _customDaysFlow.emit(customDate)
            _selectedDaysFlow.emit(null)
        }
    }

    fun updateAutoInvestPauseDurationFlow(
        pause: Boolean,
        pauseDailySavingData: PauseDailySavingData?,
        customDuration: Long?,
        version: String? = null
    ) {
        viewModelScope.launch {
            updateSavingPauseDurationUseCase.updateSavingPauseDuration(
                pause,
                pauseDailySavingData?.pauseDailySavingsOption?.name,
                SavingsType.DAILY_SAVINGS,
                customDuration
            )
                .collectLatest {
                    if (version == DailyInvestmentCancellationEnum.V3.name) {
                        analyticsHandler.postEvent(
                            DailyInvestmentCancellationEventKey.DSCancellation_StopDSpopup_Clicked,
                            mapOf(
                                Button_type to DailyInvestmentCancellationEventKey.Stop_Now,
                                DailyInvestmentPauseKey.Final_selected_days to pauseDailySavingData?.pauseDailySavingsOption?.name.toString(),
                                is_Permanently_Cancel_flow to true
                            )
                        )
                    }
                    _updatePauseDurationFlow.emit(it)
                }
        }
    }

    fun fetchPauseDetailsDataFlow() {
        viewModelScope.launch {
            pauseDetailsUseCase.fetchDailyInvestmentPauseData().collectLatest {
                _pauseDetailsFlow.emit(it)
            }
        }
    }
}