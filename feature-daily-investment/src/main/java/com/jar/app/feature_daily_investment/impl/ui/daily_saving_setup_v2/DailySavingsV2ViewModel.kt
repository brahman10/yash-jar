package com.jar.app.feature_daily_investment.impl.ui.daily_saving_setup_v2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_daily_investment.shared.domain.model.AmountSelectionResp
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSAmountSelectionUseCase
import com.jar.app.feature_daily_investment.impl.util.DailySavingPredictionUtil
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
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
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DailySavingsV2ViewModel @Inject constructor(
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    private val fetchDSAmountSelectionUseCase: FetchDSAmountSelectionUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val dailySavingPredictionUtil: DailySavingPredictionUtil,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase
    ) : ViewModel() {

    private val _amountSelectionScreenData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<AmountSelectionResp?>>>()
    val amountSelectionScreenData: LiveData<RestClientResult<ApiResponseWrapper<AmountSelectionResp?>>>
        get() = _amountSelectionScreenData

    private val _dsTotalGoldLiveData = MutableLiveData<Float>()
    val dsTotalGoldLiveData: LiveData<Float>
        get() = _dsTotalGoldLiveData

    private val _dSAmountLiveData = MutableLiveData<Float>()
    val dSAmountLiveData: LiveData<Float>
        get() = _dSAmountLiveData

    private val _dsGoldValueLiveData = MutableLiveData<Float>()
    val dsGoldValueLiveData: LiveData<Float>
        get() = _dsGoldValueLiveData


    private val _dSTimeLiveData = MutableLiveData<Int>()
    val dSTimeLiveData: LiveData<Int>
        get() = _dSTimeLiveData

    private val _dsSeekBarLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>()
    val dsSeekBarLiveData: LiveData<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
        get() = _dsSeekBarLiveData

    private val _isAutoPayResetRequiredLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    private val _roundOffDetailsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val roundOffDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsLiveData

    private val _updateDailySavingStatusLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>()
    val updateDailyInvestmentStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>
        get() = _updateDailySavingStatusLiveData

    private val _rVLiveData = MutableLiveData<List<SuggestedRecurringAmount>>()
    val rVLiveData: LiveData<List<SuggestedRecurringAmount>>
        get() = _rVLiveData


    private var job: Job? = null

    var fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null

    private val _exitSurveyResponse = MutableSharedFlow<Boolean?>()
    val exitSurveyResponse: SharedFlow<Boolean?>
        get() = _exitSurveyResponse

    private val _loading = MutableSharedFlow<Boolean>()
    val loading: SharedFlow<Boolean>
        get() = _loading

    init {
        fetchGoldPrice()
    }

    private fun fetchAmountSelectionScreenData() {
        viewModelScope.launch {
            fetchDSAmountSelectionUseCase.fetchAmountSelectionScreenData().collect {
                _amountSelectionScreenData.postValue(it)
            }
        }
    }

    private fun fetchGoldPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY)
                .collect(
                    onSuccess = {
                        fetchCurrentGoldPriceResponse = it
                        fetchSeekBarData()
                        fetchAmountSelectionScreenData()
                    }
                )
        }
    }

    private fun fetchSeekBarData() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                SavingsSubscriptionType.DEFAULT,
                SavingsType.DAILY_SAVINGS,
                com.jar.app.feature_savings_common.shared.domain.model.DSSavingsState.DS_SETUP.name
            ).collect {
                _dsSeekBarLiveData.postValue(it)
            }
        }
    }

    fun setDailySavingAmount(amount: Float) {
        _dSAmountLiveData.value = amount
    }

    fun setDailySavingTime(months: Int) {
        _dSTimeLiveData.value = months * 30
    }

    fun calculateGoldAmount(
        dailyInvestment: Float,
        months: Int
    ) {
        job?.cancel()
        job = viewModelScope.launch {
            val dailySavingPrediction = dailySavingPredictionUtil.generateDailySavingPrediction(
                months = months,
                currentPrice = fetchCurrentGoldPriceResponse,
                dailyInvestment = dailyInvestment
            )
            _dsTotalGoldLiveData.postValue(dailySavingPrediction.totalInvestmentInVolume)
            _dsGoldValueLiveData.postValue(dailySavingPrediction.totalInvestmentAfterAppreciation)
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

    fun getExitSurveyData() {
        viewModelScope.launch {
            fetchExitSurveyQuestionsUseCase.fetchExitSurveyQuestions(ExitSurveyRequestEnum.DAILY_SAVINGS.toString()).collect(
                onLoading = {
                            _loading.emit(true)
                },
                onSuccessWithNullData = {
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