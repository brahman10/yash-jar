package com.jar.feature_quests.impl.ui.dashboard_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.feature_quests.shared.domain.model.QUEST_CLICK_ACTION
import com.jar.feature_quests.shared.domain.model.Quest
import com.jar.feature_quests.shared.domain.model.QuestStatus
import com.jar.feature_quests.shared.domain.model.QuestsDashboardData
import com.jar.feature_quests.shared.domain.use_case.FetchHomePageUseCase
import com.jar.feature_quests.shared.domain.use_case.MarkGameInProgressUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@Immutable
data class DashboardViewState(
    val dashboardResponse: RestClientResult<ApiResponseWrapper<QuestsDashboardData>>? = null,
    val shouldShowCongratsCard: Boolean = true,
    val shouldShowInternetNotWorking: Boolean = false
)

@HiltViewModel
internal class DashboardViewModel @Inject constructor(
    private val fetchHomePageUseCase: FetchHomePageUseCase,
    private val markGameInProgressUseCase: MarkGameInProgressUseCase,
    private val networkFlow: NetworkFlow,
    private val prefs: PrefsApi,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {
    private var networkJob: Job? = null

    private val _dashboardState = MutableStateFlow<DashboardViewState>(DashboardViewState(null))
    val dashboardStateFlow: CStateFlow<DashboardViewState> = _dashboardState.toCommonStateFlow()

    private val _markInProgressFLow = MutableSharedFlow<Quest>()
    val markInProgressFLow: CFlow<Quest> = _markInProgressFLow.toCommonFlow()

    fun fetchHomeState() {
        viewModelScope.launch {
            fetchHomePageUseCase.fetchHomePage().collectLatest { result ->
                _dashboardState.update {
                    it.copy(
                        dashboardResponse = result,
                        shouldShowCongratsCard = prefs.shouldShowCongratsCardInQuests()
                    )
                }
                if (result.status != RestClientResult.Status.LOADING) {
                    val currentQuest = result.data?.data?.getCurrentQuestPairForEvent()
                    fireShownAnalyticsEvent(
                        mapOf(
                            QuestEventKey.Properties.level_number to currentQuest?.first.orZero()
                                .toString(),
                            QuestEventKey.Properties.level_type to currentQuest?.second?.type.orEmpty()
                        )
                    )
                }
            }
        }
    }

    init {
        observeNetwork()
    }

    fun observeNetwork() {
        networkJob?.cancel()
        networkJob = viewModelScope.launch {
            networkFlow.networkStatus.collect { bool ->
                _dashboardState.update {
                    it.copy(
                        shouldShowInternetNotWorking = !bool
                    )
                }
            }
        }
    }
    fun markGameInProgress(quest: Quest) {
        viewModelScope.launch {
            markGameInProgressUseCase.markGameInProgress(quest.type.orEmpty()).collect(
                onSuccessWithNullData = {
                    _markInProgressFLow.emit(quest)
                },
                onSuccess = {
                    _markInProgressFLow.emit(quest)
                }
            )
        }
    }

    fun onShownCongratsCard() {
        prefs.setShouldShowCongratsCardInQuests(false)
        _dashboardState.update {
            it.copy(
                shouldShowCongratsCard = false
            )
        }
    }

    fun fireShownAnalyticsEvent(analyticsData: Map<String, String>) {
        analyticsApi.postEvent(
            QuestEventKey.Events.Shown_QuestHomepage,
            buildMapForAnalytics().apply {
                analyticsData.forEach {
                    this[it.key] = it.value
                }
            }
        )
    }

    fun fireClickedAnalyticsEvent(analyticsData: Map<String, String>) {
        analyticsApi.postEvent(
            QuestEventKey.Events.Clicked_QuestHomepage,
            buildMapForAnalytics().apply {
                analyticsData.forEach {
                    this[it.key] = it.value
                }
            }
        )
    }

    fun buildMapForAnalytics(): MutableMap<String, String> {
        var levelUnlocked = ""
        var levelCompleted = ""
        var levelPending = ""
        var levelLocked = ""
        var level3Status = ""
        _dashboardState.value.dashboardResponse?.data?.data?.quests?.forEachIndexed { index, quest ->
            if (quest.getStatusEnum() == QuestStatus.UNLOCKED) {
                levelUnlocked += "${index+1} ,"
            }
            if (quest.getStatusEnum() == QuestStatus.IN_PROGRESS) {
                levelPending += "${index+1} ,"
            }
            if (quest.getStatusEnum() == QuestStatus.COMPLETED) {
                levelCompleted += "${index+1} ,"
            }
            if (quest.getStatusEnum() == QuestStatus.LOCKED) {
                levelLocked += "${index+1} ,"
            }
            if (quest.type.orEmpty() == QUEST_CLICK_ACTION.TXN_GAME.name) {
                level3Status = quest.cardDetails?.questCardCta?.title.orEmpty()
            }
        }
        return mutableMapOf<String, String>(
            QuestEventKey.Properties.levels_completed to levelCompleted,
            QuestEventKey.Properties.levels_pending to levelPending,
            QuestEventKey.Properties.levels_unlocked to levelUnlocked,
            QuestEventKey.Properties.levels_locked to levelLocked,
            QuestEventKey.Properties.level3_status to level3Status
        )
    }
}