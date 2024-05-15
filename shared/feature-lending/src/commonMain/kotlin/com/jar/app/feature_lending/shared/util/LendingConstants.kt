package com.jar.app.feature_lending.shared.util

import com.jar.app.core_base.util.BaseConstants

public object LendingConstants {
    const val MAX_PDFS_ALLOWED_TO_UPLOAD = 6
    const val ADDRESS_CATEGORY = "CURRENT"

    const val CHARGE_TYPE_GST = "GST"
    const val CHARGE_TYPE_PROCESSING_FEE = "Processing Fees"
    const val STATUS_SCREEN_ACTION = "ScreenAction"
    const val SCREEN_ACTION_DONE = "Done"
    const val SCREEN_ACTION_PAY_AGAIN = "PayAgain"
    const val WITHDRAWAL_ERROR_CODE = "6018"
    const val BANK_VERIFICATION_ERROR_CODE = "6019"

    object LendingApplicationCheckpoints {
        const val EMPLOYMENT_DETAILS = "EMPLOYMENT_DETAILS"
        const val ADDRESS = "ADDRESS"
        const val LOAN_DETAILS = "LOAN_DETAILS"
        const val DRAW_DOWN = "DRAW_DOWN"
        const val KYC = "KYC"
        const val KYC_CONSENT = "KYC_CONSENT"
        const val CKYC = "CKYC"
        const val BANK_ACCOUNT_DETAILS = "BANK_ACCOUNT_DETAILS"
        const val BANK_VERIFICATION = "BANK_VERIFICATION"
        const val LOAN_SUMMARY = "LOAN_SUMMARY"
        const val MANDATE_SETUP = "MANDATE_SETUP"
        const val LOAN_AGREEMENT = "LOAN_AGREEMENT"
        const val ELIGIBILITY = "ELIGIBILITY"
        const val LEAD_CREATION = "LEAD_CREATION"
        const val WITHDRAWAL = "WITHDRAWAL"
        const val FORECLOSURE = "FORECLOSURE"
        const val READY_CASH_DETAILS = "READY_CASH_DETAILS"
    }

    object LendingConfirmDetails {
        const val LENDING_CONFIRM_DETAILS_CTA_REQUEST_KEY =
            "lendingConfirmDetailsCtaSelectionRequestKey"
        const val LENDING_CONFIRM_DETAILS_SELECTED_CTA = "lendingConfirmDetailsSelectedCta"
        const val LENDING_CONFIRM_DETAILS_POSITIVE_CTA = "lendingConfirmDetailsPositiveCta"
        const val LENDING_CONFIRM_DETAILS_NEGATIVE_CTA = "lendingConfirmDetailsNegativeCta"
    }

    object StaticContentType {
        const val LOAN_NAME_CHIPS = "LOAN_NAME_CHIPS"
        const val PERSONAL_LOAN_ELIGIBILITY_FAQ = "PERSONAL_LOAN_ELIGIBILITY_FAQ"
        const val LENDER_LOGO = "LENDER_LOGO"
        const val EXPERIAN_LOGO = "EXPERIAN_LOGO"
        const val NPCI_LOGO = "NPCI_LOGO"
        const val AADHAAR_SCREEN = "AADHAAR_SCREEN"
        const val CONFIRM_KYC = "CONFIRM_KYC"
        const val BANK_SCREEN = "BANK_SCREEN"
        const val READY_CASH_BREAKDOWN_DESCRIPTION = "READY_CASH_BREAKDOWN_DESCRIPTION"
        const val FEES_AND_CHARGES_DESCRIPTION = "FEES_AND_CHARGES_DESCRIPTION"
        const val LOAN_AGREEMENT = "LOAN_AGREEMENT"
        const val APPLICATION_REJECTED = "APPLICATION_REJECTED"
        const val MANDATE_SETUP_CONTENT = "MANDATE_SETUP_CONTENT"
        const val LENDER_ELIGIBILITY_RANGE = "LENDER_ELIGIBILITY_RANGE"
        const val REPAYMENT_EMI_SCREEN = "REPAYMENT_EMI_SCREEN"
        const val REPAYMENT_TRANSACTION_SCREEN = "REPAYMENT_TRANSACTION_SCREEN"
        const val LANDING_SCREEN = "LANDING_SCREEN"
        const val APP_UNDER_MAINTENANCE = "APP_UNDER_MAINTENANCE"
        const val REALTIME_LENDING = "REALTIME_LENDING"
        const val REALTIME_LENDING_BANK_DETAILS_STEPS = "REALTIME_LENDING_BANK_DETAILS"
        const val MANDATE_SETUP_UPDATED_CONTENT = "MANDATE_SETUP_UPDATED_CONTENT"
        const val MANDATE_SETUP_FAILURE_CONTENT = "MANDATE_SETUP_FAILURE_CONTENT"
        const val CREDIT_REPORT_EXISTENCE = "CREDIT_REPORT_EXISTENCE"
    }

