package com.jar.feature_quests.impl.ui.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.feature_quests.shared.domain.model.WelcomeRewardData
import com.jar.feature_quests.shared.domain.use_case.FetchWelcomeRewardUseCase
import com.jar.feature_quests.shared.domain.use_case.UnlockWelcomeRewardUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SplashScreenViewModel @Inject constructor(
    private val unlockWelcomeRewardUseCase: UnlockWelcomeRewardUseCase,
    private val fetchWelcomeRewardUseCase: FetchWelcomeRewardUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val _unlockApi = MutableSharedFlow<Unit>()
    val unlockApi: CFlow<Unit> = _unlockApi.toCommonFlow()


    private val _welcomeRewardsData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WelcomeRewardData?>>>(RestClientResult.none())
    val welcomeRewardsData: CStateFlow<RestClientResult<ApiResponseWrapper<WelcomeRewardData?>>>
        get() = _welcomeRewardsData.toCommonStateFlow()

    fun fetchWelcome() {
        viewModelScope.launch {
            fetchWelcomeRewardUseCase.fetchWelcomeReward().collectLatest {
                _welcomeRewardsData.emit(it)
            }
        }
    }

    fun unlockDone() {
        viewModelScope.launch {
            unlockWelcomeRewardUseCase.unlockWelcomeReward().collectUnwrapped(
                onSuccessWithNullData = {
                    _unlockApi.emit(Unit)
                },
                onSuccess = {
                    _unlockApi.emit(Unit)
                }
            )
        }
    }

    fun fireShownLandingAnalyticsEvent() {
        analyticsApi.postEvent(QuestEventKey.Events.Shown_QuestLandingPage)
    }

    fun fireClickedLandingAnalyticsEvent(analyticsData: Map<String, String>) {
        analyticsApi.postEvent(
            QuestEventKey.Events.Clicked_QuestLandingPage,
            analyticsData
        )
    }
}
