package com.jar.app.feature_daily_investment.shared.ui

import com.jar.app.feature_daily_investment.shared.domain.model.DSOnboardingStoryData
import com.jar.app.feature_daily_investment.shared.domain.model.OnboardingStoryIndicatorData
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOnboardingStoryUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DailySavingsOnboardingStoriesViewModel constructor(
    private val fetchDailyInvestmentOnboardingStoryUseCase: FetchDailyInvestmentOnboardingStoryUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _storyDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DSOnboardingStoryData?>>>(
            RestClientResult.none()
        )
    val storyDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DSOnboardingStoryData?>>>
        get() = _storyDataFlow.toCommonStateFlow()

    private val _indicatorFlowData =
        MutableSharedFlow<List<OnboardingStoryIndicatorData>>()
    val indicatorFlowData: CFlow<List<OnboardingStoryIndicatorData>>
        get() = _indicatorFlowData.toCommonFlow()

    fun fetchOnboardingStoryData() {
        viewModelScope.launch {
            fetchDailyInvestmentOnboardingStoryUseCase.fetchDailyInvestmentStoriesData().collect {
                _storyDataFlow.emit(it)
                val indicatorList: MutableList<OnboardingStoryIndicatorData> = mutableListOf()
                if (it.data?.data?.stories != null) {
                    for (x in it.data?.data?.stories?.withIndex()!!) {
                        if (x.index == 0) {
                            indicatorList.add(
                                OnboardingStoryIndicatorData(
                                    0,
                                    true
                                )
                            )
                        } else {
                            indicatorList.add(
                                OnboardingStoryIndicatorData(
                                    x.index,
                                    false
                                )
                            )
                        }
                    }
                    updateIndicatorData(indicatorList, 0)
                }
            }
        }
    }

    fun updateIndicatorData(data: List<OnboardingStoryIndicatorData>, position: Int) {
        viewModelScope.launch {
            val newList = data.map { it.copy() }
            val selectedIndex = newList.find { it.isSelected }
            if (selectedIndex != null) {
                newList.getOrNull(selectedIndex.id)?.isSelected = false
            }
            newList[position].isSelected = true
            _indicatorFlowData.emit(newList)
        }
    }
}
