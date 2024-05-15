package com.jar.app.feature_daily_investment.impl.ui.update_ds_v2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsSubscriptionType
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateDailySavingV2ViewModel @Inject constructor(
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val remoteConfigManager: RemoteConfigApi,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase
) : ViewModel() {

    companion object {
        const val MONTH_DAYS = 30
    }

    private val _dsSetupInfoLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>()
    val dsSetupInfoLiveData: LiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
        get() = _dsSetupInfoLiveData

    private val _updateDailySavingStatusLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>()
    val updateDailyInvestmentStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>
        get() = _updateDailySavingStatusLiveData

    private val _isAutoPayResetRequiredLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    private val _dsDetailsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val dsDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _dsDetailsLiveData

    private val _roundOffDetailsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val roundOffDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsLiveData

    private val _dsInfoChangesLiveData =
        MutableLiveData<Triple<Float, Float, Float>>()
    val dsInfoChangesLiveData: LiveData<Triple<Float, Float, Float>>
        get() = _dsInfoChangesLiveData

    private val _exitSurveyResponse = MutableSharedFlow<Boolean?>()
    val exitSurveyResponse: SharedFlow<Boolean?>
        get() = _exitSurveyResponse

    private val _loading = MutableSharedFlow<Boolean>()
    val loading: SharedFlow<Boolean>
        get() = _loading

    fun fetchDSSetupInfo() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                SavingsSubscriptionType.DEFAULT,
                SavingsType.DAILY_SAVINGS,
                com.jar.app.feature_savings_common.shared.domain.model.DSSavingsState.DS_UPDATE.name
            ).collect {
                _dsSetupInfoLiveData.postValue(it)
            }
        }
    }

    fun enableOrUpdateDailySaving(amount: Float) {
        viewModelScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = amount)
                .collect {
                    _updateDailySavingStatusLiveData.postValue(it)
                }
        }
    }

    fun enableAutomaticDailySavings() {
        viewModelScope.launch {
            manageSavingPreferenceUseCase.manageSavingsPreference(
                savingsType = SavingsType.DAILY_SAVINGS,
                enableAutoSave = true
            ).collect {}
        }
    }

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.DAILY_SAVINGS.name
            ).collect {
                _isAutoPayResetRequiredLiveData.postValue(it)
            }
        }
    }

    fun fetchUserRoundOffDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect {
                _roundOffDetailsLiveData.postValue(it)
            }
        }
    }

    fun fetchUserDSDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS).collect {
                _dsDetailsLiveData.postValue(it)
            }
        }
    }

    fun updateDsInfoChanges(currentDsAmount: Float, newDsAmount: Float) {
        viewModelScope.launch {
            val newDsAmountWithXMonth =
                remoteConfigManager.updateDsV2MonthCount() * MONTH_DAYS * newDsAmount
            val currentDsAmountWithXMonth =
                remoteConfigManager.updateDsV2MonthCount() * MONTH_DAYS * currentDsAmount
            _dsInfoChangesLiveData.postValue(
                Triple(
                    currentDsAmount,
                    newDsAmount,
                    if (newDsAmount > currentDsAmount) (newDsAmountWithXMonth - currentDsAmountWithXMonth) else (currentDsAmountWithXMonth - newDsAmountWithXMonth)
                )
            )
        }
    }

    fun getExitSurveyData() {
        viewModelScope.launch {
            fetchExitSurveyQuestionsUseCase.fetchExitSurveyQuestions(ExitSurveyRequestEnum.DAILY_SAVINGS.toString()).collect(
                onLoading = {
                    _loading.emit(true)
                },
                onSuccessWithNullData = {
                    _loading.emit(false)
                    _exitSurveyResponse.emit(false)
                },
                onSuccess = {
                    _loading.emit(false)
                    _exitSurveyResponse.emit(true)
                },
                onError = {_, _ ->
                    _loading.emit(false)
                }
            )
        }
    }
}