package com.jar.app.feature_goal_based_saving.impl.ui.endGoal

import com.jar.app.feature_goal_based_saving.shared.data.model.EndScreenResponse

internal sealed class EndGoalFragmentState {
    object Loading: EndGoalFragmentState()
    data class OnData(val data: EndScreenResponse): EndGoalFragmentState()
    object OnClickOnEndGoal: EndGoalFragmentState()
    object OnClickOnContinue: EndGoalFragmentState()
    object OnClickOnClose: EndGoalFragmentState()
}