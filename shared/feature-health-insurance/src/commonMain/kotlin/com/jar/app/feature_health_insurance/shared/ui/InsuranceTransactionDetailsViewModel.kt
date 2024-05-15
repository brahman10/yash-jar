package com.jar.app.feature_health_insurance.shared.ui

import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsuranceTransactionDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InsuranceTransactionDetailsViewModel(
    private val fetchInsuranceTransactionDetailsUseCase: FetchInsuranceTransactionDetailsUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _uiState = MutableStateFlow(InsuranceTransactionDetailsState())
    val uiState = _uiState.toCommonStateFlow()

    fun onTriggerEvent(eventType: InsuranceTransactionDetailScreenEvent) {
        when (eventType) {
            is InsuranceTransactionDetailScreenEvent.LoadInsuranceTransactionDetails -> loadData(
                eventType.transactionId
            )
        }
    }

    private fun loadData(transactionId: String) {
        viewModelScope.launch {
            fetchInsuranceTransactionDetailsUseCase.fetchInsuranceTransactionDetails(transactionId).collect(
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
                            insuranceTransactionDetails = response,
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

}