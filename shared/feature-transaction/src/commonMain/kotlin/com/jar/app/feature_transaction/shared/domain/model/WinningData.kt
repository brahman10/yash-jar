package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.feature_transactions_common.shared.CommonTransactionStatus
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.ColorResource
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class WinningData(
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("goldPurchaseAmount")
    val goldPurchaseAmount: Float? = null,
    @SerialName("assetSourceType")
    val assetSourceType: String? = null,
    @SerialName("assetTxnId")
    val assetTxnId: String? = null,
    @SerialName("date")
    val date: String? = null,
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("nestedCardName")
    val nestedCardName: String? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("subTitle")
    val subTitle: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("txnType")
    val txnType: String? = null,
    @SerialName("isEnabled")
    val isEnabled: Boolean? = null
) : Parcelable {

    fun getTransactionType(): WinningTxnType {
        return if (txnType.isNullOrBlank())
            WinningTxnType.CREDIT
        else
            WinningTxnType.valueOf(txnType)
    }

    fun getColorForStatus(): ColorResource {
        val transactionStatus = status?.uppercase()
        val status: CommonTransactionStatus =
            CommonTransactionStatus.values().find { it.name == transactionStatus }
                ?: CommonTransactionStatus.DEFAULT
        return status.getColor()
    }
}

enum class WinningTxnType {
    CREDIT,
    DEBIT
}