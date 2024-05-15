package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalName

sealed class EnterTitleFragmentAction {
    object Init: EnterTitleFragmentAction()
    object SendShownEvent: EnterTitleFragmentAction()
    data class OnClickedOnNext(
        val screenType: String,
        val clickAction: String,
        val goalselectionprocess: String,
        val finalgoalselected: String
    ): EnterTitleFragmentAction()
}