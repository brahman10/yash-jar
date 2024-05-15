package com.jar.app.feature_goal_based_saving.impl.ui.endGoal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchEndGoalScreenResponseUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EndGoalFragmentViewModel @Inject constructor(
    private val fetchEndGoalScreenResponseUseCase: FetchEndGoalScreenResponseUseCase,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state: MutableStateFlow<EndGoalFragmentState> = MutableStateFlow(EndGoalFragmentState.Loading)
    val state: StateFlow<EndGoalFragmentState> = _state

    fun handleAction(action: EndGoalFragmentAction) {
        when(action) {
            EndGoalFragmentAction.Init -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenShown,
                    mapOf(
                        screen_type to "End Goal confirmation screen"
                    )
                )
                fetch()
            }
            EndGoalFragmentAction.OnClickOnContinueGoal -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenClicked,
                    mapOf(
                        screen_type to "End Goal confirmation screen",
                        "buttonclicked" to "Continue Goal",
                    )
                )
                _state.value = EndGoalFragmentState.OnClickOnContinue
            }
            EndGoalFragmentAction.OnClickOnEndGoal -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenClicked,
                    mapOf(
                        screen_type to "End Goal confirmation screen",
                        "buttonclicked" to "End Goal",
                    )
                )
                _state.value = EndGoalFragmentState.OnClickOnEndGoal
            }

            EndGoalFragmentAction.OnClickOnClose -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenClicked,
                    mapOf(
                        screen_type to "End Goal confirmation screen",
                        "buttonclicked" to "Cross clicked",
                    )
                )
                _state.value = EndGoalFragmentState.OnClickOnClose
            }
        }
    }

    private fun fetch() {
        viewModelScope.launch {
            fetchEndGoalScreenResponseUseCase.execute().collect(
                onLoading = {
                    _state.value = EndGoalFragmentState.Loading
                },
                onSuccess = {
                    _state.value = EndGoalFragmentState.OnData(it)
                },
                onError = {it, it1 ->

                }
            )
        }
    }
}