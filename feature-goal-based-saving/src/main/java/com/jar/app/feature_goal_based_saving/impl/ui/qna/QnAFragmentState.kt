package com.jar.app.feature_goal_based_saving.impl.ui.qna

data class QnAFragmentState(
    val GoalEndResponse: String? = null,
    val selectedMessage: Pair<String, Int>? = null,
)

data class OnNavigateToEndGoalScreenRequestBody(
    val endStateResponse: String,
    val goalId: String
)
