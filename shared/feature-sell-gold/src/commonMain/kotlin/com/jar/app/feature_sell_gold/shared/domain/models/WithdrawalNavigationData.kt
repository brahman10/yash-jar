package com.jar.app.feature_sell_gold.shared.domain.models

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class WithdrawalNavigationData(
    val isRetryFlow: Boolean,
    val withdrawRequest: WithdrawRequest? = null,          //will be null in retry flow
    val orderId: String? = null                            //will be null in regular flow
) : Parcelable