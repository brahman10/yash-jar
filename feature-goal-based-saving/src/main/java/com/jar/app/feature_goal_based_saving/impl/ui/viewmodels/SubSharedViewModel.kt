package com.jar.app.feature_goal_based_saving.impl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.facebook.internal.Mutable
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingState
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.GoalSelectionScreen
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.GoalSelectionScreenV2
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.Goalselect
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.goalselected
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.clickaction
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
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
class SubSharedViewModel @Inject constructor(
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state = MutableStateFlow(
        GoalBasedSavingState()
    )
    val state: StateFlow<GoalBasedSavingState> = _state

    private val _scrollToEndFlow: MutableSharedFlow<Unit?> = MutableSharedFlow()
    val scrollToEndFlow: SharedFlow<Unit?> = _scrollToEndFlow

    private val _navigateToFlow = MutableSharedFlow<Int?>()
    val navigateTo: SharedFlow<Int?> = _navigateToFlow

    private val _navigateWithDirection = MutableSharedFlow<NavDirections?>()
    val navigateWithDirection: SharedFlow<NavDirections?> = _navigateWithDirection

    private val _popUserEntryFragment = MutableSharedFlow<Unit?>()
    val popUserEntryFragment: SharedFlow<Unit?> = _popUserEntryFragment

    private val _currentStep = MutableSharedFlow<GOAL_BASED_SAVING_STEPS>()
    val currentStep: SharedFlow<GOAL_BASED_SAVING_STEPS> = _currentStep

    private val _onCustomButtonSheet = MutableSharedFlow<Boolean>()
    val onCustomButtonSheet: SharedFlow<Boolean> = _onCustomButtonSheet

    var shouldNavigateForward = true

    fun handleActions(actions: GoalBasedSavingActions) {
        viewModelScope.launch(Dispatchers.IO) {
            when (actions) {
                is GoalBasedSavingActions.NavigateTo -> {
                    viewModelScope.launch {
                        _navigateToFlow.emit(
                            actions.navigateTo
                        )
                    }
                }
                is GoalBasedSavingActions.OnStepChange -> {
                    viewModelScope.launch {
                        _currentStep.emit(
                            actions.currentStep
                        )
                    }
                }
                is GoalBasedSavingActions.OnGoalTitleChange -> {
                    viewModelScope.launch {
                        _state.value = _state.value.copy(
                            onGoalTitleChange = actions.title
                        )
                    }
                }
                is GoalBasedSavingActions.OnGoalSelectedFromList -> {
                    if (actions.saveForModel.name.isNullOrEmpty().not()) {
                        analyticsHandler.postEvent(
                            SavingsGoal_ScreenClicked,
                            mapOf(
                                screen_type to GoalSelectionScreenV2,
                                clickaction to Goalselect,
                                goalselected to actions.saveForModel.name!!
                            )
                        )
                    }
                    _state.value = _state.value.copy(
                        onGoalSelectedFromList = actions.saveForModel
                    )
                }
                is GoalBasedSavingActions.OnAmountChanged -> {
                    _state.value = _state.value.copy(
                        onDurationChanged = null,
                        onAmountChanged = actions.amount
                    )
                }
                is GoalBasedSavingActions.OnDurationChanged -> {
                    _state.value = _state.value.copy(
                        onDurationChanged = actions.duration
                    )
                }
                is GoalBasedSavingActions.NavigateWithDirection -> {
                    viewModelScope.launch {
                        _navigateWithDirection.emit(
                            actions.navigateDirection
                        )
                    }
                }

                GoalBasedSavingActions.PopUserEntryFragment -> {
                    viewModelScope.launch {
                        _popUserEntryFragment.emit(
                            Unit
                        )
                    }
                }
                is GoalBasedSavingActions.SendClickOnEditBar -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            "screen_type" to actions.screen_type,
                            "clickaction" to actions.clickaction,
                        )
                    )
                }
                is GoalBasedSavingActions.ScrollToEnd -> {
                    viewModelScope.launch {
                        _scrollToEndFlow.emit(Unit)
                    }
                }
                is GoalBasedSavingActions.OnFragmentHostContainerHeight -> {
                    _state.value = _state.value.copy(
                        userEntryFragmentHeight = actions.height
                    )
                }
                GoalBasedSavingActions.OnDismissCustomGoalNameBottomSheet-> {
                    _onCustomButtonSheet.emit(
                        true
                    )
                }
                else -> {}
            }

        }
    }
}
 enum class GOAL_BASED_SAVING_STEPS {
     GOAL_NAME,
     GOAL_AMOUNT,
     GOAL_DURATION
 }