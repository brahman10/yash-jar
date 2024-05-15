package com.jar.app.feature_daily_investment.shared.ui

import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentOnboardingFragmentData
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOnboardingFragmentDataUseCase
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
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DailyInvestmentOnboardingVariantsViewModel constructor(
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    private val fetchDailyInvestmentOnboardingFragmentDataUseCase: FetchDailyInvestmentOnboardingFragmentDataUseCase,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _amountListFlow = MutableStateFlow<List<SuggestedRecurringAmount>>(emptyList())
    val amountListFlow: CStateFlow<List<SuggestedRecurringAmount>>
        get() = _amountListFlow.toCommonStateFlow()

    private val _dsAmountInfoFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>()
    val dsAmountInfoFlow: CFlow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
        get() = _dsAmountInfoFlow.toCommonFlow()

    private val _isAutoPayResetRequiredFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredFlow: CFlow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredFlow.toCommonFlow()

    private val _roundOffDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>(RestClientResult.none())
    val roundOffDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsFlow.toCommonStateFlow()

    private val _updateDailySavingStatusFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>(RestClientResult.none())
    val updateDailyInvestmentStatusFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>
        get() = _updateDailySavingStatusFlow.toCommonStateFlow()

    private val _staticDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentOnboardingFragmentData?>>>(RestClientResult.none())
    val staticDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentOnboardingFragmentData?>>>
        get() = _staticDataFlow.toCommonStateFlow()

    private var job: Job? = null

    private val _exitSurveyResponse = MutableSharedFlow<Boolean?>()
    val exitSurveyResponse: SharedFlow<Boolean?>
        get() = _exitSurveyResponse

    fun fetchSavingSetupInfo() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                SavingsSubscriptionType.DEFAULT,
                SavingsType.DAILY_SAVINGS
            ).collect {
                _dsAmountInfoFlow.emit(it)
            }
        }
    }

    fun enableDailySaving(amount: Float) {
        GlobalScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = amount)
                .collect {
                    _updateDailySavingStatusFlow.emit(it)
                }
        }
    }

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.DAILY_SAVINGS.name
            ).collect {
                _isAutoPayResetRequiredFlow.emit(it)
            }
        }
    }

    fun fetchUserRoundOffDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect {
                _roundOffDetailsFlow.emit(it)
            }
        }
    }

    fun enableOrUpdateDailySaving(amount: Float) {
        viewModelScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount = amount)
                .collect {
                    _updateDailySavingStatusFlow.emit(it)
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
        viewModelScope.launch {
            val list = mutableListOf<SuggestedRecurringAmount>()
            savingSetupInfo.options.forEach {
                SuggestedRecurringAmount(it.amount, it.recommended)
                list.add(SuggestedRecurringAmount(it.amount, it.recommended))
            }
            _amountListFlow.emit(list)
        }
    }

    fun fetchDailyInvestmentOnboardingFragmentData(version: String?) {
        viewModelScope.launch {
            fetchDailyInvestmentOnboardingFragmentDataUseCase.fetchDailyInvestmentOnboardingFragmentData(version).collect {
                _staticDataFlow.emit(it)
            }
        }
    }

    fun getExitSurveyData() {
        viewModelScope.launch {
            fetchExitSurveyQuestionsUseCase.fetchExitSurveyQuestions(ExitSurveyRequestEnum.DAILY_SAVINGS.toString()).collect(
                onLoading = {},
                onSuccessWithNullData = {
                    _exitSurveyResponse.emit(false)
                },
                onSuccess = {
                    _exitSurveyResponse.emit( true)
                },
                onError = {_, _ ->
                }
            )
        }
    }

}