package com.jar.app.feature_lending.shared.domain.model.creditReport
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditDetailedReportResponse(
    @SerialName("title") val title: String? = null,
    @SerialName("titleColor") val titleColor: String? = null,
    @SerialName("subTitle") val subTitle: String? = null,
    @SerialName("subTitleIcon") val subTitleIcon: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("creditCards") val creditCardsList: List<CreditCardsAndLoan>? = null,
    @SerialName("loanAccounts") val loanAccountsList: List<CreditCardsAndLoan>? = null,
)

@Serializable
data class CreditCardsAndLoan(
    @SerialName("accountDetails") val accountDetails: AccountDetails? = null,
    @SerialName("details") val detailsList: List<Details>? = null,
)

@Serializable
data class AccountDetails(
    @SerialName("title") val title: String? = null,
    @SerialName("subTitle") val subTitle: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusColor") val statusColor: String? = null,
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("paymentDate") val paymentDate: String? = null,
    @SerialName("active") val isActive: Boolean? = null,
)

@Serializable
data class Details(
    @SerialName("key") val key: String? = null,
    @SerialName("value") val value: String? = null,
    @SerialName("valueColor") val valueColor: String? = null
)