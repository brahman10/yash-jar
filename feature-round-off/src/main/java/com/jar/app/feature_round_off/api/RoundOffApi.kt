package com.jar.app.feature_round_off.api

import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse


interface RoundOffApi {

    /**
     * Method to open round off Fragment
     * **/
    fun openRoundOffFragment()

    /**
     * Method to open round off Fragment
     * **/
    fun openRoundOffFlow()

    /**
     * Method to open round off Fragment
     * **/
    fun openRoundOffDetails(fromScreen: String? = null)

    /**
     * Method to open round off flow for setting up autopay
     * **/
    fun openRoundOffForAutoPaySetup(shouldRedirectToPreRoundOffAutopayScreen: Boolean = false, fromScreen: String = "DEFAULT")

    /**
     * Method to open round off flow for setting up autopay
     * **/
    fun openRoundOffPostPaymentStatusScreens(
        mandatePaymentResultFromSDK: MandatePaymentResultFromSDK,
        mandatePaymentStatusResponse: FetchMandatePaymentStatusResponse
    )
}