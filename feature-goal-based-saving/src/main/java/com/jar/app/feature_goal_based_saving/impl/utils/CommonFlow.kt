package com.jar.app.feature_goal_based_saving.impl.utils

import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object TransactionFlow {
    private val _transactionFlow: MutableSharedFlow<TransactionFlowModel> = MutableSharedFlow(0)
    val transactionFlow: SharedFlow<TransactionFlowModel> = _transactionFlow

    suspend fun updateFlow(
        goalId: String,
        roundOffAmount: Float,
        autoInvestStatus: MandatePaymentProgressStatus
    ) {
        _transactionFlow.emit(TransactionFlowModel(
            goalId,
            roundOffAmount,
            autoInvestStatus
        ))
    }
}

data class TransactionFlowModel(
    val goalId: String,
    val roundOffAmount: Float,
    val autoInvestStatus: MandatePaymentProgressStatus
)
