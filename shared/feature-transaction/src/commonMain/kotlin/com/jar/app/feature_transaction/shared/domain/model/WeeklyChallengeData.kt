package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class WeeklyChallengeData(
    @SerialName("challengeId")
    val challengeId: String? = null,

    override val uniqueKey: String = challengeId.orEmpty()

) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.WEEKLY_CHALLENGE
    }

    override fun equals(other: Any?): Boolean {
        return other is WeeklyChallengeData
    }
}