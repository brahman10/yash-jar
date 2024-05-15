package com.jar.app.feature_lending_kyc.shared.util

object LendingKycConstants {

    const val AADHAAR_STRING = "aadhaar"
    const val DIGILOCKER_STRING = "DIGILOCKER_SCREEN"
    const val DIGILOCKER_PAN_AADHAR_MISMATCH_ERROR = "pan_aadhar_mismatch"
    const val DIGILOCKER_TIMEOUT_ERROR = "timeout_error"


    enum class LendingKycOtpVerificationFlowType {
        EMAIL,
        CREDIT_REPORT,
        AADHAAR,
        SELFIE
    }

    enum class PanFlowType {
        //When user is directly coming from the lending happy flow
        LENDING_FLOW,

        //When user is resuming the flow when credit report was fetched
        CREDIT_REPORT,
        JAR_VERIFIED,
        MANUAL,
        IMAGE,
        BACK_FLOW
    }

    object IllustrationUrls {
        const val KYC_DOC_URL = "https://d21tpkh2l1zb46.cloudfront.net/AppGuides/kycDocIcon1.png"
        const val AADHAAR_NOT_LINKED_WITH_NUMBER =
            "/Images/Lending_Kyc/aadhaar_not_linked_with_number.png"
        const val AADHAAR_PLACEHOLDER_URL = "/Images/Lending_Kyc/aadhaar_placeholder.png"
        const val PAN_PLACEHOLDER_URL = "/Images/Lending_Kyc/pan_placeholder.png"

        const val PAN_IS_BLURRED_URL = "/Images/Lending_Kyc/pan_card_is_blurred.png"
        const val PAN_CARD_NOT_DETECTED_URL = "/Images/Lending_Kyc/pan_card_not_detected.png"
        const val PAN_CARD_IS_NOT_IN_FRAME = "/Images/Lending_Kyc/pan_card_did_not_fit_in_frame.png"
        const val AADHAAR_CARD_NOT_DETECTED_URL =
            "/Images/Lending_Kyc/aadhaar_card_not_detected.png"
        const val AADHAAR_CARD_IS_NOT_IN_FRAME =
            "/Images/Lending_Kyc/aadhar_card_is_not_in_frame.png"
        const val AADHAAR_CARD_BLURRED_URL = "/Images/Lending_Kyc/aadhar_card_is_blurred.png"
        const val AADHAAR_SERVER_DOWN_URL = "/Images/Lending_Kyc/aadhar_server_down.png"
        const val CREDIT_REPORT_NOT_FETCHED_URL =
            "/Images/Lending_Kyc/credit_report_not_fetched.png"
        const val EMAIL_NOT_SENT_URL = "/Images/Lending_Kyc/email_not_sent.png"
        const val INVALID_PAN_URL = "/Images/Lending_Kyc/invalid_pan.png"
        const val LENDING_KYC_ONBOARDING_URL = "/Images/Lending_Kyc/lending_kyc_onboarding.png"
        const val LENDING_KYC_ONBOARDING_NEW_URL =
            "/Images/Lending_Kyc/lending_kyc_onboarding_new.png"

        const val MASKED_FACE_SELFIE_URL = "/Images/Lending_Kyc/masked_face_selfie.png"
        const val BLURRED_FACE_SELFIE_URL = "/Images/Lending_Kyc/blur_face_selfie.png"
        const val CLEAR_FACE_SELFIE_URL = "/Images/Lending_Kyc/clear_face_selfie.png"
        const val SELFIE_EYES_CLOSED = "/Images/Lending_Kyc/selfie_eyes_closed.png"
        const val SELFIE_FACE_NOT_DETECTED = "/Images/Lending_Kyc/selfie_face_not_detected.png"
        const val SELFIE_LOW_QUALITY = "/Images/Lending_Kyc/selfie_low_quality.png"
        const val SELFIE_NO_IMAGE_FOUND = "/Images/Lending_Kyc/selfie_no_image_found.png"
    }

