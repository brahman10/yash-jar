package com.jar.app.feature_goal_based_saving.impl.ui.manage

import com.jar.app.feature_goal_based_saving.shared.data.model.EndStateResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.HomefeedGoalProgressReponse

internal data class ManageGoalFragmentState(
    val data: HomefeedGoalProgressReponse? = null,
    val endScreenResponse: EndStateResponse? = null,
    val showEndState: Boolean? = null,
)