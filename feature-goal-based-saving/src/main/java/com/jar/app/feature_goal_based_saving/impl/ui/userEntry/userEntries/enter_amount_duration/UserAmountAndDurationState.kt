package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.enter_amount_duration

import com.jar.app.feature_goal_based_saving.shared.data.model.GoalAmountResponse

internal data class UserAmountAndDurationState (
    val goalAmountResponse: GoalAmountResponse?= null,
    val loading: Boolean = false
)