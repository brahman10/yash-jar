package com.jar.app.feature_goal_based_saving.impl.ui.confirmScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.clickaction
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalRequest
import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalResponse
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.CreateGoalUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchMergeGoalScreenUseCase
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
internal class ConfirmGoalBasedSavingFragmentViewModel @Inject constructor(
    private val fetchMergeGoalScreenUseCase: FetchMergeGoalScreenUseCase,
    private val createGoalUseCase: CreateGoalUseCase,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {

    private val _state = MutableStateFlow(ConfirmGoalBasedSavingFragmentState())
    val state: StateFlow<ConfirmGoalBasedSavingFragmentState> = _state

    private val _loading = MutableSharedFlow<Boolean>()
    val loading: SharedFlow<Boolean> = _loading

    private val _onClickContinue = MutableSharedFlow<Unit?>()
    val onClickContinue = _onClickContinue

    private val _goalCreated = MutableSharedFlow<CreateGoalResponse>()
    val goalCreated = _goalCreated

    private fun fetchConfirmGoalResponse() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchMergeGoalScreenUseCase.execute().collect(
                onLoading = {
                    viewModelScope.launch {
                        _loading.emit(true)
                    }
                },
                onSuccess = {
                    viewModelScope.launch {
                        _loading.emit(false)
                    }
                    _state.value = _state.value.copy(
                        mergeGoalResponse = it
                    )
                },
                onError = {_,_->
                    viewModelScope.launch {
                        _loading.emit(false)
                    }
                }
            )
        }
    }

    fun handleAction(action: ConfirmGoalBasedSavingFragmentAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when(action) {
                is ConfirmGoalBasedSavingFragmentAction.OnClickOnContinue -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Mandate Screen",
                            clickaction to "Proceed click",
                            "savingsgoalamount" to action.args.savingGoalAmount,
                            "roundoffamount" to action.args.roundOffAmount.toLong()
                        )
                    )
                    viewModelScope.launch {
                        createGoalUseCase.execute(
                            createGoalRequest = CreateGoalRequest(
                                name = _state.value.goalName,
                                amount = _state.value.goalAmount,
                                duration = _state.value.goalDuration,
                                image = _state.value.goalImage
                            )
                        ).collect(
                            onLoading = {
                                viewModelScope.launch {
                                    _loading.emit(
                                        true
                                    )
                                }
                            },
                            onSuccess = {
                                viewModelScope.launch {
                                    _loading.emit(
                                        false
                                    )
                                }
                                viewModelScope.launch {
                                    _goalCreated.emit(
                                        it
                                    )
                                }

                            },
                            onError = {_, _ ->
                                viewModelScope.launch {
                                    _loading.emit(
                                        false
                                    )
                                }
                            }
                        )
                    }
                }

                is ConfirmGoalBasedSavingFragmentAction.Init -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenShown,
                        mapOf(
                            screen_type to "Mandate Screen",
                            "savingsgoalamount" to action.args.savingGoalAmount,
                            "roundoffamount" to action.args.roundOffAmount.toLong()
                        )
                    )
                    viewModelScope.launch {
                        _loading.emit(
                            false
                        )
                    }
                    _state.value = _state.value.copy(
                        goalName = action.args.goalName,
                        goalAmount = action.args.goalAmount,
                        goalDuration = action.args.duration,
                        mandateAmount = action.args.totalAmount.toLong(),
                        goalImage = action.args.goalImage
                    )
                }

                is ConfirmGoalBasedSavingFragmentAction.OnClickOnInfo -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenShown,
                        mapOf(
                            screen_type to "Mandate Screen",
                            clickaction to "info click",
                            "savingsgoalamount" to action.args.goalAmount,
                            "roundoffamount" to action.args.totalAmount.toLong()
                        )
                    )
                }

            }
        }
    }

    init {
        fetchConfirmGoalResponse()
    }

}