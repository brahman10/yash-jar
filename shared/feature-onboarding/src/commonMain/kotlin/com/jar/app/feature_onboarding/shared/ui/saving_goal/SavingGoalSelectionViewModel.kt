package com.jar.app.feature_onboarding.shared.ui.saving_goal

import com.jar.app.feature_onboarding.shared.domain.model.GoalsV2
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalPostRequest
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalsResponse
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalsV2Response
import com.jar.app.feature_onboarding.shared.domain.model.UserSavingPreferences
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSavingGoalsUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSavingGoalsV2UseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchUserSavingPreferencesUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.PostSavingGoalsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class SavingGoalSelectionViewModel constructor(
    private val postSavingGoalsUseCase: PostSavingGoalsUseCase,
    private val fetchSavingGoalsUseCase: FetchSavingGoalsUseCase,
    private val fetchSavingGoalsV2UseCase: FetchSavingGoalsV2UseCase,
    private val fetchUserSavingPreferencesUseCase: FetchUserSavingPreferencesUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _savingGoalsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SavingGoalsResponse>>>(RestClientResult.none())
    val savingGoalsFlow: CFlow<RestClientResult<ApiResponseWrapper<SavingGoalsResponse>>>
        get() = _savingGoalsFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    private val _savingGoalsV2Flow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SavingGoalsV2Response>>>(RestClientResult.none())
    val savingGoalsV2Flow: CFlow<RestClientResult<ApiResponseWrapper<SavingGoalsV2Response>>>
        get() = _savingGoalsV2Flow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    private val _goalsV2ListFlow =
        MutableStateFlow<List<GoalsV2>?>(emptyList())
    val goalsV2ListFlow: CFlow<List<GoalsV2>?>
        get() = _goalsV2ListFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    private val _savingGoalsPostFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val savingGoalsPostFlow: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _savingGoalsPostFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    private val _userSavingPrefFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserSavingPreferences?>>>(RestClientResult.none())
    val userSavingPrefFlow: CFlow<RestClientResult<ApiResponseWrapper<UserSavingPreferences?>>>
        get() = _userSavingPrefFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    fun fetchSavingGoals() {
        viewModelScope.launch {
            fetchSavingGoalsUseCase.fetchSavingGoals().collect {
                _savingGoalsFlow.emit(it)
                getUserSavingPreferences()
            }
        }
    }

    fun fetchSavingGoalsV2() {
        viewModelScope.launch {
            fetchSavingGoalsV2UseCase.fetchSavingGoals().collect {
                _savingGoalsV2Flow.emit(it)
                _goalsV2ListFlow.emit(it.data?.data?.savingsGoalsV2Data?.savingsGoalList)
                getUserSavingPreferences()
            }
        }
    }

    fun updateGoalList(goal : GoalsV2){
        viewModelScope.launch {
            val updatedList = _goalsV2ListFlow.value?.map { it.copy() }
            val index = updatedList?.indexOfFirst { it.title == goal.title }

            if (index != -1) {
                if (index != null) {
                    updatedList[index].isSelected = !updatedList[index].isSelected!!
                }
                _goalsV2ListFlow.emit(updatedList)
            }
        }
    }
    fun postSavingGoals(goals: List<String>) {
        viewModelScope.launch {
            postSavingGoalsUseCase.postSavingGoals(
                SavingGoalPostRequest(
                    goals
                )
            ).collect {
                _savingGoalsPostFlow.emit(it)
            }
        }
    }

    fun getUserSavingPreferences() {
        viewModelScope.launch {
            fetchUserSavingPreferencesUseCase.getUserSavingPreferences().collect {
                _userSavingPrefFlow.emit(it)
            }
        }
    }
}