    object LottieUrls {
        const val GENERIC_ERROR = "/LottieFiles/Lending_Kyc/generic-error.json"
        const val PAPER_STACK = "/LottieFiles/Lending_Kyc/paper-stack.json"
        const val GENERIC_LOADING = "/LottieFiles/Lending_Kyc/loading.json"
        const val SMALL_CHECK = "/LottieFiles/Lending_Kyc/small_check.json"
        const val TICK_WITH_CELEBRATION = "/LottieFiles/Lending_Kyc/tick_with _celebration.json"
        const val VERIFYING = "/LottieFiles/Lending_Kyc/verify.json"
    }

    internal object Endpoints {
        const val REQUEST_EMAIL_OTP = "v1/api/kyc/verify/email"
        const val VERIFY_EMAIL_OTP = "v1/api/kyc/verify/email"
        const val FETCH_EMAIL_DELIVERY_STATUS = "v1/api/kyc/verify/email"
        const val REQUEST_CREDIT_REPORT_OTP = "v2/api/loan/requestExperianOTP"
        const val VERIFY_CREDIT_REPORT_OTP = "v2/api/loan/validateExperianOTP"
        const val REQUEST_CREDIT_REPORT_OTP_V2 = "v2/api/otp/send"
        const val VERIFY_CREDIT_REPORT_OTP_V2 = "v2/api/otp/verify"
        const val FETCH_JAR_VERIFIED_USER_PAN = "v1/api/kyc/getJarVerifiedUserPAN"
        const val SEARCH_KYC_DETAILS = "v1/api/kyc/searchKYCDetails"
        const val FETCH_KYC_AADHAAR_DETAILS = "v1/api/kyc/downloadKYCDetails"
        const val FETCH_AADHAAR_CAPTCHA = "v1/api/kyc/aadhaar/captcha"
        const val REQUEST_AADHAAR_OTP = "v1/api/kyc/aadhaar/send/otp"
        const val VERIFY_AADHAAR_OTP = "v1/api/kyc/aadhaar/verify/otp"
        const val VERIFY_SELFIE = "v2/api/kyc/selfieMatch"
        const val FETCH_KYC_PROGRESS = "v1/api/kyc/progress"
        const val SAVE_PAN_DETAILS = "v1/api/kyc/savePanDetails"
        const val VERIFY_PAN_DETAILS = "v1/api/kyc/verifyPanDetails"
        const val SAVE_AADHAAR_DETAILS = "v1/api/kyc/saveAadhaarDetails"
        const val FETCH_EXPERIAN_T_N_C = "v2/api/dashboard/static"
        const val FETCH_LENDING_KYC_FAQ_LIST = "v2/api/dashboard/static"
        const val FETCH_LENDING_KYC_FAQ_DETAIL = "v2/api/dashboard/static"
        const val FETCH_EXPERIAN_CONSENT = "v2/api/dashboard/static"
        const val VERIFY_AADHAAR_PAN_LINKAGE = "v1/api/kyc/verifyAadhaarPanLinkage"
        const val FETCH_DIGILOCKER_SCREEN_CONTENT = " /v2/api/loan/static"
        const val UPDATE_DIGILOCKER_REDIRECT_DATA  = "v2/api/kyc/aadhaar/digilocker/redirectData"
        const val FETCH_DIGILOCKER_VERIFICATION_URL  = "/v1/api/kyc/aadhaar/digilocker"
        const val FETCH_DIGILOCKER_VERIFICATION_STATUS  = "v1/api/kyc/aadhaar/digilocker/status"
        const val FETCH_DIGILOCKER_VERIFICATION_URL_V2  = "/v2/api/kyc/aadhaar/digilocker"
        const val FETCH_DIGILOCKER_VERIFICATION_STATUS_V2  = "v2/api/kyc/aadhaar/digilocker/status"
    }

}

enum class LendingKycFlowType {
    EMAIL,
    PAN,
    AADHAAR,
    SELFIE
}