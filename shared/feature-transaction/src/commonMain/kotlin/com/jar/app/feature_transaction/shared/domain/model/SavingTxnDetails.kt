package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavingTxnDetails(
    @SerialName("header")
    val header: String? = null,

    @SerialName("savingsBreakDown")
    val savingBreakdownList: List<SavingBreakdownListItem>? = null,

    override val uniqueKey: String = header?.plus(savingBreakdownList?.joinToString {
        it.label.plus(
            it.value
        )
    }).orEmpty()
) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.SAVINGS_BREAKDOWN
    }

    override fun equals(other: Any?): Boolean {
        return other is SavingTxnDetails
    }
}

@Serializable
data class SavingBreakdownListItem(
    @SerialName("label")
    val label: String? = null,

    @SerialName("value")
    val value: String? = null
)