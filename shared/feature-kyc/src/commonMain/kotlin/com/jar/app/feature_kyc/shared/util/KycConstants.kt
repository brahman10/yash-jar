package com.jar.app.feature_kyc.shared.util

object KycConstants {

    const val PART = "part"

    const val SELFIE = "selfie"

    const val KYC_DOC_LIST_PARAM = "6"

    const val KYC_FLOW_STATE = "KYC_FLOW_STATE"

    internal object Endpoints {
        const val FETCH_KYC_DETAILS = "v1/api/user/kyc"
        const val FETCH_KYC_FAQ = "v2/api/dashboard/static"
        const val POST_MANUAL_KYC_REQUEST = "v1/api/kyc/verify"
        const val FETCH_DOCUMENT_LIST = "v1/api/dashboard/static"
        const val POST_OCR_REQUEST = "v1/api/kyc/ocr"
        const val POST_PAN_OCR_REQUEST = "v1/api/kyc/ocr"
        const val POST_FACE_MATCH_REQUEST = "v1/api/kyc/faceMatch"
    }

    object AnalyticsKeys {
        const val OPTION_CHOSEN = "optionChosen"
        const val BUTTON = "button"
        const val ERROR_MESSAGE = "errorMessage"
        const val RESULT_SHOWN = "resultShown"
        const val SHOWN_IDENTITY_VERIFICATION_SCREEN = "Shown_IdentityVerificationScreen"
        const val CLICKED_BACK_IDENTITY_VERIFICATION_SCREEN =
            "Clicked_BackArrow_IdentityVerificationScreen"
        const val SHOWN_FAQ_SECTION_IDENTITY_VERIFICATION_SCREEN =
            "Shown_FAQSection_IdentityVerificationScreen"
        const val CLICKED_BUTTON_IDENTITY_VERIFICATION_SCREEN =
            "Clicked_Button_IdentityVerificationScreen"
        const val SHOWN_UPLOAD_PAN_CARD_SCREEN = "Shown_UploadPANcardScreen"
        const val CLICKED_BUTTON_UPLOAD_PAN_CARD_SCREEN = "Clicked_Button_UploadPANcardScreen"
        const val CLICKED_BOTTOM_SHEET_BUTTON_UPLOAD_DOCUMENT_ERROR_SCREEN =
            "Clicked_BottomSheetButton_UploadDocumentErrorScreen"
        const val SHOWN_ENTER_PAN_CARD_DETAILS_SCREEN = "Shown_EnterPANdetailsScreen"
        const val CLICKED_VERIFY_PAN_BUTTON_ENTER_PAN_CARD_DETAILS_SCREEN =
            "Clicked_VerifyPANDetailsButton_EnterPANdetailsScreen"
        const val SHOWN_CHOOSE_DOCUMENT_SCREEN = "Shown_ChooseDocumentScreen"
        const val CLICKED_BUTTON = "Clicked_Button"
        const val SHOWN_UPLOAD_DOCUMENT_SCREEN = "Shown_UploadDocumentScreen"
        const val CLICKED_BUTTON_UPLOAD_DOCUMENT_SCREEN = "Clicked_Button_UploadDocumentScreen"
        const val CLICKED_TAKE_SELFIE_BUTTON_PHOTO_VERIFICATION_SCREEN = "Clicked_TakeSelfieButton_PhotoVerificationScreen"
        const val PhotoVerificationDetails_Screen_Shown = "PhotoVerificationDetails_Screen_Shown"
        const val SHOWN_PHOTO_VERIFICATION_ISSUE_SCREEN = "Shown_PhotoVerificationIssueScreen"
        const val CLICKED_RETAKE_SELFIE_BUTTON_PHOTO_VERIFICATION_ISSUE_SCREEN =
            "Clicked_RetakeSelfieButton_PhotoVerificationIssueScreen"
        const val CLICKED_GO_TO_HOME_BUTTON_IDENTITY_VERIFICATION_SCREEN =
            "Clicked_GotoHomeButton_IdentityVerificationScreen"
        const val SHOWN_IDENTITY_VERIFICATION__RESULT_SCREEN =
            "Shown_IdentityVerificationResultScreen"
        const val CLICKED_BUTTON_IDENTITY_VERIFICATION_RESULT_SCREEN =
            "Clicked_Button_IdentityVerificationResultScreen"
        const val SHOWN_ID_DETAILS_IDENTITY_VERIFICATION_SCREEN =
            "Shown_IDDetails_IdentityVerificationScreen"
        const val SHOWN_UPLOAD_DOCUMENT_ERROR_SCREEN = "Shown_UploadDocumentErrorScreen"
        const val CLICKED_RETAKE_PHOTO_UPLOAD_DOCUMENT_ERROR_SCREEN =
            "Clicked_RetakePhoto_UploadDocumentErrorScreen"
        const val PhotoVerificationError_Screen_Shown = "PhotoVerificationError_Screen_Shown"
        const val PhotoVerificationDetails_Screen_Clicked = "PhotoVerificationDetails_Screen_Clicked"
        const val ButtonClicked = "ButtonClicked"
        const val ChooseFromGallery = "ChooseFromGallery"
        const val TakePhoto = "TakePhoto"
        const val PhotoVerificationError_Screen_Clicked = "PhotoVerificationError_Screen_Clicked"
        const val Retake = "Retake"
        const val IdentityVerificationSuccess_Screen_Clicked = "IdentityVerificationSuccess_Screen_Clicked"
        const val Withdrawal_ZeroBalanceDS_Popup = "Withdrawal_ZeroBalanceDS_Popup"
        const val Withdrawal_ZeroBalanceDS_Clicked = "Withdrawal_ZeroBalanceDS_Clicked"
        const val StartInvesting = "StartInvesting"
        const val Status = "Status"
    }

}