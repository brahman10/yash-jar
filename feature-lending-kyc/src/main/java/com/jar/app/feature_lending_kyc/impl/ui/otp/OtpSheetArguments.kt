package com.jar.app.feature_lending_kyc.impl.ui.otp

import android.os.Parcelable
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class OtpSheetArguments(
    @SerialName("flowType")
    val flowType: LendingKycConstants.LendingKycOtpVerificationFlowType,
    @SerialName("expiresInTime")
    val expiresInTime: Long,
    @SerialName("resendTime")
    val resendTime: Long,
    @SerialName("email")
    val email: String? = null,
    @SerialName("emailMessageId")
    val emailMessageId: String? = null,
    @SerialName("aadhaarSessionId")
    val aadhaarSessionId: String? = null,
    @SerialName("aadhaarNumber")
    val aadhaarNumber: String? = null,
    @SerialName("fromScreen")
    val fromScreen: String,
    @SerialName("lenderName")
    val lenderName: String? = null,
    @SerialName("kycFeatureFlowType")
    val kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN,
    @SerialName("shouldNotifyAfterOtpSuccess")
    val shouldNotifyAfterOtpSuccess: Boolean = false,
    @SerialName("nameForCreditReport")
    val nameForCreditReport:String? = null,
    @SerialName("panNumberForCreditReport")
    val panNumberForCreditReport:String? = null
) : Parcelable
