package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LeasingTnxDetails(
    @SerialName("header")
    val header: String? = null,

    @SerialName("footer")
    val footer: String? = null,

    @SerialName("leaseBreakdownList")
    val leaseBreakdownList: List<LeaseBreakdownListItem>? = null,

    override val uniqueKey: String = header?.plus(footer)
        ?.plus(leaseBreakdownList?.joinToString { it.title.plus(it.volume) }).orEmpty()
) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.GOLD_LEASE_BREAKDOWN
    }

    override fun equals(other: Any?): Boolean {
        return other is LeasingTnxDetails
    }
}

@kotlinx.serialization.Serializable
data class LeaseBreakdownListItem(
    @SerialName("title")
    val title: String? = null,

    @SerialName("volume")
    val volume: Float? = null
)