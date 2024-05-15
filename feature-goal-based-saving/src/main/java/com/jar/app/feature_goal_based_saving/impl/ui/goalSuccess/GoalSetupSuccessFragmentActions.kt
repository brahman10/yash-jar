package com.jar.app.feature_goal_based_saving.impl.ui.goalSuccess

internal sealed class GoalSetupSuccessFragmentActions {
    data class Init(val data: String?, val goalId: String): GoalSetupSuccessFragmentActions()
    object OnClickOnWithdraw: GoalSetupSuccessFragmentActions()
    data class OnClickOnContinue(val deepLink: String): GoalSetupSuccessFragmentActions()
}