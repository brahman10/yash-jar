package com.jar.app.feature_goal_based_saving.impl

import androidx.lifecycle.*
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalSavingsIntoPage
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchIntroDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class GoalBasedSavingViewModel @Inject constructor(
    private val fetchIntroPageUseCase: FetchIntroDetailsUseCase,
    private val analyticsHandler: AnalyticsApi
) : ViewModel() {
    private val _introductionScreenStaticData = MutableLiveData<RestClientResult<ApiResponseWrapper<GoalSavingsIntoPage>>>()
    val introductionScreenStaticData: LiveData<RestClientResult<ApiResponseWrapper<GoalSavingsIntoPage>>>
        get() = _introductionScreenStaticData
    fun fetchIntroductionScreenStaticData() {
        viewModelScope.launch {
            fetchIntroPageUseCase.execute().collect() {
                _introductionScreenStaticData.postValue(it)
            }
        }
    }

    fun sendBackButton() {
        analyticsHandler.postEvent(
            GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked,
            mapOf(
                GBSAnalyticsConstants.screen_type to "IntroScreen",
                GBSAnalyticsConstants.clickaction  to GBSAnalyticsConstants.Back
            )
        )
    }


}