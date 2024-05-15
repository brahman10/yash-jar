package com.jar.app.feature_goal_based_saving.impl.model

import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.GOAL_BASED_SAVING_STEPS
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedItem
import java.util.concurrent.atomic.AtomicBoolean

data class GoalBasedSavingState (
    val onGoalTitleChange: String? = null,
    val onGoalSelectedFromList: GoalRecommendedItem? = null,
    val onAmountChanged: String? = null,
    val onDurationChanged: Int? = null,
    var defaultGoalImage: String?=null,
    val isShowTransactionScreen: Boolean? = false,
    var isCallHomeFeedApi: AtomicBoolean = AtomicBoolean(true),
    var isNavigateToMergePlan: AtomicBoolean = AtomicBoolean(false),
    val userEntryFragmentHeight: Int? = null
)
