package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.feature_transactions_common.shared.CommonTransactionStatus
import com.jar.app.feature_transactions_common.shared.CommonTransactionValueType
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.ColorResource
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class TransactionData(
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("assetTransactionId")
    val assetTransactionId: String? = null,
    @SerialName("currentStatus")
    val currentStatus: String? = null,
    @SerialName("date")
    val date: String? = null,
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("productId")
    val productId: String? = null,
    @SerialName("sourceType")
    val sourceType: String? = null,
    @SerialName("statusEnum")
    val statusEnum: String? = null,
    @SerialName("subTitle")
    val subTitle: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("volume")
    val volume: Float? = null,

    @SerialName("valueType")
    private val valueType: String? = null,

    //TODO : To be removed once we move to New Transaction Tab changes
    @SerialName("transactionListDetailsType")
    private val transactionListDetailsType: String? = null,

    @SerialName("orderedInLast24Hrs")
    val orderedInLast24Hrs: Boolean? = null
) : Parcelable {

    //TODO : To be removed once we move to New Transaction Tab changes
    fun getTransactionListDetailsType() = TransactionListDetailsType.values().find { it.name == transactionListDetailsType } ?: TransactionListDetailsType.V4

    fun getValueType() = CommonTransactionValueType.values().find { it.name == valueType }
        ?: CommonTransactionValueType.AMOUNT

    fun getColorForStatus(): ColorResource {
        val transactionStatus = statusEnum?.uppercase()
        val status: CommonTransactionStatus =
            CommonTransactionStatus.values().find { it.name == transactionStatus }
                ?: CommonTransactionStatus.DEFAULT
        return status.getColor()
    }
}

enum class TransactionListDetailsType (val transactionScreenDeepLink: String) {
    V5("android-app://com.jar.app/newTransactionDetail"),
    V4("android-app://com.jar.app/transactionDetail")
}