package com.jar.app.feature_goal_based_saving.impl.ui.goalSetting

internal sealed class GoalSettingFragmentActions {
    object Init: GoalSettingFragmentActions()
    data class OnClickOnEndGoal(val goalId: String): GoalSettingFragmentActions()
    object OnClickOnTrackGoal: GoalSettingFragmentActions()
    object OnClickOnContactUs: GoalSettingFragmentActions()
    data class OnClickOnPendingBanner(val deepLink: String): GoalSettingFragmentActions()
    object OnClickBack: GoalSettingFragmentActions()
    object OnClickOnChevron: GoalSettingFragmentActions()

}