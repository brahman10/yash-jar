package com.jar.app.feature_goal_based_saving.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.shared.data.model.HomefeedGoalProgressReponse
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchHomeFeedResponseUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.UpdateGoalDailyRecurringAmountUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class GoalBasedSavingFragmentViewModel @Inject constructor(
    private val fetchHomeFeedResponseUseCase: FetchHomeFeedResponseUseCase,
    private val UpdateGoalDailyInvestmentStatusUseCase: UpdateGoalDailyRecurringAmountUseCase,
    private val appScope: CoroutineScope,
    private val analyticsApi: com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
    ): ViewModel() {

    private val _data = MutableSharedFlow<HomefeedGoalProgressReponse>()
    val data:SharedFlow<HomefeedGoalProgressReponse> = _data

    private val _loading = MutableSharedFlow<Boolean>()
    val loading: SharedFlow<Boolean> = _loading

    fun handleActions(action: GoalBasedSavingFragmentActions) {
        viewModelScope.launch(Dispatchers.IO) {
            when(action) {
                GoalBasedSavingFragmentActions.Init -> {
                    getGBSData()
                }
                is GoalBasedSavingFragmentActions.OnBackIconClicked -> {
                    analyticsApi.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            GBSAnalyticsConstants.screen_type to action.screenName,
                            GBSAnalyticsConstants.clickaction  to GBSAnalyticsConstants.Back
                        )
                    )
                }
            }
        }
    }

    private fun getGBSData() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchHomeFeedResponseUseCase.fetchHomeFeedResponse().collect(
                onLoading = {
                    viewModelScope.launch {
                        _loading.emit(
                            true
                        )
                    }
                },
                onSuccess = {
                    viewModelScope.launch {
                        _data.emit(
                            it
                        )
                    }
                    viewModelScope.launch {
                        _loading.emit(
                            false
                        )
                    }
                }
            )
        }
    }

    suspend fun updateRecurringAmount(amount: Float) {
        appScope.launch(Dispatchers.IO) {
            UpdateGoalDailyInvestmentStatusUseCase.execute(
                amount
            ).collect(
                onLoading = {
                },
                onSuccess = {},
                onError = { _, _ -> }
            )
        }
    }
}