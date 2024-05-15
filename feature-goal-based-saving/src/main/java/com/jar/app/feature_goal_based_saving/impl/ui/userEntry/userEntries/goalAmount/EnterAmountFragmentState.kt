package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalAmount

import com.jar.app.feature_goal_based_saving.shared.data.model.CalculateDailyAmountResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalAmountResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalDurationResponse

internal data class EnterAmountFragmentState(
    val goalAmountResponse: GoalAmountResponse?= null,
    val loading: Boolean = false,
)