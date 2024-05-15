package com.jar.app.feature_health_insurance.shared.ui

import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsuranceTransactionsUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchManageScreenDataUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPaymentConfigUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.orFalse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageScreenViewModel(
    private val fetchManageScreenDataUseCase: FetchManageScreenDataUseCase,
    private val fetchPaymentConfigUseCase: FetchPaymentConfigUseCase,
    private val fetchInsuranceTransactionsUseCase: FetchInsuranceTransactionsUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _uiState = MutableStateFlow(ManageInsuranceState())
    val uiState = _uiState.toCommonStateFlow()

    private val _initiatePaymentFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val initiatePaymentFlow: CFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _initiatePaymentFlow.toCommonFlow()

    fun onTriggerEvent(eventType: ManageScreenEvent) {
        when (eventType) {
            is ManageScreenEvent.LoadManageScreenData -> loadData(eventType.insuranceId)
            is ManageScreenEvent.ErrorMessageDisplayed -> clearErrorMessage()
            is ManageScreenEvent.InitiateManualPayment -> initiateOneTimePayment(eventType.insuranceId)
            is ManageScreenEvent.TriggerAnalyticEvent -> onTriggerAnalyticEvent(eventType.eventType)
        }
    }

    private fun onTriggerAnalyticEvent(eventType: ManageScreenAnalyticsEvents) {
        when (eventType) {
            is ManageScreenAnalyticsEvents.ManageScreenShownEvent -> {
                if (uiState.value.manageScreenData?.isInsuranceExpired.orFalse()) {
                    analyticsApi.postEvent(
                        HealthInsuranceEvents.Insurance_ManageScreen_Shown,
                        eventType.analyticsData
                    )
                } else {
                    analyticsApi.postEvent(
                        HealthInsuranceEvents.Insurance_ManageScreen_Shown,
                        eventType.analyticsData
                    )
                }
            }

            is ManageScreenAnalyticsEvents.ManageScreenClickedEvent -> {
                if (uiState.value.manageScreenData?.isInsuranceExpired.orFalse()) {
                    analyticsApi.postEvent(
                        HealthInsuranceEvents.Insurance_Manage_Screen_Clicked,
                        eventType.analyticsData
                    )
                } else {
                    analyticsApi.postEvent(
                        HealthInsuranceEvents.Insurance_Manage_Screen_Clicked,
                        eventType.analyticsData
                    )
                }
            }
        }
    }

    private fun initiateOneTimePayment(insuranceId: String) {
        viewModelScope.launch {
            fetchPaymentConfigUseCase.fetchPaymentConfig(insuranceId)
                .collectLatest { paymentConfig ->
                    _initiatePaymentFlow.emit(paymentConfig)
                }
        }
    }

    private fun clearErrorMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }

    private fun loadData(insuranceId: String) {
        viewModelScope.launch {
            fetchManageScreenDataUseCase.fetchManageScreenData(insuranceId).collect(
                onLoading = {
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                },
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            manageScreenData = response,
                            isLoading = false
                        )
                    }
                },

                onError = { errorMessage, errorCode ->
                    _uiState.update {
                        it.copy(
                            errorMessage = errorMessage,
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun loadInsuranceTransactions(insuranceId: String) = Pager(
        viewModelScope,
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            initialLoadSize = NETWORK_PAGE_SIZE
        ),
        initialKey = 1,
        getItems = { currentKey, size ->
            val response = fetchInsuranceTransactionsUseCase.fetchInsuranceTransactions(
                insuranceId = insuranceId,
                page = currentKey,
                size = size
            )
            _uiState.update {
                it.copy(
                    transactionHeader = response.data?.data?.title
                )
            }
            PagingResult(
                items = response.data?.data?.insuranceTransactionDataList.orEmpty(),
                currentKey = currentKey,
                prevKey = { currentKey - 1 },
                nextKey = { currentKey + 1 }
            )
        },
    ).pagingData.cachedIn(viewModelScope).toCommonFlow()

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }
}



sealed class ManageScreenAnalyticsEvents {
    data class ManageScreenShownEvent(val analyticsData: Map<String, String>) :
        ManageScreenAnalyticsEvents()

    data class ManageScreenClickedEvent(val analyticsData: Map<String, String>) :
        ManageScreenAnalyticsEvents()
}

