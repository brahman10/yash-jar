package com.jar.app.feature_lending.shared.domain.model.realTimeFlow

import dev.icerock.moko.resources.StringResource

data class RefreshCreditScoreBottomSheetUiState(
    val name: String = "",
    val mobileNo: String = "",
    val panNo: String = "",
    val isPanReadOnly: Boolean = false,
    val showPanError: Boolean = false,
    val panErrorMessageId: StringResource = com.jar.app.feature_lending.shared.MR.strings.feature_lending_blank,
    val isButtonEnabled: Boolean = false,
    val showOtpScreen: Boolean = false,
    val isOtpResendClickable: Boolean = false,
    val otp: String = "",
    val experianConsentRequired: Boolean = false,//for redirection
    val validityInSeconds: Int = 0,
    val resentOTPInSeconds: Int = 0
) {
    fun shouldEnableButton(updatedName: String? = null): Boolean {
        val userName = updatedName ?: name
        return userName.length >= 3 && mobileNo.isNotEmpty() && panNo.length==10
    }
    fun shouldEnableButtonForPan(panNos: String? = null): Boolean {
        val panNumber = panNos ?: panNo
        return name.length >= 3 && mobileNo.isNotEmpty() && panNumber.length==10
    }
}
