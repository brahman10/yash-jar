package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.enter_amount_duration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalAmount.EnterAmountFragmentAction
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalAmount.EnterAmountFragmentState
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalDuration.MandateAndSavingDetails
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.AmountEntered
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.Duration
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.GoalAmountandDurationScreenV2
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.action
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.editGoal
import com.jar.app.feature_goal_based_saving.shared.data.model.CalculateDailyAmountResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalRequest
import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalDurationResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.MandateInfo
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.CreateGoalUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchDailyAmountUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchGoalAmountScreenDetailsUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchGoalDurationUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchMandateInfoUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.UpdateGoalDailyRecurringAmountUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UserAmountAndDurationViewModel  @Inject constructor(
    private val fetchGoalAmountScreenDetailsUseCase: FetchGoalAmountScreenDetailsUseCase,
    private val fetchGoalDurationUseCase: FetchGoalDurationUseCase,
    private val fetchCalculateDurationUseCase: FetchDailyAmountUseCase,
    private val fetchMandateInfoUseCase: FetchMandateInfoUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val createGoalUseCase: CreateGoalUseCase,
    private val updateGoalDailyInvestmentStatusUseCase: UpdateGoalDailyRecurringAmountUseCase,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state = MutableStateFlow(EnterAmountFragmentState())
    val state: StateFlow<EnterAmountFragmentState> = _state

    private val _calculateDailyAmountResponse = MutableSharedFlow<CalculateDailyAmountResponse?>()
    val calculateDailyAmountResponse:SharedFlow<CalculateDailyAmountResponse?> = _calculateDailyAmountResponse

    private val _goalDurationResponse = MutableSharedFlow<GoalDurationResponse?>()
    val goalDurationResponse: SharedFlow<GoalDurationResponse?> = _goalDurationResponse


    var dailyAmount: Int? = null

    private val _mandateInfoResponse =
        MutableLiveData<RestClientResult<ApiResponseWrapper<MandateInfo>>>()
    var mandateInfoResponse: LiveData<RestClientResult<ApiResponseWrapper<MandateInfo>>> = _mandateInfoResponse
        private set

    private val _savingDetails =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    var savingDetails: MutableLiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>> = _savingDetails
        private set

    private val _goalCreationResponse = MutableLiveData<RestClientResult<ApiResponseWrapper<CreateGoalResponse>>>()
    var goalCreationResponse: MutableLiveData<RestClientResult<ApiResponseWrapper<CreateGoalResponse>>> = _goalCreationResponse
        private set

    var mandateInfo: MutableLiveData<MandateInfo> = MutableLiveData()
    var userSavingsDetails: MutableLiveData<UserSavingsDetails> = MutableLiveData()
    private val _mandateAndSavingDetailsMediatorLiveData = MediatorLiveData<MandateAndSavingDetails>().apply {
        addSource(mandateInfo) {
            combineMandateAndSavingDetails()
        }
        addSource(userSavingsDetails) {
            combineMandateAndSavingDetails()
        }
    }
    val mandateAndSavingDetailsMediatorLiveData: LiveData<MandateAndSavingDetails> =
        _mandateAndSavingDetailsMediatorLiveData

    private fun combineMandateAndSavingDetails() {
        if (mandateInfo.value != null && userSavingsDetails.value != null) {
            _mandateAndSavingDetailsMediatorLiveData.value = MandateAndSavingDetails(
                mandateInfo.value,
                userSavingsDetails.value
            )
        }
    }
    fun handelAction(enterAmountFragmentAction: EnterAmountFragmentAction) {
        when (enterAmountFragmentAction) {
            EnterAmountFragmentAction.Init -> {
                fetchGaolAmountScreenStaticData()
                analyticsHandler.postEvent(
                    GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown,
                    mapOf(
                        GBSAnalyticsConstants.screen_type to "Goal Amount and Duration Screen V2"
                    )
                )
            }

            is EnterAmountFragmentAction.SentAmountChangedEvent -> {
                analyticsHandler.postEvent(
                    GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked,
                    mapOf(
                        GBSAnalyticsConstants.screen_type to enterAmountFragmentAction.screenType,
                        "action" to enterAmountFragmentAction.action,
                        "errormessageshown" to enterAmountFragmentAction.errorMessageShown
                    )
                )
            }

            is EnterAmountFragmentAction.OnNextButtonClicked -> {
                analyticsHandler.postEvent(
                    GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked,
                    mapOf(
                        GBSAnalyticsConstants.screen_type to "Amount Screen",
                        "action" to "click next",
                        "amount" to enterAmountFragmentAction.amount
                    )
                )
            }

            is EnterAmountFragmentAction.FetchDuration -> {
                val amount = try {
                    enterAmountFragmentAction.amount.toInt()
                } catch (e: Exception) {
                    0
                }
                fetchDurations(amount)
            }

            is EnterAmountFragmentAction.FetchBreakDown -> {
                getDailyAmount(enterAmountFragmentAction.amount, enterAmountFragmentAction.months)
            }

            EnterAmountFragmentAction.ClearOldDurationData -> {
                viewModelScope.launch {
                    dailyAmount = null
                    _calculateDailyAmountResponse.emit(null)
                    //_goalDurationResponse.emit(null)
                }
            }

            is EnterAmountFragmentAction.OnEditGoalIconClicked -> {
                analyticsHandler.postEvent(
                    GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked,
                    mapOf(
                        GBSAnalyticsConstants.screen_type to GoalAmountandDurationScreenV2,
                        action to editGoal,
                        AmountEntered to enterAmountFragmentAction.amountentered,
                        Duration to (enterAmountFragmentAction.duration?:0),
                        "daily_savings_amount" to (enterAmountFragmentAction.dailySavingsAmount ?: 0)
                    )
                )
            }

            is EnterAmountFragmentAction.OnConfirmButtonClicked -> {
                analyticsHandler.postEvent(
                    GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked,
                    mapOf(
                        "screen_type" to GoalAmountandDurationScreenV2,
                        "action" to "Confirm Goal",
                        "amountentered" to enterAmountFragmentAction.amountentered,
                        "duration" to (enterAmountFragmentAction.duration?:0),
                        "daily_savings_amount" to (enterAmountFragmentAction.dailySavingsAmount ?: 0)
                    )
                )
            }
        }
    }

    private fun fetchGaolAmountScreenStaticData() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchGoalAmountScreenDetailsUseCase.execute().collect(
                onLoading = {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                },
                onSuccess = {
                    _state.value = _state.value.copy(
                        loading = false,
                        goalAmountResponse = it
                    )
                },
                onError = { _, _ ->
                    _state.value = _state.value.copy(
                        loading = false
                    )
                }
            )
        }
    }

    private fun fetchDurations(amount: Int) {
        viewModelScope.launch {
            fetchGoalDurationUseCase.getGoalDurationData(amount).collect(
                onLoading = {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                },
                onSuccess = {
                    _goalDurationResponse.emit(it)
                },
                onError = { _, _ ->
                    _state.value = _state.value.copy(
                        loading = false
                    )
                }
            )
        }
    }

    private fun getDailyAmount(amount: Int, months: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (amount != 0) {
                fetchCalculateDurationUseCase.execute(amount, months).collect(
                    onLoading = {
                        _state.value = _state.value.copy(
                            loading = true
                        )
                   },
                    onSuccess = {
                        dailyAmount = it.amount
                        _calculateDailyAmountResponse.emit(
                            it
                        )
                    },
                    onError = { _, _ ->
                        _state.value = _state.value.copy(
                            loading = false
                        )
                    }
                )
            }
        }
    }

    fun fetchMandateInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchMandateInfoUseCase.execute(dailyAmount ?: 0, "DAILY_SAVINGS").collect() {
                _mandateInfoResponse.postValue(it)
            }
        }
    }

    fun fetchSavingDetails(savingsType: SavingsType) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(savingsType).collect() {
                _savingDetails.postValue(it)
            }
        }
    }

    fun createGoal(createGoalRequest: CreateGoalRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            createGoalUseCase.execute(
                createGoalRequest
            ).collect() {
                _goalCreationResponse.postValue(it)
            }
        }
    }

    fun updateDailyGoalRecurringAmount(amount: Float) {
        GlobalScope.launch(Dispatchers.IO) {
            updateGoalDailyInvestmentStatusUseCase.execute(amount).collect(
                onLoading = {},
                onSuccess = {},
            )
        }
    }
}