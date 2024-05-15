package com.jar.app.feature_onboarding.shared.ui.onboarding_story

import com.jar.app.feature_onboarding.shared.domain.model.OnboardingStoryData
import com.jar.app.feature_onboarding.shared.domain.model.OnboardingStoryIndicatorData
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOnboardingStoriesUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OnboardingStoryFragmentViewModel constructor(
    private val fetchOnboardingStoriesUseCase: FetchOnboardingStoriesUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _onboardingStoryFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<OnboardingStoryData>>>(RestClientResult.none())
    val onboardingStoryFlow: CStateFlow<RestClientResult<ApiResponseWrapper<OnboardingStoryData>>>
        get() = _onboardingStoryFlow.toCommonStateFlow()

    private val _indicatorFlow =
        MutableStateFlow<List<OnboardingStoryIndicatorData>>(emptyList())
    val indicatorFlow: CFlow<List<OnboardingStoryIndicatorData>>
        get() = _indicatorFlow.toCommonFlow()

    fun fetchOnboardingStoryData() {
        viewModelScope.launch {
            fetchOnboardingStoriesUseCase.fetchOnboardingStories().collect {
                _onboardingStoryFlow.emit(it)
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
            val newList = ArrayList(data.map { it.copy() })
            val selectedIndex = newList.find { it.isSelected }
            if (selectedIndex != null) {
                newList.getOrNull(selectedIndex.id)?.isSelected = false
            }
            newList[position].isSelected = true
            _indicatorFlow.emit(newList)
        }
    }
}