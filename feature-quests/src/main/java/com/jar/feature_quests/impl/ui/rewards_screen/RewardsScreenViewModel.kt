package com.jar.feature_quests.impl.ui.rewards_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.orZero
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.feature_quests.impl.util.QuestEventKey.Events.Clicked_QuestRewardsPage
import com.jar.feature_quests.impl.util.QuestEventKey.Events.Shown_QuestRewardsPage
import com.jar.feature_quests.impl.util.QuestEventKey.Properties.button_type
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.feature_quests.shared.domain.model.RewardsResponse
import com.jar.feature_quests.shared.domain.use_case.FetchQuestRewardsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RewardsScreenViewModel @Inject constructor(
    private val rewardsUseCase: FetchQuestRewardsUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val _welcomeRewardsData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<RewardsResponse?>>>(RestClientResult.none())
    val welcomeRewardsData: CStateFlow<RestClientResult<ApiResponseWrapper<RewardsResponse?>>>
        get() = _welcomeRewardsData.toCommonStateFlow()

    fun fetchWelcome() {
        viewModelScope.launch {
            rewardsUseCase.fetchQuestRewards().collectLatest {
                _welcomeRewardsData.emit(it)
                if (it.status != RestClientResult.Status.LOADING) {
                    fireShownAnalyticsEvent()
                }
            }
        }
    }

    fun fireShownAnalyticsEvent() {
        analyticsApi.postEvent(Shown_QuestRewardsPage, buildMapForAnalytics())
    }

    fun fireClickedAnalyticEvent(buttonClick: String) {
        analyticsApi.postEvent(Clicked_QuestRewardsPage, buildMapForAnalytics().apply {
            this[button_type] = buttonClick
        })
    }

    fun buildMapForAnalytics(): MutableMap<String, String> {
        return mutableMapOf<String, String>(
            QuestEventKey.Properties.locked_rewards to _welcomeRewardsData.value.data?.data?.lockedRewardsCount.orZero().toString(),
            QuestEventKey.Properties.unlocked_rewards to _welcomeRewardsData.value.data?.data?.unlockedRewardsCount.orZero().toString(),
            QuestEventKey.Properties.total_rewards to _welcomeRewardsData.value.data?.data?.rewardsList?.size.orZero().toString(),
            QuestEventKey.Properties.missed_rewards to _welcomeRewardsData.value.data?.data?.missedRewardsCount.orZero().toString(),
        )
    }
}
