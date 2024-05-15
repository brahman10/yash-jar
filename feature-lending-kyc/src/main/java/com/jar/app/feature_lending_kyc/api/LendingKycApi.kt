package com.jar.app.feature_lending_kyc.api

import androidx.navigation.NavController
import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.feature_lending_kyc.impl.domain.model.KYCScreenArgs
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN

interface LendingKycApi {

    /**
     * This function opens lending kyc flow and automatically handles the onboarding/resume kyc redirections
     */
    fun openLendingKyc(flowType: String, progressApiCallback: (String?) -> Unit)

    /**
     * This function also opens lending kyc flow but takes kyc response
     */
    fun openLendingKycWithProgressResponse(flowType: String, progressResponse: KycProgressResponse)

    /**
     * opens from experian otp BottomSheet if failed then redirected to manual pan entry
     */
    fun openPANFetchFlow(
        kycFeatureFlowType: KycFeatureFlowType = KycFeatureFlowType.UNKNOWN,
        fromScreen: String = "",
        childNavController: NavController,
        shouldOpenPanInBackground: Boolean = false,
        shouldNotifyAfterOtpSuccess: Boolean = false,
        nameForCreditReport: String? = null,
        panNumberForCreditReport: String? = null,
        lenderName: String? = null,
        apiStateCallback: ((Boolean, String?) -> Unit)? = null
    )

    /**
     * This will open when user is already verified PAN or fetched Experian details
     */
    fun openCreditReportFetchedScreen(
        creditReportPan: CreditReportPAN?,
        fromScreen: String,
        childNavController: NavController,
        kycFeatureFlowType: KycFeatureFlowType,
        lenderName: String? = null
    )

    /**
     * this function opens Aadhar verification flow
     */
    fun openAadharVerificationFlow(
        kycScreenArgs: KYCScreenArgs,
        childNavController: NavController,
        apiCallback: ((Boolean, String?) -> Unit)? = null
    )

    /**
     * this function opens Selfie verification flow
     */
    fun openSelfieFlow(
        kycScreenArgs: KYCScreenArgs,
        childNavController: NavController,
        apiCallback: ((Boolean, String?) -> Unit)? = null
    )
}