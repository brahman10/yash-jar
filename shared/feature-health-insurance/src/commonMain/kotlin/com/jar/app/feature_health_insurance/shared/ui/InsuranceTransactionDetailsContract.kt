package com.jar.app.feature_health_insurance.shared.ui

import com.jar.app.feature_health_insurance.shared.data.models.transaction_details.InsuranceTransactionDetails

data class InsuranceTransactionDetailsState(
    val insuranceTransactionDetails: InsuranceTransactionDetails? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val transactionHeader: String? = null
)

sealed class InsuranceTransactionDetailScreenEvent {
    data class LoadInsuranceTransactionDetails(val transactionId: String) :
        InsuranceTransactionDetailScreenEvent()
}