    object AccountType {
        const val ACCOUNT_TYPE_CURRENT = "CURRENT"
        const val ACCOUNT_TYPE_SAVINGS = "SAVINGS"
    }

    object MandateAuthType {
        const val NET_BANKING = "NET_BANKING"
        const val DEBIT_CARD = "DEBIT_CARD"
    }

    object LottieUrls {
        const val GENERIC_LOADING =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/Lending_Kyc/loading.json" //HourGlass animation
        const val GENERIC_ERROR =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/Lending_Kyc/generic-error.json" // RedCircle Error
        const val TICK_WITH_CELEBRATION =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/Lending_Kyc/tick_with _celebration.json"
        const val SMALL_CHECK =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/Lending_Kyc/small_check.json" //Green small check
        const val SEARCHING_LOADER =
            "https://assets8.lottiefiles.com/packages/lf20_bryykdkb.json" //Magnifying glass animation searching
        const val ELIGIBILITY_LOADING =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/Lending_Feature/processing_rupee.json" // Coin with progress bar
        const val COIN_RAIN =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/Lending_Feature/coin_rain.json" //Coin rain animation
        const val FETCH_LOCATION =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/GoldSip/fetching_location.json" //Fetching location animation
        const val ROTATING_COIN =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/Lending_Feature/rupee_coin_rotating.json" //Rotating coin lottie
        const val GET_MORE_LOAN =
            BaseConstants.CDN_BASE_URL + "/LottieFiles/Lending_Feature/repeat_loan_card_animation.lottie" //Get More loan animation
    }

    object ImageUrls {
        const val IMAGE_LAUNCHING_SOON =
            BaseConstants.CDN_BASE_URL + "/lending/images/emi_cal.webp"
        const val IMAGE_STARS_WITH_CHECK =
            BaseConstants.CDN_BASE_URL + "/lending/images/emi_cal_background.webp"
    }

    object ErrorCodesLending {
        object LendingEligibility {
            const val SERVER_DOWN = "200000"
            const val UNABLE_TO_CHECK_AT_THE_MOMENT = "200001"
            const val RETRY_LIMIT_EXHAUSTED = "200009"
        }

        object LendingOtp {
            const val OTP_SEND_LIMIT_EXHAUSTED = "6003"
        }
    }

    object TransitionType {
        const val OTP_SUCCESS = "OTP_SUCCESS"
        const val APPLICATION_SUCCESS = "APPLICATION_SUCCESS"
        const val ALL_DONE = "ALL_DONE"
    }

    object OtpVerificationRequest {
        const val OTP_VERIFICATION_REQUEST_KEY = "otpVerificationRequestKey"
        const val OTP_VERIFICATION_REQUEST_RESULT = "otpVerificationRequestResult"
        const val OTP_VERIFICATION_REQUEST_SUCCESS = "otpVerificationRequestSuccess"
        const val OTP_VERIFICATION_REQUEST_CANCELLED = "otpVerificationRequestCancelled"
        const val OTP_VERIFICATION_REQUEST_EXHAUSTED = "otpVerificationRequestExhausted"
        const val OTP_VERIFICATION_WITHDRAWAL_ERROR = "otpVerificationWithdrawalError"
    }

