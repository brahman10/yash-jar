package com.jar.app.feature_goal_based_saving.impl.ui.abandonSheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_ui.util.unfoldNetworkResponse
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.clickaction
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchAbandonScreenResponseUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AbandonScreenFragmentViewModel @Inject constructor(
    private val fetchAbandonScreenResponseUseCase: FetchAbandonScreenResponseUseCase,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state: MutableStateFlow<AbandonScreenFragmentState?> = MutableStateFlow(null)
    val state: StateFlow<AbandonScreenFragmentState?> = _state

    private fun fetch(){
        viewModelScope.launch(Dispatchers.IO) {
            fetchAbandonScreenResponseUseCase.execute().collect {
                it.unfoldNetworkResponse(
                    onLoading = {
                        _state.value = AbandonScreenFragmentState.onLoading
                    },
                    onSuccess = {
                        _state.value = AbandonScreenFragmentState.OnData(
                            it
                        )
                    },
                    onSuccessWithNullData = {

                    }
                )
            }
        }
    }
    fun handleActions(action: AbandonScreenFragmentAction) {
        when(action) {
            AbandonScreenFragmentAction.OnClickOnClose -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenClicked,
                    mapOf(
                        screen_type to "Abandon Bottom Sheet",
                        clickaction to "Cross click"
                    )
                )
                _state.value = AbandonScreenFragmentState.OnClose
            }
            is AbandonScreenFragmentAction.OnClickOnExit -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenClicked,
                    mapOf(
                        screen_type to "Abandon Bottom Sheet",
                        clickaction to "I'll do it later"
                    )
                )
                _state.value = AbandonScreenFragmentState.OnIWllDoItLater(
                    action.deepLink
                )
            }

            AbandonScreenFragmentAction.Init -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenShown,
                    mapOf(
                        screen_type to "Abandon Bottom Sheet"
                    )
                )
                fetch()
            }

            AbandonScreenFragmentAction.OnClickOnContinue -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenClicked,
                    mapOf(
                        screen_type to "Abandon Bottom Sheet",
                        clickaction to "Continue"
                    )
                )
            }
        }

    }

}