package com.jar.app.feature_round_off.shared.domain.model

import com.jar.app.feature_user_api.data.dto.FullPaymentInfoDTO
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RoundOffBreakUp(
    @SerialName("transactions")
    val transactions: List<Transaction>,

    @SerialName("manualPayment")
    val fullPayment: FullPaymentInfoDTO? = null,
)