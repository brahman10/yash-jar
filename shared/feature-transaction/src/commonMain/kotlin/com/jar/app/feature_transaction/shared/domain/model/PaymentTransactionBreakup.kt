package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.feature_user_api.domain.model.FullPaymentInfo
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PaymentTransactionBreakup(

    @SerialName("transactions")
    val transactions: List<Transaction>,

    @SerialName("manualPayment")
    val fullPayment: FullPaymentInfo? = null,
)