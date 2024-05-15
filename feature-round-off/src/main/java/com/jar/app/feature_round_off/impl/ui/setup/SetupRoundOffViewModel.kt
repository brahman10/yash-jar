package com.jar.app.feature_round_off.impl.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupRoundOffViewModel @Inject constructor(
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase
) : ViewModel() {

    private val _exitSurveyResponse = MutableSharedFlow<Boolean?>()
    val exitSurveyResponse: SharedFlow<Boolean?>
        get() = _exitSurveyResponse

    private val _roundOffDetailsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val roundOffDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsLiveData

    fun fetchUserRoundOffDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect{
                _roundOffDetailsLiveData.postValue(it)
            }
        }
    }

    fun getExitSurveyData() {
        viewModelScope.launch {
            fetchExitSurveyQuestionsUseCase.fetchExitSurveyQuestions(ExitSurveyRequestEnum.ROUND_OFFS.toString()).collect(
                onLoading = {},
                onSuccessWithNullData = {
                    _exitSurveyResponse.emit(false)
                },
                onSuccess = {
                    _exitSurveyResponse.emit(true)
                },
                onError = {_, _ ->
                }
            )
        }
    }
}