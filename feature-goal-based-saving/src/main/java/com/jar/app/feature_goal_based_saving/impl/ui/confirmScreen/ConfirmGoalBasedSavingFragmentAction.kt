package com.jar.app.feature_goal_based_saving.impl.ui.confirmScreen

internal sealed class ConfirmGoalBasedSavingFragmentAction {
    data class Init(val args: ConfirmGoalBasedSavingFragmentArgs): ConfirmGoalBasedSavingFragmentAction()
    data class OnClickOnContinue(val args: ConfirmGoalBasedSavingFragmentArgs): ConfirmGoalBasedSavingFragmentAction()
    data class OnClickOnInfo(val args: ConfirmGoalBasedSavingFragmentArgs): ConfirmGoalBasedSavingFragmentAction()
}