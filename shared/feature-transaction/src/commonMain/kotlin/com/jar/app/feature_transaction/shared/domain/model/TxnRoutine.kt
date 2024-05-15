package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.feature_transactions_common.shared.CommonTransactionStatus
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.ImageResource
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class TxnRoutine(
    @SerialName("key")
    val key: String,

    @SerialName("value")
    val value: String? = null,

    @SerialName("status")
    val status: String? = null
) : Parcelable {

    fun getColorForStatus(): ColorResource {
        val transactionStatus = status?.uppercase()
        val finalStatus: CommonTransactionStatus =
            CommonTransactionStatus.values().find { it.name == transactionStatus }
                ?: CommonTransactionStatus.DEFAULT
        return finalStatus.getColor()
    }

    fun getLogoForStatus(): ImageResource {
        val transactionStatus = status?.uppercase()
        val finalStatus: CommonTransactionStatus =
            CommonTransactionStatus.values().find { it.name == transactionStatus }
                ?: CommonTransactionStatus.DEFAULT
        return finalStatus.getIcon()
    }
}