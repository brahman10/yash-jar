package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalName

import androidx.lifecycle.ViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.ManualGoalselectionBottomSheetV2
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoalNameBottomSheetViewModel @Inject constructor(
    private val analyticsHandler: AnalyticsApi
) : ViewModel(){
    fun handleAction(action: GoalNameBottomSheetAction) {
        when(action) {
            GoalNameBottomSheetAction.SendShownEvent -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenShown,
                    mapOf(
                        screen_type to ManualGoalselectionBottomSheetV2
                    )
                )
            }
        }
    }
}

sealed class GoalNameBottomSheetAction {
    object SendShownEvent: GoalNameBottomSheetAction()
}