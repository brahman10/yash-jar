package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TxnDetailsData(
    @SerialName("title")
    val title: String? = null,
    @SerialName("value")
    val value: String? = null,
    @SerialName("detailsList")
    val list: List<TxnDetails>? = null,

    override val uniqueKey: String = title?.plus(value)?.plus(list?.joinToString { it.title?.plus(it.value).orEmpty() }).orEmpty()
) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.TRANSACTION_DETAILS
    }

    override fun equals(other: Any?): Boolean {
        return other is TxnRoutine
    }
}