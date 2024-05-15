package com.jar.app.feature_gold_lease.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GoldLeaseV2TitleValuePair(
    @SerialName("title")
    val title: String? = null,

    @SerialName("value")
    val value: String? = null,

    @SerialName("rowCosmetics")
    private val rowCosmetics: String? = null
): Parcelable {
    fun getRowCosmetics() = TitleValueCosmetics.values().find { it.name == rowCosmetics }

    fun maskTransactionId(transactionId: String): String {
        val masked = StringBuilder()
        masked.append(".......")
        masked.append(transactionId.takeLast(6).orEmpty())
        return masked.toString()
    }
}

enum class TitleValueCosmetics {
    TXN_ID,
    HIGHLIGHTED,
    WEBSITE
}