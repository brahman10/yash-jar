package com.jar.app.feature_goal_based_saving.impl.ui.manage

import com.jar.app.feature_goal_based_saving.shared.data.model.HomefeedGoalProgressReponse

internal sealed class ManageGoalFragmentActions {
    data class Init(val data: String): ManageGoalFragmentActions()
    data class OnData(val data: HomefeedGoalProgressReponse): ManageGoalFragmentActions()
    object OnOpenSettings: ManageGoalFragmentActions()
    object OnGoalCompleted: ManageGoalFragmentActions()
    object OnClickOnDailySavingRestart: ManageGoalFragmentActions()
    object SendSaveNowAnalyticEvent: ManageGoalFragmentActions()
    object OnFetchUserMandateInfo: ManageGoalFragmentActions()
    data class UpdateOnEnableDailySavings(val amount: Float): ManageGoalFragmentActions()
    object EnableAutomaticDailySavings: ManageGoalFragmentActions()
}