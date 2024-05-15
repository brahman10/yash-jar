package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalName

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.clickaction
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalFirstQuestionResponse
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchGoalNameScreenDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EnterTitleFragmentViewModel @Inject constructor(
    private val fetchGoalNameScreenDetails: FetchGoalNameScreenDetails,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {

    var maxLength = 20
    var minLength = 3
    var questionName = ""
    var nameInputText = ""
    var goalIcon = ""

    private val _into = MutableLiveData<RestClientResult<ApiResponseWrapper<GoalFirstQuestionResponse>>>()
    val intro: LiveData<RestClientResult<ApiResponseWrapper<GoalFirstQuestionResponse>>>
        get() = _into

    fun fetch() {
        viewModelScope.launch {
            val data = fetchGoalNameScreenDetails.execute()
            data.collect() {
                _into.postValue(it)
            }
        }
    }

    fun handelAction(action: EnterTitleFragmentAction) {
        when(action) {
            EnterTitleFragmentAction.Init -> {
                fetch()
            }
            EnterTitleFragmentAction.SendShownEvent -> {
                analyticsHandler.postEvent(
                    GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown,
                    mapOf(
                        screen_type to GBSAnalyticsConstants.GoalNameScreen.GoalSelectionScreenV2
                    )
                )
            }
            is EnterTitleFragmentAction.OnClickedOnNext -> {
                analyticsHandler.postEvent(
                    SavingsGoal_ScreenClicked,
                    mapOf(
                        screen_type to action.screenType,
                        clickaction to action.clickAction,
                        "goalselectionprocess" to action.goalselectionprocess,
                        "finalgoalselected" to action.finalgoalselected
                    )
                )
            }
        }
    }
}