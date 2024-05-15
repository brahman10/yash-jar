package com.jar.app.feature_daily_investment.impl.ui.onboarding_setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsSubscriptionType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
internal class DailyInvestOnboardingFragmentViewModel @Inject constructor(
    private val buyGoldUseCase: BuyGoldUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _rVLiveData = MutableLiveData<List<SuggestedRecurringAmount>>()

    val rVLiveData: LiveData<List<SuggestedRecurringAmount>>
        get() = _rVLiveData

    private val _dsAmountInfoLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>()

    val dsAmountInfoLiveData: LiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
        get() = _dsAmountInfoLiveData

    private val _volumeLiveData = MutableLiveData<Float>()
    val volumeLiveData: LiveData<Float>
        get() = _volumeLiveData

    private val _futureLiveData = MutableLiveData<Float>()
    val futureLiveData: LiveData<Float>
        get() = _futureLiveData

    private val _isAutoPayResetRequiredLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    private val _roundOffDetailsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val roundOffDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsLiveData

    private val _updateDailySavingStatusLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>()
    val updateDailyInvestmentStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>
        get() = _updateDailySavingStatusLiveData

    private var fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null

    private var job: Job? = null

    fun fetchCurrentGoldPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY)
                .collect(
                    onSuccess = {
                        fetchCurrentGoldPriceResponse = it
                    }
                )
        }
    }

    fun fetchSeekBarData() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                SavingsSubscriptionType.DEFAULT,
                SavingsType.DAILY_SAVINGS
            ).collect {
                _dsAmountInfoLiveData.postValue(it)
            }
        }
    }

    fun fetchValueInYears(dailyAmount: Float, percent: Float = 9.9f, months: Int = 12 * 10) {
        job?.cancel()
        job = viewModelScope.launch(dispatcherProvider.default) {
            val daysInAMonth = 30.436875f
            val initialMonthlyAmount = dailyAmount * daysInAMonth
            var principle = dailyAmount * daysInAMonth
            val rate = (percent / 100f) / 12f
            var a: Float
            var interest: Float
            repeat(months - 1) {
                if (currentCoroutineContext().isActive) {
                    a = principle + initialMonthlyAmount
                    interest = rate * a
                    principle = a + interest
                } else {
                    return@repeat
                }
            }
            _futureLiveData.postValue(principle)
        }
    }

    fun enableDailySaving(amount: Float) {
        GlobalScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = amount)
                .collect {
                    EventBus.getDefault().post(RefreshDailySavingEvent())
                }
        }
    }

    fun getVolumeFromAmount(amount: Float) {
        viewModelScope.launch {
            buyGoldUseCase.calculateVolumeFromAmount(amount, fetchCurrentGoldPriceResponse)
                .collectLatest {
                    it.data?.let {
                        _volumeLiveData.postValue(it)
                    }
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

    fun enableOrUpdateDailySaving(amount: Float) {
        viewModelScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = amount)
                .collect {
                    _updateDailySavingStatusLiveData.postValue(it)
                }
        }
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
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