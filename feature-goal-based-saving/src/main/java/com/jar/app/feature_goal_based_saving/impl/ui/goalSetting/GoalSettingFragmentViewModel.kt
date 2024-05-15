package com.jar.app.feature_goal_based_saving.impl.ui.goalSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.data.model.ProgressStatus
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchGBSSettingsReponse
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class GoalSettingFragmentViewModel @Inject constructor(
    private val fetchGBSSettingsReponse: FetchGBSSettingsReponse,
    private val serializer: Serializer,
    private var remoteConfigApi: RemoteConfigApi,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state: MutableStateFlow<GoalSettingFragmentState> = MutableStateFlow(
        GoalSettingFragmentState()
    )
    val state: StateFlow<GoalSettingFragmentState> = _state

    private val _loading = MutableSharedFlow<Boolean>()
    val loading: SharedFlow<Boolean> = _loading

    private val _onEndGoal = MutableSharedFlow<String?>()
    val onEndGoal: SharedFlow<String?> = _onEndGoal

    private val _onTrackGoal = MutableSharedFlow<String?>()
    val onTrackGoal: SharedFlow<String?> = _onTrackGoal

    private val _onContactUs = MutableSharedFlow<String?>()
    val onContactUs:SharedFlow<String?> = _onContactUs

    private val _onClickOnPendingBanner = MutableSharedFlow<String?>()
    val onClickOnPendingBanner: SharedFlow<String?> = _onClickOnPendingBanner

    private val _onNavigateGoalCompletionScreen = MutableSharedFlow<Pair<String, String>?>()
    val onNavigateGoalCompletionScreen = _onNavigateGoalCompletionScreen

    private val _onNavigateGoalSetupScreen = MutableSharedFlow<Unit?>()
    val onNavigateGoalSetupScreen: SharedFlow<Unit?> = _onNavigateGoalSetupScreen

    private val _onBackButtonClick = MutableSharedFlow<Unit?>()
    val onBackButtonClick = _onBackButtonClick



    fun handleAction(action: GoalSettingFragmentActions) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                is GoalSettingFragmentActions.Init -> {
                    fetchSettings()
                }

                is GoalSettingFragmentActions.OnClickOnContactUs -> {
                    val number = remoteConfigApi.getWhatsappNumber()
                    viewModelScope.launch {
                        _onContactUs.emit(
                            number
                        )
                    }
                }

                is GoalSettingFragmentActions.OnClickOnEndGoal -> {
                    val eventMap = hashMapOf<String,Any>()
                    _state.value.onData?.goalCompletedResponse?.activeResponse?.goalDetails?.details?.forEachIndexed {i, item ->
                        eventMap[item.key ?: ""] = item.value ?: ""
                    }
                    val additionalAttributes = mapOf(
                        "screen_type" to "Settings Screen",
                        "setupdetailsstatus" to (_state.value.onData?.progressStatus ?: ""),
                        "buttonclicked" to "End goal"
                    )
                    val combinedMap = eventMap + additionalAttributes
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        combinedMap
                    )
                    viewModelScope.launch {
                        _onEndGoal.emit(
                            action.goalId
                        )
                    }
                }

                GoalSettingFragmentActions.OnClickOnTrackGoal -> {
                    val eventMap = hashMapOf<String,Any>()
                    _state.value.onData?.goalCompletedResponse?.activeResponse?.goalDetails?.details?.forEachIndexed {i, item ->
                        eventMap[item.key ?: ""] = item.value ?: ""
                    }
                    val additionalAttributes = mapOf(
                        "screen_type" to "Settings Screen",
                        "setupdetailsstatus" to (_state.value.onData?.progressStatus ?: ""),
                        "buttonclicked" to "Track my goal"
                    )
                    val combinedMap = eventMap + additionalAttributes
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        combinedMap
                    )
                    viewModelScope.launch {
                        _onTrackGoal.emit(_state.value.onData?.progressResponse?.trackGoalButton?.deeplink ?:"")
                    }
                }

                is GoalSettingFragmentActions.OnClickOnPendingBanner -> {
                    viewModelScope.launch {
                        _onClickOnPendingBanner.emit(
                            action.deepLink
                        )
                    }
                }

                GoalSettingFragmentActions.OnClickBack -> {}
                GoalSettingFragmentActions.OnClickOnChevron -> {
                    val eventMap = hashMapOf<String,Any>()
                    _state.value.onData?.goalCompletedResponse?.activeResponse?.goalDetails?.details?.forEachIndexed {i, item ->
                        eventMap[item.key ?: ""] = item.value ?: ""
                    }
                    val additionalAttributes = mapOf(
                        "screen_type" to "Settings Screen",
                        "setupdetailsstatus" to (_state.value.onData?.progressStatus ?: ""),
                        "buttonclicked" to "Auto save details"
                    )
                    val combinedMap = eventMap + additionalAttributes
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        combinedMap
                    )
                }
            }
        }
    }

    private fun fetchSettings() {
        viewModelScope.launch {
            fetchGBSSettingsReponse.execute().collect(
                onLoading = {
                    viewModelScope.launch {
                        _loading.emit(
                            true
                        )
                    }
                },
                onSuccess = {
                    when (ProgressStatus.fromString(it.progressStatus)) {
                        ProgressStatus.ACTIVE -> {
                            analyticsHandler.postEvent(
                                SavingsGoal_ScreenShown,
                                mapOf(
                                    screen_type to "Settings Screen",
                                    "setupdetailsstatus" to "Active"
                                )
                            )
                            viewModelScope.launch {
                                _loading.emit(
                                    false
                                )
                            }
                            _state.value = _state.value.copy(
                                onData = it
                            )
                        }

                        ProgressStatus.IN_PROGRESS -> {
                            _loading.emit(
                                false
                            )
                            _state.value = _state.value.copy(
                                onData = it
                            )
                        }

                        ProgressStatus.COMPLETED -> {
                            val data =
                                serializer.encodeToString(it.goalCompletedResponse?.endStateResponse)
                            viewModelScope.launch {
                                _loading.emit(
                                    false
                                )
                            }
                            viewModelScope.launch {
                                _onNavigateGoalCompletionScreen.emit(
                                    Pair(data, it.goalId ?: "")
                                )
                            }

                        }

                        ProgressStatus.SETUP -> {
                            analyticsHandler.postEvent(
                                SavingsGoal_ScreenShown,
                                mapOf(
                                    screen_type to "Settings Screen",
                                    "setupdetailsstatus" to "Setup state"
                                )
                            )
                            viewModelScope.launch {
                                _loading.emit(
                                    false
                                )
                            }
                            viewModelScope.launch {
                                _onNavigateGoalSetupScreen.emit(
                                    Unit
                                )
                            }
                        }
                        else -> Unit
                    }
                },
                onError = { _, _ ->
                    viewModelScope.launch {
                        _loading.emit(
                            false
                        )
                    }
                }
            )
        }
    }
}
