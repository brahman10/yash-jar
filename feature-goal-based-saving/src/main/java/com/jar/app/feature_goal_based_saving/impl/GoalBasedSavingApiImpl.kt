package com.jar.app.feature_goal_based_saving.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_goal_based_saving.api.GoalBasedSavingApi
import dagger.Lazy
import javax.inject.Inject

internal class GoalBasedSavingApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : GoalBasedSavingApi, BaseNavigation {

    val navController by lazy {
        navControllerRef.get()
    }

    override fun openGoalBasedSaving() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/savingsGoal"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openGoalBasedSavingSettings() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/savingsGoalSettings"),
            getNavOptions(shouldAnimate = true)
        )
    }

}