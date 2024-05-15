package com.jar.app.feature_goal_based_saving.impl.ui

internal sealed class GoalBasedSavingFragmentActions {
    object Init: GoalBasedSavingFragmentActions()
    data class OnBackIconClicked(val screenName: String): GoalBasedSavingFragmentActions()
}