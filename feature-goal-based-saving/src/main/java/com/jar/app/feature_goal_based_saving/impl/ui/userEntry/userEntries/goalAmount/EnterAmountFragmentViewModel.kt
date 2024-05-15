package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalAmount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalAmountResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.MandateInfo
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchGoalAmountScreenDetailsUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchMandateInfoUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EnterAmountFragmentViewModel @Inject constructor(
    private val fetchGoalAmountScreenDetailsUseCase: FetchGoalAmountScreenDetailsUseCase,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state = MutableStateFlow(EnterAmountFragmentState())
    val state: StateFlow<EnterAmountFragmentState> = _state

    fun handelAction(enterAmountFragmentAction: EnterAmountFragmentAction) {
        when(enterAmountFragmentAction) {
            EnterAmountFragmentAction.Init -> {
                fetch()
                analyticsHandler.postEvent(
                    GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown,
                    mapOf(
                        screen_type to "Amount screen"
                    )
                )
            }
            is EnterAmountFragmentAction.SentAmountChangedEvent -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenClicked,
                    mapOf(
                        screen_type to enterAmountFragmentAction.screenType,
                        "action" to enterAmountFragmentAction.action,
                        "errormessageshown" to enterAmountFragmentAction.errorMessageShown
                    )
                )
            }
            is EnterAmountFragmentAction.OnNextButtonClicked -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenClicked,
                    mapOf(
                        screen_type to "Amount Screen",
                        "action" to "click next",
                        "amount" to enterAmountFragmentAction.amount
                    )
                )
            }

            else -> {}
        }
    }

    private fun fetch() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchGoalAmountScreenDetailsUseCase.execute().collect(
                onLoading = {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                },
                onSuccess = {
                    _state.value = _state.value.copy(
                        loading = false,
                        goalAmountResponse = it
                    )
                },
                onError = { _,_ ->
                    _state.value = _state.value.copy(
                        loading = false
                    )
                }
            )
        }
    }
}