package com.jar.app.feature_goal_based_saving.impl.ui.endGoal

internal sealed class EndGoalFragmentAction {
    object Init: EndGoalFragmentAction()
    object OnClickOnEndGoal: EndGoalFragmentAction()
    object OnClickOnContinueGoal: EndGoalFragmentAction()
    object OnClickOnClose: EndGoalFragmentAction()
}