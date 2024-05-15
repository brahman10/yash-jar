package com.jar.app.feature_round_off.impl.ui.pre_autopay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UpdateUserSavingRequest
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.UpdateUserSavingUseCase
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
internal class PreRoundOffAutopayViewModel @Inject constructor(
    private val fetchDailyInvestmentStatusUseCase: com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentStatusUseCase,
    private val initialRoundOffUseCase: FetchInitialRoundOffUseCase,
    private val updateUserSavingUseCase: UpdateUserSavingUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase
) : ViewModel() {

    private val _dailyInvestmentStatusLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>()
    val dailyInvestmentStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>
        get() = _dailyInvestmentStatusLiveData

    private val _initialRoundOffLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff?>>>()
    val initialRoundOffLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff?>>>
        get() = _initialRoundOffLiveData

    private val _isAutoPayResetRequiredLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    private val _managePreferenceLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val managePreferenceLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _managePreferenceLiveData

    private val _savingDetails =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val savingDetails: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _savingDetails

    fun fetchInitialRoundOffsData() {
        viewModelScope.launch {
            initialRoundOffUseCase.initialRoundOffsData(type = com.jar.app.feature_round_off.shared.domain.model.RoundOffType.SMS.name).collect {
                _initialRoundOffLiveData.postValue(it)
            }
        }
    }

    fun fetchDailySavingStatus() {
        viewModelScope.launch {
            fetchDailyInvestmentStatusUseCase.fetchDailyInvestmentStatus().collect {
                _dailyInvestmentStatusLiveData.postValue(it)
            }
        }
    }

    fun enableRoundOff() {
        viewModelScope.launch {
            updateUserSavingUseCase.updateUserSavings(
                UpdateUserSavingRequest(
                    subscriptionType = BaseConstants.SUBSCRIPTION_TYPE_DEFAULT,
                    savingsType = SavingsType.ROUND_OFFS.name
                )
            ).collect { EventBus.getDefault().post(com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent()) }
        }
    }


    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.ROUND_OFFS.name
            ).collect {
                _isAutoPayResetRequiredLiveData.postValue(it)
            }
        }
    }


    fun enableAutomaticRoundOff() {
        viewModelScope.launch {
            manageSavingPreferenceUseCase.manageSavingsPreference(
                savingsType = SavingsType.ROUND_OFFS,
                enableAutoSave = true
            ).collect { _managePreferenceLiveData.postValue(it) }
        }
    }

    fun fetchSavingDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS).collect() {
                _savingDetails.postValue(it)
            }
        }
    }
}