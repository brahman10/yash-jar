package com.jar.app.feature_goal_based_saving.impl.ui.confirmScreen

import com.jar.app.feature_goal_based_saving.shared.data.model.MergeGoalResponse

internal data class ConfirmGoalBasedSavingFragmentState (
    val goalName: String? = null,
    val goalAmount: Long? = null,
    val goalDuration: Int? = null,
    val mergeGoalResponse: MergeGoalResponse? = null,
    val mandateAmount: Long? = null,
    val goalImage: String? = null,
)