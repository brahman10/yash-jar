package com.jar.app.feature_goal_based_saving.impl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SuperSharedViewModel: ViewModel() {
    private val _state = MutableStateFlow(
        GoalBasedSavingState()
    )
    val state:StateFlow<GoalBasedSavingState> = _state

    private val _navigateToFlow = MutableSharedFlow<Int?>()
    val navigateTo: SharedFlow<Int?> = _navigateToFlow

    private val _navigateWithDirection = MutableSharedFlow<NavDirections?>()
    val navigateWithDirection: SharedFlow<NavDirections?> = _navigateWithDirection

    private val _navigateToDeepLink = MutableSharedFlow<String?>()
    val navigateToDeepLink: SharedFlow<String?> = _navigateToDeepLink

    private val _hideAppBar = MutableSharedFlow<Boolean?>()
    val hideAppBar: SharedFlow<Boolean?> = _hideAppBar

     var navigateToPaymentSuccessScreen = MutableSharedFlow<String?>(0)
   // val navigateToPaymentSuccessScreen: SharedFlow<String?> = _navigateToPaymentSuccessScreen

    private val _onPopPayment = MutableSharedFlow<Boolean?>()
    val onPopPayment: SharedFlow<Boolean?> = _onPopPayment

    private val _popBackStack = MutableSharedFlow<Unit?>()
    val popBackStack: SharedFlow<Unit?> = _popBackStack

    fun handleActions(actions: GoalBasedSavingActions) {
        viewModelScope.launch(Dispatchers.IO) {
            when (actions) {
                is GoalBasedSavingActions.NavigateTo -> {
                    viewModelScope.launch {
                        _navigateToFlow.emit(actions.navigateTo)
                    }
                }
                is GoalBasedSavingActions.NavigateWithDirection -> {
                    viewModelScope.launch {
                        _navigateWithDirection.emit(actions.navigateDirection)
                    }
                }
                is GoalBasedSavingActions.NavigatetoDeeplink -> {
                    viewModelScope.launch {
                        _navigateToDeepLink.emit(
                            actions.deeplink
                        )
                    }
                }
                is GoalBasedSavingActions.HideAppBar -> {
                    viewModelScope.launch {
                        _hideAppBar.emit(
                            actions.hideAppBar
                        )
                    }
                }
                is GoalBasedSavingActions.NavigateToPaymentSuccessScreen -> {
                    _state.value = _state.value.copy(
                        isShowTransactionScreen = true
                    )
                    viewModelScope.launch {
                        navigateToPaymentSuccessScreen.emit(actions.data)
                        navigateToPaymentSuccessScreen = MutableSharedFlow(1)
                    }
                }
                is GoalBasedSavingActions.OnPopupPayment -> {
                    viewModelScope.launch {
                        _onPopPayment.emit(true)
                    }
                }
                is GoalBasedSavingActions.PopBackStack -> {
                    viewModelScope.launch {
                        _popBackStack.emit(
                            Unit
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}