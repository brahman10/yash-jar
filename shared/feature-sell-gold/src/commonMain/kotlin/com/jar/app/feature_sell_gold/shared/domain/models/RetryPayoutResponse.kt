package com.jar.app.feature_sell_gold.shared.domain.models

import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RetryPayoutResponse(
    @SerialName("payoutResponse")
    val withdrawalAcceptedResponse: WithdrawalAcceptedResponse
)