    internal object Endpoints {
        const val FETCH_LOAN_APPLICATIONS = "v1/api/loan/details"
        const val UPDATE_EMPLOYMENT_DETAIL = "v1/api/loan/application"
        const val UPDATE_ADDRESS_DETAIL = "v1/api/loan/application"
        const val FETCH_LENDING_ELIGIBILITY = "v1/api/loan/eligibility"
        const val FETCH_LOAN_REASONS = "v2/api/dashboard/static"
        const val FETCH_LENDING_FAQ = "v2/api/dashboard/static"
        const val VALIDATE_IFSC_CODE = "v2/api/loan/validate/ifsc"
        const val VALIDATE_BANK_ACCOUNT = "v1/api/loan/bankAccount"
        const val UPDATE_DRAW_DOWN = "v1/api/loan/confirmation"
        const val FETCH_LOAN_AGREEMENT = "v1/api/loan/agreement"
        const val REQUEST_LENDING_OTP = "v2/api/otp/send"
        const val VERIFY_LENDING_OTP = "v2/api/otp/verify"
        const val FETCH_MANDATE_LINK = "v1/api/loan/mandate"
        const val FETCH_PRE_APPROVED_DATA = "v2/api/loan/preApprovedDetails"
        const val FETCH_EMI_PLANS = "v2/api/loan/emi/calculator"
        const val FETCH_LOAN_APPLICATION_LIST = "v2/api/loan/applicationList"
        const val UPDATE_LOAN_DETAILS = "v2/api/loan/application"
        const val FETCH_LENDING_STATIC_CONTENT = "v2/api/loan/static"
        const val FETCH_LOAN_DETAILS = "v2/api/loan/details"
        const val FETCH_LOAN_APPLICATION_STATUS = "v2/api/loan/status"
        const val INITIATE_FORECLOSURE_PAYMENT = "v2/api/payments/initiate"
        const val FETCH_REPAYMENT_DETAILS = "v2/api/loan/repayment/getRepaymentDetails"
        const val FETCH_READYCASH_LANDING_SCREEN_DATA = "v2/api/loan/static"
        const val FETCH_EMI_TRANSACTION_HISTORY = "v2/api/loan/repayment/getEmiTransactionHistory"
        const val FETCH_TRANSACTION_DETAILS = "v2/api/loan/repayment/getTransactionDetails"
        const val FETCH_READY_CASH_JOURNEY = "v2/api/loan/journey"
        const val SCHEDULE_USER_NOTIFICATION = "v2/api/loan/maintenance/notification"
        const val UPLOAD_BANK_STATEMENT = " /v2/api/loan/realTime/uploadBankStatement"
        const val GET_UPLOADED_BANK_STATEMENTS = "/v2/api/loan/realTime/bankDetails"
        const val UPDATE_BANK_DETAIL = "v2/api/loan/realTime/updateBankDetails"
        const val SET_BANK_STATEMENT_PASSWORD = "v2/api/loan/realTime/updateBankStatement"
        const val REAL_TIME_CREDIT_DETAILS = "v2/api/loan/realTime/creditDetails"
        const val REAL_TIME_LEAD_STATUS = "v2/api/loan/realTime/leadStatus"
        const val FETCH_CAMS_BANKS = "v2/api/loan/getCamsBanks"
        const val FETCH_CAMS_SDK_REDIRECT_DATA = "v2/api/loan/getCamsSdkRedirectData"
        const val FETCH_CAMS_DATA_STATUS = "v2/api/loan/getCamsDataStatus"
        const val UPDATE_CAMS_DOWNTIME = "v2/api/loan/setCamsDowntime"
        const val FETCH_PAN_STATUS = "v2/api/loan/getPanStatus"
        const val FETCH_EXPERIAN_REPORT = "/v2/api/credit/fetchReport"
        const val ACKNOWLEDGE_ONE_TIME_CARD = "v2/api/loan/removeCard"
        const val CREDIT_REPORT_SUMMARY = "/v2/api/credit/report/summary"
        const val CREDIT_DETAILED_REPORT = "/v2/api/credit/detailed/report"
        const val CREDIT_REPORT_SUMMARY_REFRESH = "/v2/api/credit/report/refresh"
    }

    object TransactionType {
        const val EMI = "emi"
        const val TRANSACTION = "transaction"
    }

    object OneTimeCardType {
        const val BANK_VERIFICATION = "BANK_VERIFICATION"
    }

    object RealTimeLeadStatus {
        const val BANK_DETAILS_SUBMITTED = "BANK_DETAILS_SUBMITTED"
        const val BANK_STATEMENT_UPLOADED = "BANK_STATEMENT_UPLOADED"
        const val PARTIAL_BANK_STATEMENT_UPLOADED = "PARTIAL_BANK_STATEMENT_UPLOADED"
    }
}

enum class LendingFlowType {
    KYC,
    PERSONAL_DETAILS,
    LOAN_APPLICATION,
    AGREEMENT,
}