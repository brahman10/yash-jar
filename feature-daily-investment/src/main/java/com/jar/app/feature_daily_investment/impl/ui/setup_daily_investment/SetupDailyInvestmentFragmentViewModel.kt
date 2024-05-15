package com.jar.app.feature_daily_investment.impl.ui.setup_daily_investment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.addPercentage
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchIsSavingPausedUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper

@HiltViewModel
internal class SetupDailyInvestmentFragmentViewModel @Inject constructor(
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val fetchDailyInvestmentStatusUseCase: FetchDailyInvestmentStatusUseCase,
    private val fetchIsSavingsPausedUseCase: FetchIsSavingPausedUseCase,
    private val updateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    private val appScope: CoroutineScope,
) : ViewModel() {

    private val _amountSourceLiveData = MutableLiveData<String>()
    val amountSourceLiveData: LiveData<String>
        get() = _amountSourceLiveData

    private val _recommendedAmountFromApiLiveData = MutableLiveData<Int>()
    val recommendedAmountFromApiLiveData: LiveData<Int>
        get() = _recommendedAmountFromApiLiveData

    private val _suggestedAmountFromApiLiveData = MutableLiveData<Int>()
    val suggestedAmountFromApiLiveData: LiveData<Int>
        get() = _suggestedAmountFromApiLiveData

    private val _rVLiveData = MutableLiveData<List<SuggestedRecurringAmount>>()

    val rVLiveData: LiveData<List<SuggestedRecurringAmount>>
        get() = _rVLiveData

    private val _dsSeekBarLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>()

    val dsSeekBarLiveData: LiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
        get() = _dsSeekBarLiveData

    private val _dailySavingAmountLiveData = MutableLiveData<Float>()
    val dailySavingAmountLiveData: LiveData<Float>
        get() = _dailySavingAmountLiveData

    private val _dailySavingStatusLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>()
    val dailyInvestmentStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>
        get() = _dailySavingStatusLiveData

    private val _updateDailySavingStatusLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>()
    val updateDailyInvestmentStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>
        get() = _updateDailySavingStatusLiveData

    private val _savingsPausedLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val savingsPausedLiveData: LiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _savingsPausedLiveData

    private val _updatePauseDurationLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val updatePauseDurationLiveData: LiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _updatePauseDurationLiveData

    private val _isAutoPayResetRequiredLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    private val _buyPriceLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>()
    val buyPriceLiveData: LiveData<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _buyPriceLiveData

    private val _dailySavingsDetailsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val dailySavingsDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _dailySavingsDetailsLiveData

    private val _roundOffDetailsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val roundOffDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsLiveData

    private var updateDailySavingJob: Job? = null

    var fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null

    var isSavingsEnabled = false
    var isSavingPaused = false

    init {
        fetchUserDailySavingsDetails()
        fetchSeekBarData()
        fetchDailySavingStatus()
        fetchUserRoundOffDetails()
    }

    fun fetchUserDailySavingsDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS).collect {
                _dailySavingsDetailsLiveData.postValue(it)
            }
        }
    }

    fun fetchSeekBarData() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                SavingsSubscriptionType.DEFAULT,
                SavingsType.DAILY_SAVINGS
            ).collect {
                _dsSeekBarLiveData.postValue(it)
            }
        }
    }

    fun fetchIsSavingsPaused() {
        viewModelScope.launch {
            fetchIsSavingsPausedUseCase.fetchIsSavingPaused(SavingsType.DAILY_SAVINGS)
                .collectLatest {
                    _savingsPausedLiveData.postValue(it)
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

    fun setDailyAmount(amount: Float) {
        _dailySavingAmountLiveData.value = amount
    }

    fun valueInTenYears(dailyAmount: Double, years: Int = 10): Double {
        return ((dailyAmount * 365 * years) * (1 + 9.0 / 100).pow(years))
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

    fun updateAutoInvestPauseDuration(pause: Boolean, pauseDuration: String?) {
        viewModelScope.launch {
            updateSavingPauseDurationUseCase.updateSavingPauseDuration(
                pause,
                pauseDuration,
                SavingsType.DAILY_SAVINGS
            )
                .collectLatest {
                    _updatePauseDurationLiveData.postValue(it)
                }
        }
    }

    fun fetchDailySavingStatus() {
        viewModelScope.launch {
            fetchDailyInvestmentStatusUseCase.fetchDailyInvestmentStatus().collect {
                fetchIsSavingsPaused()
                _dailySavingStatusLiveData.postValue(it)
            }
        }
    }

    fun fetchBuyPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collectLatest {
                _buyPriceLiveData.postValue(it)
            }
        }
    }

    fun getFinalVolume(amount: Float): Float {
        return fetchCurrentGoldPriceResponse?.let {
            val currentPriceWithTax = it.price.addPercentage(it.applicableTax!!)
                .roundUp(2)
            return (amount * 365 / currentPriceWithTax).roundDown(4)
        } ?: kotlin.run {
            5f
        }
    }

    fun fetchUserRoundOffDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect{
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

    fun setAmountSource(source: String) {
        _amountSourceLiveData.value = source
    }

    fun setRecommendedAmount(amount: Int) {
        _recommendedAmountFromApiLiveData.value = amount
    }

    fun setSuggestedAmount(amount: Int) {
        _suggestedAmountFromApiLiveData.value = amount
    }
}