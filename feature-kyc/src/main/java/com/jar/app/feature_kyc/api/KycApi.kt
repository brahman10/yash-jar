package com.jar.app.feature_kyc.api

import com.jar.app.feature_user_api.domain.model.UserKycStatus

/**
 * Kyc Api (to be used by other modules)
 * **/
interface KycApi {
    /**
     * Method to start setup KYC
     * **/
    fun openKYC(userKycStatus: UserKycStatus, fromScreen: String = "")

    /**
     * Method to start setup KYC
     * **/
    fun initiateUserIdVerification(
        fromScreen: String,
        shouldShowOnlyPan: Boolean,
        onKycFlowExecution: (String) -> Unit
    )
}