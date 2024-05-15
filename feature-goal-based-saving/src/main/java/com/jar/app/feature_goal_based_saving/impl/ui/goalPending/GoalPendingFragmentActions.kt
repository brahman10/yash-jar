package com.jar.app.feature_goal_based_saving.impl.ui.goalPending

internal sealed class GoalPendingFragmentActions {
    data class Init(val serializedData: String): GoalPendingFragmentActions()
    object OnClickOnBanner: GoalPendingFragmentActions()
}