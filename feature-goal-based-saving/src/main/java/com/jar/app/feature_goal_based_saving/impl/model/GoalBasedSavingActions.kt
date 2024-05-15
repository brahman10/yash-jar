package com.jar.app.feature_goal_based_saving.impl.model

import androidx.navigation.NavDirections
import com.jar.app.feature_goal_based_saving.impl.ui.GoalBasedSavingFragmentActions
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedItem

sealed class GoalBasedSavingActions {
    data class NavigateTo(val navigateTo: Int): GoalBasedSavingActions()
    data class NavigateWithDirection(val navigateDirection: NavDirections): GoalBasedSavingActions()
    data class OnStepChange(val currentStep: com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.GOAL_BASED_SAVING_STEPS): GoalBasedSavingActions()
    data class OnGoalTitleChange(val title: String): GoalBasedSavingActions()
    data class OnGoalSelectedFromList(val saveForModel: GoalRecommendedItem): GoalBasedSavingActions()
    data class OnAmountChanged(val amount: String): GoalBasedSavingActions()
    data class OnDurationChanged(val duration: Int?): GoalBasedSavingActions()
    object PopUserEntryFragment: GoalBasedSavingActions()
    data class NavigatetoDeeplink(val deeplink:String): GoalBasedSavingActions()
    data class HideAppBar(val hideAppBar: Boolean): GoalBasedSavingActions()
    object MadeAppBarTransparent: GoalBasedSavingActions()
    data class NavigateToPaymentSuccessScreen(val data: String): GoalBasedSavingActions()
    object OnPopupPayment: GoalBasedSavingActions()
    data class SendClickOnEditBar(
        val screen_type: String,
        val clickaction: String,
    ): GoalBasedSavingActions()

    object ScrollToEnd: GoalBasedSavingActions()
    data class OnFragmentHostContainerHeight(val height: Int) : GoalBasedSavingActions()
    object PopBackStack: GoalBasedSavingActions()
    object OnDismissCustomGoalNameBottomSheet: GoalBasedSavingActions()
}