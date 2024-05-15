package com.jar.app.feature_lending.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BankIfscDetails(
    @SerialName("ADDRESS")
    val ADDRESS: String,

    @SerialName("BANK")
    val BANK: String,

    @SerialName("BANKCODE")
    val BANKCODE: String,

    @SerialName("BRANCH")
    val BRANCH: String,

    @SerialName("CENTRE")
    val CENTRE: String,

    @SerialName("CITY")
    val CITY: String,

    @SerialName("CONTACT")
    val CONTACT: String,

    @SerialName("DISTRICT")
    val DISTRICT: String,

    @SerialName("IFSC")
    val IFSC: String,

    @SerialName("IMPS")
    val IMPS: Boolean,

    @SerialName("MICR")
    val MICR: String,

    @SerialName("NEFT")
    val NEFT: Boolean,

    @SerialName("RTGS")
    val RTGS: Boolean,

    @SerialName("STATE")
    val STATE: String,

    @SerialName("SWIFT")
    val SWIFT: String,

    @SerialName("UPI")
    val UPI: Boolean,

    @SerialName("bankLogo")
    val icon: String
)