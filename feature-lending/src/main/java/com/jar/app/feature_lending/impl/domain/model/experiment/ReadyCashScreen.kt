package com.jar.app.feature_lending.impl.domain.model.experiment

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_lending.impl.ui.withdrawal_wait.LendingServerTimeOutOrPendingFragment
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus

internal object ReadyCashScreen {
    const val HOME_SCREEN = "HOME_PAGE"
    const val LANDING_SCREEN_OLD = "LANDING_SCREEN_OLD"
    const val LANDING_SCREEN_NEW = "LANDING_SCREEN_NEW"
    const val CHOOSE_AMOUNT = "CHOOSE_AMOUNT"
    const val EMI_SELECTION = "EMI_SELECTION"
    const val LOAN_SUMMARY = "LOAN_SUMMARY"
    const val EMPLOYMENT_DETAILS = "EMPLOYMENT_DETAILS"
    const val PAN = "PAN"
    const val AADHAAR = "AADHAAR"
    const val SELFIE = "SELFIE"
    const val KYC_CONSENT = "KYC_CONSENT"
    const val CKYC_CONSENT = "CKYC"
    const val BANK_ACCOUNT_DETAILS = "BANK_ACCOUNT_DETAILS"
    const val CONFIRM_BANK_ACCOUNT_DETAILS = "CONFIRM_BANK_ACCOUNT_DETAILS"
    const val BANK_VERIFICATION = "BANK_VERIFICATION"
    const val BANK_VERIFICATION_V2 = "BANK_VERIFICATION_V2"
    const val MANDATE_SETUP = "MANDATE_SETUP"
    const val LOAN_AGREEMENT = "LOAN_AGREEMENT"
    const val LOAN_SUMMARY_AND_AGREEMENT = "LOAN_SUMMARY_AND_AGREEMENT"
    const val READY_CASH_DETAILS = "READY_CASH_DETAILS"
    const val DISBURSAL = "DISBURSAL"
    const val LANDING_SCREEN_FROM_SELL_GOLD = "LANDING_SCREEN_FROM_SELL_GOLD"
    const val LANDING_REPEAT_WITHDRAWAL = "LANDING_REPEAT_WITHDRAWAL"

    fun getScreenNavigationUri(screen: String, args: String, status: String?) = when (screen) {
        LANDING_SCREEN_FROM_SELL_GOLD -> "android-app://com.jar.app/sellGoldLandingFragment/$args"
        LANDING_REPEAT_WITHDRAWAL -> "android-app://com.jar.app/lendingRepeatWithdrawal/$args"
        LANDING_SCREEN_OLD -> "android-app://com.jar.app/lendingEducationalIntroFragment/$args"
        LANDING_SCREEN_NEW -> "android-app://com.jar.app/readyCashLandingFragment/$args"
        CHOOSE_AMOUNT -> "android-app://com.jar.app/selectLoanAmount/$args"
        EMI_SELECTION -> "android-app://com.jar.app/selectEMIFragment/$args"
        LOAN_SUMMARY -> "android-app://com.jar.app/loanSummaryV2Fragment/$args"
        LOAN_SUMMARY_AND_AGREEMENT -> if (status == LoanStatus.PENDING.name)
            "android-app://com.jar.app/loanSummaryAndAgreementRetryFragment/$args"
        else "android-app://com.jar.app/loanSummaryV2Fragment/$args"
        EMPLOYMENT_DETAILS -> "android-app://com.jar.app/lendingEmploymentDetailsFragment/$args"
        //These 3 will be handled explicitly in fragment itself since they are in Separate module
        PAN -> PAN
        AADHAAR -> AADHAAR
        SELFIE -> SELFIE
        KYC_CONSENT -> "android-app://com.jar.app/kycConsent/$args"
        CKYC_CONSENT -> "android-app://com.jar.app/confirmCkycDetailsFragment/$args"
        BANK_ACCOUNT_DETAILS -> "android-app://com.jar.app/bankDetailsFragment/$args"
        CONFIRM_BANK_ACCOUNT_DETAILS -> "android-app://com.jar.app/confirmBankDetailFragment/$args"
        BANK_VERIFICATION -> if (status == LoanStatus.FAILED.name) {
            BANK_VERIFICATION
        } else if (status == LoanStatus.CALLBACK_PENDING.name)
            "android-app://com.jar.app/serverTimeOutOrPending/${LendingServerTimeOutOrPendingFragment.FLOW_TYPE_BANK_PENDING}/$args"
        else "android-app://com.jar.app/pennyDropVerification/$args"

        BANK_VERIFICATION_V2 -> if (status == LoanStatus.FAILED.name) {
            BANK_VERIFICATION_V2
        } else "android-app://com.jar.app/abflWaitingFragment/$args"

        MANDATE_SETUP ->if (status == LoanStatus.FAILED.name)
            "android-app://com.jar.app/loanMandateFailureFragment/$args"
            else if (status == LoanStatus.IN_PROGRESS.name)
            "android-app://com.jar.app/loanPendingFragment/$args"
        else "android-app://com.jar.app/loanMandateConsentFragment/$args"
        LOAN_AGREEMENT -> "android-app://com.jar.app/loanAgreementFragment/$args"
        READY_CASH_DETAILS -> "android-app://com.jar.app/loanReasonFragment/$args"
        DISBURSAL -> if (status == LoanStatus.PENDING.name)
            "android-app://com.jar.app/serverTimeOutOrPending/${LendingServerTimeOutOrPendingFragment.FLOW_TYPE_WITHDRAWAL_SERVER_TIME_OUT}/$args"
        else DISBURSAL     //Handled explicitly in fragment
        else -> "android-app://com.jar.app/readyCashLandingFragment/$args"
    }
}