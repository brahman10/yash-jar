package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RoundOffData(
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("roundoffCount")
    val roundoffCount: Int? = null,

    override val uniqueKey: String = orderId?.plus(type)?.plus(roundoffCount).orEmpty()
) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.ROUND_OFF
    }

    override fun equals(other: Any?): Boolean {
        return other is RoundOffData
    }
}