package com.jar.app.feature_lending.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LendingFlowStatusResponse(
    @SerialName("applicationId")
    val applicationId: String,
    @SerialName("checkpoints")
    val checkpoints: CheckpointStatus,
    @SerialName("status")
    val status: String
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class CheckpointStatus(
    @SerialName("employment")
    val employment: String? = null,
    @SerialName("pan")
    val pan: String? = null,
    @SerialName("drawdown")
    val drawdown: String? = null,
    @SerialName("aadhaar")
    val aadhaar: String? = null,
    @SerialName("selfie")
    val selfie: String? = null,
    @SerialName("kyc")
    val kyc: String? = null,
    @SerialName("kycVerificationConsent")
    val kycVerificationConsent: String? = null,
    @SerialName("bankAccount")
    val bankAccount: String? = null,
    @SerialName("loanDetails")
    val loanDetails: String? = null,
    @SerialName("mandateSetup")
    val mandateSetup: String? = null,
    @SerialName("loanAgreement")
    val loanAgreement: String? = null,
    @SerialName("leadCreation")
    val leadCreation: String? = null,
    @SerialName("eligibility")
    val eligibility: String? = null,
    @SerialName("withdrawal")
    val withdrawal: String? = null
) : Parcelable