package com.jar.app.feature_vasooli.impl.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class Borrower(
    @SerialName("loanId")
    val loanId: String,

    @SerialName("borrowerName")
    val borrowerName: String? = null,

    @SerialName("dueAmount")
    val dueAmount: Int,

    @SerialName("lastRepaymentAmount")
    val lastRepaymentAmount: Int? = null,

    @SerialName("borrowedAmount")
    val borrowedAmount: Int,

    @SerialName("lastRepaymentDate")
    val lastRepaymentDate: Long? = null,

    @SerialName("lastReminderSentOn")
    val lastReminderSentOn: String? = null,

    @SerialName("dueDate")
    val dueDate: Long? = null,

    @SerialName("lentOn")
    val lentOn: Long? = null,

    @SerialName("borrowerPhoneNumber")
    val borrowerPhoneNumber: String? = null,

    @SerialName("borrowerPhoneCountryCode")
    val borrowerCountryCode: String? = null,

    @SerialName("status")
    val status: String
) : Parcelable

enum class VasooliStatus {
    ACTIVE,
    DEFAULT,
    RECOVERED,
    PARTIALLY_RECOVERED
}