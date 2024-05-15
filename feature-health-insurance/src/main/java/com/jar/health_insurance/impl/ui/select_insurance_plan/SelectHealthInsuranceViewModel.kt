package com.jar.health_insurance.impl.ui.select_insurance_plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalRequest
import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalResponse
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.PremiumOption
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.SelectPremiumResponse
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.domain.use_cases.CreateProposalUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsurancePlansUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPaymentConfigUseCase
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp as MandateUpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp as OneTimeApp
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import javax.inject.Inject

@HiltViewModel
class SelectHealthInsuranceViewModel @Inject constructor(
    private val fetchInsurancePlansUseCase: FetchInsurancePlansUseCase,
    private val createProposalUseCase: CreateProposalUseCase,
    private val fetchPaymentConfigUseCase: FetchPaymentConfigUseCase,
    private val analyticsApi: AnalyticsApi,
    private val serializer: Serializer,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectInsuranceState())
    val uiState = _uiState.asStateFlow()

    private val _oneTimeUpiAppList = MutableSharedFlow<List<OneTimeApp>>()
    val oneTimeUpiAppList get() = _oneTimeUpiAppList

    var insuranceIdSelected = ""

    private var timerJob: Job? = null

    private val _initiateOneTimePaymentWithCustomUiFlow = MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val initiateOneTimePaymentWithCustomUiFlow :SharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>> get() = _initiateOneTimePaymentWithCustomUiFlow

    private val _initiatePaymentFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val initiatePaymentFlow: SharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _initiatePaymentFlow

    var selectPremiumResponse: SelectPremiumResponse? = null

    private val _createProposalResponseFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<CreateProposalResponse?>>>()
    val createProposalResponseFlow: SharedFlow<RestClientResult<ApiResponseWrapper<CreateProposalResponse?>>>
        get() = _createProposalResponseFlow

    private val _initiateMandatePayment =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<CreateProposalResponse?>>>()
    val initiateMandatePayment: SharedFlow<RestClientResult<ApiResponseWrapper<CreateProposalResponse?>>>
        get() = _initiateMandatePayment

    fun onTriggerEvent(eventType: SelectInsuranceEvents) {
        when (eventType) {
            is SelectInsuranceEvents.OnDataLoad -> onLoadData(eventType.orderId, eventType.mandateUpiApp, eventType.oneTimeUpiApp)
            is SelectInsuranceEvents.OnButtonClick -> onButtonClicked()
            is SelectInsuranceEvents.OnPlanSelection -> onPlanSelected(
                eventType.planSelected,
                eventType.premiumOptionList
            )

            is SelectInsuranceEvents.OnSubscriptionSelected -> onSubscriptionSelected(eventType.subscriptionSelected)
            is SelectInsuranceEvents.OnErrorMessageDisplayed -> updateErrorState()
        }
    }

    private fun updateErrorState() {
        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }

    fun startPopupTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.countDownTimer(
            totalMillis = 45000,
            onFinished = {
                timerJob?.cancel()
                _uiState.update {
                    it.copy(
                        shouldShowPopUp = true
                    )
                }
            }
        )
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                shouldShowPopUp = false
            )
        }
    }

    private fun onSubscriptionSelected(subscriptionSelected: Int) {
        _uiState.update {
            it.copy(
                subscriptionSelected = subscriptionSelected
            )
        }
    }

    private fun getDefaultSubscriptionSelectedIndex(premiumOptionList: List<PremiumOption?>?): Int {
        var defaultSubscriptionSelectedIndex = 0
        premiumOptionList?.forEachIndexed { index, premiumOption ->
            if (premiumOption?.selected.orFalse()) {
                defaultSubscriptionSelectedIndex = index
            }
        }
        return defaultSubscriptionSelectedIndex
    }

    private fun onPlanSelected(planSelected: Int, premiumOptionList: List<PremiumOption?>) {

        analyticsApi.postEvent(
            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                HealthInsuranceEvents.Payment_Frequency to premiumOptionList.joinToString { it?.premiumTypeTxt.orEmpty() }
            )
        )
        _uiState.update {
            it.copy(
                planSelected = planSelected,
                subscriptionSelected = getDefaultSubscriptionSelectedIndex(premiumOptionList)
            )
        }
    }

    private fun onLoadData(orderId: String, mandateUpiApp: MandateUpiApp?, oneTimeUpiApp: OneTimeApp?) {
        viewModelScope.launch {
            fetchInsurancePlansUseCase.fetchInsurancePlans(orderId)
                .collect(
                    onSuccess = { plans ->
                        selectPremiumResponse = plans
                        analyticsApi.postEvent(
                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.Insurance_Select_Plan_Shown,
                                HealthInsuranceEvents.ORDER_ID to orderId,
                            )
                        )
                        analyticsApi.postEvent(
                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                HealthInsuranceEvents.Payment_Frequency to plans.main?.plans?.get(plans.main?.defaultPlanIndex.orZero())?.premiumOptions?.joinToString { it?.premiumTypeTxt.orEmpty() }.orEmpty()
                            )
                        )
                        _uiState.update {
                            it.copy(
                                selectPremiumResponse = plans,
                                planSelected = plans.main?.defaultPlanIndex.orZero(),
                                subscriptionSelected = getDefaultSubscriptionSelectedIndex(
                                    plans.main?.plans?.get(
                                        plans.main?.defaultPlanIndex.orZero()
                                    )?.premiumOptions
                                ),
                                mandateUpiApp = mandateUpiApp,
                                oneTimeUpiApp = oneTimeUpiApp
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        _uiState.update {
                            it.copy(
                                errorMessage = errorMessage
                            )
                        }
                    }
                )
        }
    }

    fun initiateOneTimePaymentWithCustomUi(orderId: String){
        viewModelScope.launch {
            val configId =
                _uiState.value.selectPremiumResponse?.main?.plans?.get(
                    getPlanSelected()
                )?.premiumOptions?.get(getSubscription())?.id
            configId?.let {
                CreateProposalRequest(
                    orderId = orderId,
                    configId = it
                )
            }?.let {
                createProposalUseCase.createProposal(
                    it
                ).collect(
                    onSuccess = { createProposalResponse ->
                        createProposalResponse?.insuranceId?.let { insuranceId ->
                            insuranceIdSelected = insuranceId
                            fetchPaymentConfigUseCase.fetchPaymentConfig(insuranceId)
                                .collectLatest { paymentConfig ->
                                    _initiateOneTimePaymentWithCustomUiFlow.emit(paymentConfig)
                                }
                        }
                    },

                    onError = { errorMessage, _ ->
                        _uiState.update { SelectInsuranceState ->
                            SelectInsuranceState.copy(
                                errorMessage = errorMessage
                            )
                        }
                    }
                )
            }
        }
    }

    fun initiateOneTimePayment(orderId: String) {
        viewModelScope.launch {
            val configId =
                _uiState.value.selectPremiumResponse?.main?.plans?.get(
                    getPlanSelected()
                )?.premiumOptions?.get(getSubscription())?.id
            configId?.let {
                CreateProposalRequest(
                    orderId = orderId,
                    configId = it
                )
            }?.let {
                createProposalUseCase.createProposal(
                    it
                ).collect(
                    onSuccess = { createProposalResponse ->
                        createProposalResponse?.insuranceId?.let { insuranceId ->
                            insuranceIdSelected = insuranceId
                            fetchPaymentConfigUseCase.fetchPaymentConfig(insuranceId)
                                .collectLatest { paymentConfig ->
                                    _initiatePaymentFlow.emit(paymentConfig)
                                }
                        }
                    },

                    onError = { errorMessage, _ ->
                        _uiState.update { SelectInsuranceState ->
                            SelectInsuranceState.copy(
                                errorMessage = errorMessage
                            )
                        }
                    }
                )
            }
        }
    }

    fun initiateMandatePaymentWithCustomUI(orderId: String){
        viewModelScope.launch {
            val configId =
                _uiState.value.selectPremiumResponse?.main?.plans?.get(
                    getPlanSelected()
                )?.premiumOptions?.get(getSubscription())?.id
            configId?.let {
                CreateProposalRequest(
                    orderId = orderId,
                    configId = it
                )
            }?.let { createProposalRequest ->
                createProposalUseCase.createProposal(
                    createProposalRequest
                ).collectLatest {
                    _initiateMandatePayment.emit(it)
                }
            }
        }
    }

    fun setUpiApps(jsonArray: JsonArray) {
        viewModelScope.launch(Dispatchers.Default) {
            val list = serializer.decodeFromString<List<OneTimeApp>>(jsonArray.toString())
            _oneTimeUpiAppList.emit(list)
        }
    }

    fun initiateMandatePayment(orderId: String) {
        viewModelScope.launch {
            val configId =
                _uiState.value.selectPremiumResponse?.main?.plans?.get(
                    getPlanSelected()
                )?.premiumOptions?.get(getSubscription())?.id
            configId?.let {
                CreateProposalRequest(
                    orderId = orderId,
                    configId = it
                )
            }?.let { createProposalRequest ->
                createProposalUseCase.createProposal(
                    createProposalRequest
                ).collectLatest {
                    _createProposalResponseFlow.emit(it)
                }
            }
        }
    }

    fun onPayNowClickedEventAnalytic(eventName: String, paymentType: String, prePaidUpi: Boolean){
        analyticsApi.postEvent(
            HealthInsuranceEvents.Health_Insurance_Event,
            mapOf(
                HealthInsuranceEvents.EVENT_NAME to eventName,
                HealthInsuranceEvents.PREPAID_UI to prePaidUpi,
                HealthInsuranceEvents.PREPAID_UPI_APP to if(paymentType == HealthInsuranceEvents.PAYMENT_TYPE_MANUAL && prePaidUpi) uiState.value.oneTimeUpiApp?.appName.orEmpty() else uiState.value.mandateUpiApp?.appName.orEmpty(),
                HealthInsuranceEvents.Payment_type to paymentType
            )
        )
    }

    private fun onButtonClicked() {

    }

    fun getPlanSelected(): Int {
        return _uiState.value.planSelected
    }

    fun getSubscription(): Int {
        return _uiState.value.subscriptionSelected
    }
}