package com.jar.app.feature_goal_based_saving.impl.ui.goalSuccess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.data.model.Button
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalEndResponse
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.EndScreenViewedUseCase
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
internal class GoalSetupSuccessFragmentViewModel @Inject constructor(
    private val endScreenViewedUseCase: EndScreenViewedUseCase,
    private val serializer: Serializer,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state = MutableStateFlow(GoalSetupSuccessFragmentState())
    val state: StateFlow<GoalSetupSuccessFragmentState> = _state

    private val _loading = MutableSharedFlow<Boolean>()
    val loading: SharedFlow<Boolean> = _loading

    private val _onWithDraw = MutableSharedFlow<String?>()
    val onWithDraw: SharedFlow<String?> = _onWithDraw

    private val _onContinue = MutableSharedFlow<String?>()
    val onContinue: SharedFlow<String?> = _onContinue

    fun handleActions(action: GoalSetupSuccessFragmentActions) {
        viewModelScope.launch(Dispatchers.IO) {
            when(action) {
                is GoalSetupSuccessFragmentActions.Init -> {
                    val response = action.data?.let {
                        serializer.decodeFromString<GoalEndResponse>(it)
                    }
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenShown,
                        mapOf(
                            screen_type to "Goal Status Screen",
                            "goalstatus" to (response?.header ?: ""),
                            "amountsaved" to (response?.investedAmount ?: "")
                        )
                    )
                    _loading.emit(
                        false
                    )
                    _state.value = _state.value.copy(
                        OnData = response,
                        goalId = action.goalId
                    )
                }
                is GoalSetupSuccessFragmentActions.OnClickOnContinue -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Goal Status Screen",
                            "goalstatus" to (_state.value.OnData?.header ?: ""),
                            "amountsaved" to (_state.value.OnData?.investedAmount ?: ""),
                            "clickaction" to "Start new goal"
                        )
                    )
                    _state.value.goalId?.let {
                        val goalId = it
                        endScreenViewedUseCase.execute(goalId = goalId).collect(
                            onLoading = {
                                _loading.emit(
                                    true
                                )
                            },
                            onSuccess = {
                                _loading.emit(
                                    false
                                )
                                _onContinue.emit(
                                    _state.value.OnData?.newGoalButton?.deeplink
                                )
                            },
                            onSuccessWithNullData = {
                                _loading.emit(
                                    false
                                )
                                _onContinue.emit(
                                    _state.value.OnData?.newGoalButton?.deeplink
                                )
                            },
                            onError = {_, _ ->
                                _loading.emit(
                                    false
                                )
                            }
                        )
                    }
                }
                GoalSetupSuccessFragmentActions.OnClickOnWithdraw -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Goal Status Screen",
                            "goalstatus" to (_state.value.OnData?.header ?: ""),
                            "amountsaved" to (_state.value.OnData?.investedAmount ?: ""),
                            "clickaction" to "Withdraw"
                        )
                    )
                    _state.value.goalId?.let {
                        val goalId = it
                        endScreenViewedUseCase.execute(goalId = goalId).collect(
                            onLoading = {
                                _loading.emit(
                                    true
                                )
                            },
                            onSuccess = {
                                _loading.emit(
                                    false
                                )
                                _onWithDraw.emit(
                                    _state.value.OnData?.withdrawButton?.deeplink
                                )
                            },
                            onSuccessWithNullData = {
                                _loading.emit(
                                    false
                                )
                                _onWithDraw.emit(
                                    _state.value.OnData?.withdrawButton?.deeplink
                                )
                            },
                            onError = {_, _ ->
                                _loading.emit(
                                    false
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun getResponse(): GoalEndResponse {
        return GoalEndResponse(
            title = "Dummy Title",
            header = "Dummy Header",
            goalImage = "https://dummy-url.com/image.jpg",
            goalName = "Dummy Goal Name",
            lottie = "https://dummy-url.com/animation.json",
            investmentHeader = "Dummy Investment Header",
            investedAmount = "$1000",
            timeDesc = "Dummy Time Description",
            newGoalButton = Button(
                text = "Dummy Button Text",
                iconLink = "https://dummy-url.com/icon.png",
                deeplink = "dummy://deeplink"
            ),
            withdrawButton = Button()
        )
    }
}