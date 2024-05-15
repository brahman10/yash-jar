package com.jar.app.feature_goal_based_saving.impl.ui.transaction

import com.jar.app.feature_goal_based_saving.shared.data.model.GoalStatusResponse

internal data class TransactionFragmentState(
    val goalId: String? = null,
    val OnData: GoalStatusResponse? = null,
)