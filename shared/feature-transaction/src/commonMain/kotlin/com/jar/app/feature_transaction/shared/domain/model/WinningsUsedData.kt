package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class WinningsUsedData(
    @SerialName("jarWinningsUsedText")
    val jarWinningsUsedText: String? = null,

    override val uniqueKey: String = jarWinningsUsedText.orEmpty()
) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.WINNINGS_USED
    }

    override fun equals(other: Any?): Boolean {
        return other is WinningsUsedData
    }
}