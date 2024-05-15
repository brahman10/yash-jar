package com.jar.app.feature_round_off.impl.ui.round_off_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.domain.mappers.toUserSettings
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_user_api.domain.model.UserSettings
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdatePauseSavingUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserSettingsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RoundOffDetailsViewModel @Inject constructor(
    private val initialRoundOffUseCase: FetchInitialRoundOffUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val updatePauseSavingUseCase: UpdatePauseSavingUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase
) : ViewModel() {

    private val _roundOffPausedLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val roundOffPausedLiveData: LiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _roundOffPausedLiveData

    private val _roundOffDetailsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val roundOffDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsLiveData

    private val _managePreferenceLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val managePreferenceLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _managePreferenceLiveData

    private val _updateAutoInvestStateLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSettings?>>>()
    val updateAutoInvestStateLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSettings?>>>
        get() = _updateAutoInvestStateLiveData

    private val _initialRoundOffLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff?>>>()
    val initialRoundOffLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff?>>>
        get() = _initialRoundOffLiveData

    private val _isAutoPayResetRequiredLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    var apiExecutionCount: Int = 0

    private val _exitSurveyResponse = MutableSharedFlow<Boolean?>()
    val exitSurveyResponse: SharedFlow<Boolean?>
        get() = _exitSurveyResponse

    fun fetchUserRoundOffDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect {
                _roundOffDetailsLiveData.postValue(it)
            }
        }
    }

    fun resumeRoundOff() {
        viewModelScope.launch {
            updatePauseSavingUseCase.updatePauseSavingValue(
                shouldPause = false, pauseType = SavingsType.ROUND_OFFS.name
            ).collect { _roundOffPausedLiveData.postValue(it) }
        }
    }

    fun toggleAutoInvest(isEnabled: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateUserSettings(UserSettingsDTO(autoInvestNoSpends = isEnabled))
                .mapToDTO {
                    it?.toUserSettings()
                }.collect {
                    _updateAutoInvestStateLiveData.postValue(it)
                }
        }
    }

    fun enableAutomaticRoundOff() {
        viewModelScope.launch {
            manageSavingPreferenceUseCase.manageSavingsPreference(
                savingsType = SavingsType.ROUND_OFFS, enableAutoSave = true
            ).collect { _managePreferenceLiveData.postValue(it) }
        }
    }

    fun fetchInitialRoundOffsData() {
        viewModelScope.launch {
            initialRoundOffUseCase.initialRoundOffsData(type = com.jar.app.feature_round_off.shared.domain.model.RoundOffType.SMS.name)
                .collect {
                    _initialRoundOffLiveData.postValue(it)
                }
        }
    }

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount, SavingsType.ROUND_OFFS.name
            ).collect {
                _isAutoPayResetRequiredLiveData.postValue(it)
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