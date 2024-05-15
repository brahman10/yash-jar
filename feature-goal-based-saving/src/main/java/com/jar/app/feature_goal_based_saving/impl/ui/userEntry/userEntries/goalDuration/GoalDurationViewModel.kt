package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalDuration

import androidx.lifecycle.*
import com.jar.app.feature_goal_based_saving.shared.data.model.*
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.*
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class GoalDurationViewModel @Inject constructor(
    private val fetchGoalDurationUseCase: FetchGoalDurationUseCase,
    private val fetchCalculateDurationUseCase: FetchDailyAmountUseCase,
    private val fetchMandateInfoUseCase: FetchMandateInfoUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val createGoalUseCase: CreateGoalUseCase,
    private val updateGoalDailyInvestmentStatusUseCase: UpdateGoalDailyRecurringAmountUseCase,
    ) : ViewModel() {
    private val _goalDurationResponse =
        MutableLiveData<RestClientResult<ApiResponseWrapper<GoalDurationResponse>>>()
    val goalDurationResponse: LiveData<RestClientResult<ApiResponseWrapper<GoalDurationResponse>>>
        get() = _goalDurationResponse

    private val _amountBreakDown =
        MutableLiveData<RestClientResult<ApiResponseWrapper<CalculateDailyAmountResponse>>>()
    val amountBreakDown: LiveData<RestClientResult<ApiResponseWrapper<CalculateDailyAmountResponse>>>
        get() = _amountBreakDown

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

    private val _updateDailyGoalResponse = MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>()
    var updateDailyGoalResponse: MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>> = _updateDailyGoalResponse
        private set

    var dailyAmount: Int = 0
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

    fun fetch(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = fetchGoalDurationUseCase.getGoalDurationData(amount)
            data.collect {
                _goalDurationResponse.postValue(it)
            }
        }
    }

    fun getDailyAmount(amount: Int, months: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = fetchCalculateDurationUseCase.execute(amount, months)
            data.collect {
                _amountBreakDown.postValue(
                    it
                )
            }
        }
    }

    fun fetchMandateInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchMandateInfoUseCase.execute(dailyAmount, "DAILY_SAVINGS").collect() {
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
                onLoading = {
                },
                onSuccess = {},
            )
        }
    }
}

data class MandateAndSavingDetails(
    val mandateInfo: MandateInfo?,
    val savingsType: UserSavingsDetails?
)