package com.jar.app.feature_sell_gold.shared.domain.models

import com.jar.app.feature_settings.domain.model.VerifyUpiResponse

data class UpiVerificationStatus(
    val isLoading: Boolean = false,
    val verifyUpiResponse: VerifyUpiResponse? = null,
    val isError: Boolean = false,
    val errorMessage: String = ""
)