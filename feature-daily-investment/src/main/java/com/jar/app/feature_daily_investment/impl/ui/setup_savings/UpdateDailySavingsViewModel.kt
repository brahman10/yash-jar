package com.jar.app.feature_daily_investment.impl.ui.setup_savings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsSubscriptionType
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UpdateDailySavingsViewModel @Inject constructor(
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    private val appScope: CoroutineScope,
) : ViewModel() {

    private val _rVLiveData = MutableLiveData<List<SuggestedRecurringAmount>>()

    val rVLiveData: LiveData<List<SuggestedRecurringAmount>>
        get() = _rVLiveData

    private val _dsSeekBarLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>()

    val dsSeekBarLiveData: LiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
        get() = _dsSeekBarLiveData

    private var updateDailySavingJob: Job? = null

    private val _isAutoPayResetRequiredLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    private val _dailySavingStatusLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>()
    val dailyInvestmentStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>
        get() = _dailySavingStatusLiveData

    private val _updateDailySavingStatusLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>()
    val updateDailyInvestmentStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>
        get() = _updateDailySavingStatusLiveData

    private val _roundOffDetailsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val roundOffDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsLiveData

    private val _dailySavingAmountLiveData = MutableLiveData<Float>()
    val dailySavingAmountLiveData: LiveData<Float>
        get() = _dailySavingAmountLiveData

    fun setDailyAmount(amount: Float) {
        _dailySavingAmountLiveData.value = amount
    }

    fun fetchSeekBarData() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                SavingsSubscriptionType.DEFAULT,
                SavingsType.DAILY_SAVINGS,
                com.jar.app.feature_savings_common.shared.domain.model.DSSavingsState.DS_UPDATE.name
            ).collect {
                _dsSeekBarLiveData.postValue(it)
            }
        }
    }

    fun enableOrUpdateDailySaving(amount: Float) {
        updateDailySavingJob?.cancel()
        updateDailySavingJob = appScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = amount)
                .collect {
                    _updateDailySavingStatusLiveData.postValue(it)
                }
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


    fun enableAutomaticDailySavings() {
        viewModelScope.launch {
            manageSavingPreferenceUseCase.manageSavingsPreference(
                savingsType = SavingsType.DAILY_SAVINGS,
                enableAutoSave = true
            ).collect {}
        }
    }

    fun createRvListData(savingSetupInfo: SavingSetupInfo) {
        val list = mutableListOf<SuggestedRecurringAmount>()
        savingSetupInfo.options.forEach {
            SuggestedRecurringAmount(it.amount,it.recommended)
            list.add(SuggestedRecurringAmount(it.amount,it.recommended))
        }
        _rVLiveData.postValue(list)
    }
}
