package com.jar.app.feature_daily_investment.shared.ui

import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_daily_investment.shared.domain.model.PauseDailySavingData
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.model.PauseSavingOption
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PauseDailySavingsViewModel constructor(
    private val updateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    var pauseDailySavingsData: PauseDailySavingData? = null

    private val _pauseOptionsFlow = MutableStateFlow<RestClientResult<List<PauseDailySavingData>>>(
        RestClientResult.none()
    )
    val pauseOptionsFlow: CStateFlow<RestClientResult<List<PauseDailySavingData>>>
        get() = _pauseOptionsFlow.toCommonStateFlow()

    private val _updatePauseDurationFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val updatePauseDurationFlow: CFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _updatePauseDurationFlow.toCommonFlow()

    val isSelectedFlow = MutableSharedFlow<PauseDailySavingData?>()

    fun updateAutoInvestPauseDuration(pause: Boolean, pauseDailySavingData: PauseDailySavingData) {
        viewModelScope.launch {
            updateSavingPauseDurationUseCase.updateSavingPauseDuration(
                pause,
                pauseDailySavingData.pauseDailySavingsOption.name,
                SavingsType.DAILY_SAVINGS
            )
                .collectLatest {
                    _updatePauseDurationFlow.emit(it)
                }
        }
    }

    fun getPauseDailySavingOptions() {
        viewModelScope.launch {
            _pauseOptionsFlow.emit(
                RestClientResult.success(
                    listOf(
                        PauseDailySavingData(PauseSavingOption.TWO, true),
                        PauseDailySavingData(PauseSavingOption.EIGHT),
                        PauseDailySavingData(PauseSavingOption.TWELVE),
                        PauseDailySavingData(PauseSavingOption.FIFTEEN)
                    )
                )
            )
            delay(500)
            isSelectedFlow.emit(_pauseOptionsFlow.value.data?.find { it.isSelected })
        }
    }

    fun toggleSelection(
        pauseDailySavingData: PauseDailySavingData,
        pauseDailySavingOptionList: List<PauseDailySavingData>
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newList = pauseDailySavingOptionList.map {
                it.copy(
                    pauseDailySavingsOption = it.pauseDailySavingsOption,
                    isSelected = if (it.pauseDailySavingsOption.timeValue == pauseDailySavingData.pauseDailySavingsOption.timeValue) pauseDailySavingData.isSelected.not() else false
                )
            }
            isSelectedFlow.emit(newList.find { it.isSelected })
            _pauseOptionsFlow.emit(RestClientResult.success(newList))
        }
    }

}