package com.jar.app.feature_savings_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class UserSavingsDetails(
    @SerialName("bankLogo")
    val bankLogo: String? = null,
    @SerialName("bankName")
    val bankName: String? = null,
    @SerialName("enabled")
    val enabled: Boolean? = null,
    @SerialName("nextDeductionDate")
    val nextDeductionDate: Long? = null,
    @SerialName("pauseStatus")
    val pauseStatus: SavingsPauseStatusData? = null,
    @SerialName("provider")
    val provider: String? = null,
    @SerialName("subsState")
    val subsState: String? = null,
    @SerialName("subscriptionStatus")
    val subscriptionStatus: String? = null,
    @SerialName("subscriptionAmount")
    val subscriptionAmount: Float,
    @SerialName("subscriptionDay")
    val subscriptionDay: Int? = null,
    @SerialName("subscriptionId")
    val subscriptionId: String? = null,
    @SerialName("subscriptionType")
    val subscriptionType: String? = null,
    @SerialName("updateDate")
    val updateDate: Long? = null,
    @SerialName("upiId")
    val upiId: String? = null,
    @SerialName("manualPayment")
    val manualPaymentDetails: ManualPaymentInfo? = null,
    @SerialName("mandateAmount")
    val mandateAmount: Float? = null,
    @SerialName("autoSaveEnabled")
    val autoSaveEnabled: Boolean? = null,
    @SerialName("roundOffTo")
    val roundOffTo: RoundOffTo? = null,
    @SerialName("autoInvestForNoSpends")
    val autoInvestForNoSpends: Boolean? = null,
    @SerialName("metaData")
    val savingsMetaData: SavingsMetaData? = null
) : Parcelable {
    fun getSubscriptionStatus(): SubscriptionStatus {
        return if (subscriptionStatus.isNullOrBlank())
            SubscriptionStatus.FAILURE
        else SubscriptionStatus.valueOf(subscriptionStatus)
    }
}
