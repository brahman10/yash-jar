package com.jar.app.feature_lending_kyc.shared.util

object LendingKycEventKey {

    //PARAMS
    const val fromScreen = "fromScreen"
    const val optionChosen = "optionChosen"
    const val textDisplayed = "textDisplayed"
    const val errorMessage = "errorMessage"
    const val scenario = "scenario"
    const val textOnScreen = "textOnScreen"
    const val errorMsgShown = "errorMsgShown"
    const val messageShown = "messageShown"
    const val panNumber = "pan_number"
    const val errorType = "error_type"
    const val action = "action"
    const val check_box = "check_box"
    const val field_name = "field_name"
    const val status = "status"
    const val link = "link"
    const val lenderName = "lender_name"
    const val source = "source"
    const val linked_bank = "linked_bank"

    //Lending KYC On-boarding
    const val Shown_KYCLandingScreen = "Shown_KYCLandingScreen"
    const val Clicked_Button_KYCLandingScreen = "Clicked_Button_KYCLandingScreen"
    const val Shown_ExitBottomSheet = "Shown_ExitBottomSheet"
    const val Clicked_Button_ExitBottomSheet = "Clicked_Button_ExitBottomSheet"
    const val Shown_ContinueKYCBottomSheet = "Shown_ContinueKYCBottomSheet"
    const val Clicked_Button_ContinueKYCBottomSheet = "Clicked_Button_ContinueKYCBottomSheet"

    //Email Verification Screen
    const val Shown_EmailVerificationScreen = "Shown_EmailVerificationScreen"
    const val Clicked_Button_EmailVerificationScreen = "Clicked_Button_EmailVerificationScreen"
    const val Shown_EmailAddressEdgeCases = "Shown_EmailAddressEdgeCases"
    const val Shown_EmailVerificationSuccessfulScreen = "Shown_EmailVerificationSuccessfulScreen"

    //OTP Bottom Sheet
    const val Shown_EmailOTPVerificationBottomSheet = "Shown_EmailOTPVerificationBottomSheet"
    const val Clicked_Buttons_EmailOTPVerificationBottomSheet =
        "Clicked_Buttons_EmailOTPVerificationBottomSheet"
    const val Shown_EmailOTPAttemptLimitExceededBottomSheet =
        "Shown_EmailOTPAttemptLimitExceededBottomSheet"
    const val Shown_EmailOTPEdgeCases = "Shown_EmailOTPEdgeCases"

    //Experian Credit Report OTP bottom sheet
    const val Shown_EnterExperianOTPScreen = "Shown_EnterExperianOTPScreen"
    const val Clicked_Button_EnterExperianOTPBottomSheet =
        "Clicked_Button_EnterExperianOTPBottomSheet"
    const val Shown_ExperianTnCScreenBottomSheet = "Shown_ExperianTnCScreenBottomSheet"
    const val Shown_LookingForCreditReportScreen = "Shown_LookingForCreditReportScreen"
    const val Shown_CreditReportFetchedScreen = "Shown_CreditReportFetchedScreen"

    //PAN Screens
    const val Shown_ConfirmYourPANScreen = "Shown_ConfirmYourPANScreen"
    const val Clicked_Button_ConfirmYourPANScreen = "Clicked_Button_ConfirmYourPANScreen"
    const val Shown_PANVerifyingDetailsScreen = "Shown_PANVerifyingDetailsScreen"
    const val Shown_PANVerificationFailureScreen = "Shown_PANVerificationFailureScreen"
    const val Shown_EnterPANNumberScreen = "Shown_EnterPANNumberScreen"
    const val Shown_CreditReportNotFoundScreen = "Shown_CreditReportNotFoundScreen"
    const val Shown_PANVerificationSuccessfulScreen = "Shown_PANVerificationSuccessfulScreen"
    const val Clicked_Button_EnterPANNumberScreen = "Clicked_Button_EnterPANNumberScreen"
    const val Shown_PANManualEntryFormatErrorScreen = "Shown_PANManualEntryFormatErrorScreen"
    const val Shown_PANManualEntryErrorScreen = "Shown_PANManualEntryErrorScreen"

    //OCR Camera flow
    const val Shown_OCROptionScreen = "Shown_OCROptionScreen"
    const val Clicked_Button_OCROptionScreen = "Clicked_Button_OCROptionScreen"
    const val Shown_OCRPANErrorScreen = "Shown_OCRPANErrorScreen"
    const val Shown_OCRInvalidPANScreen = "Shown_OCRInvalidPANScreen"
    const val Shown_OCRPhotoUploadErrorScreen = "Shown_OCRPhotoUploadErrorScreen"

    //Aadhaar
    const val Shown_AadhaarDetailsScreen = "Shown_AadhaarDetailsScreen"
    const val Clicked_Button_AadhaarDetailsScreen = "Clicked_Button_AadhaarDetailsScreen"
    const val Shown_EnterAadhaarManuallyScreen = "Shown_EnterAadhaarManuallyScreen"
    const val Clicked_Button_EnterAadhaarManuallyScreen =
        "Clicked_Button_EnterAadhaarManuallyScreen"
    const val Shown_EnterAadhaarDetailsScreen = "Shown_EnterAadhaarDetailsScreen"
    const val Clicked_Button_EnterAadhaarDetailsScreen = "Clicked_Button_EnterAadhaarDetailsScreen"
    const val Shown_NoMobileLinkedToAadhaarScreen = "Shown_NoMobileLinkedToAadhaarScreen"
    const val Clicked_Button_NoMobileLinkedToAadhaarScreen =
        "Clicked_Button_NoMobileLinkedToAadhaarScreen"
    const val Shown_AadhaarEnterCaptchaBottomSheet = "Shown_AadhaarEnterCaptchaBottomSheet"
    const val Clicked_AadhaarEnterCaptchaBottomSheet = "Clicked_AadhaarEnterCaptchaBottomSheet"
    const val Shown_AadhaarSendingOTPScreen = "Shown_AadhaarSendingOTPScreen"
    const val Shown_AadhaarEnterOTPBottomSheet = "Shown_AadhaarEnterOTPBottomSheet"
    const val Shown_AadhaarOTPEdgeCases = "Shown_AadhaarOTPEdgeCases"
    const val Clicked_Button_AadhaarRetakePhotoErrorScreen =
        "clicked_button_aadhaarretakephotoerrorscreen"
    const val Clicked_Button_AadhaarConfirmationScreen = "Clicked_Button_AadhaarConfirmationScreen"
    const val Clicked_Button_AadhaarEnterOTPScreen = "Clicked_Button_AadhaarEnterOTPScreen"
    const val Shown_EnterAadhaarDetailsScreenEdgeCases = "Shown_EnterAadhaarDetailsScreenEdgeCases"
    const val Shown_AadhaarOCRCaptchaEdgeCase = "Shown_AadhaarOCRCaptchaEdgeCase"
    const val Shown_PhotoUploadScreen = "Shown_PhotoUploadScreen"
    const val Clicked_Retry_PhotoUploadFailureScreen = "Clicked_Retry_PhotoUploadFailureScreen"
    const val Shown_AadhaarVerifyingDetailsScreen = "Shown_AadhaarVerifyingDetailsScreen"
    const val Shown_AadhaarServerDownScreen = "Shown_AadhaarServerDownScreen"
    const val Shown_AadhaarVerificationSuccessfulScreen =
        "Shown_AadhaarVerificationSuccessfulScreen"
    const val Shown_AadhaarPANMismatchScreen = "Shown_AadhaarPANMismatchScreen"
    const val Clicked_Button_AadhaarPANMismatchScreen = "Clicked_Button_AadhaarPANMismatchScreen"
    const val Shown_AadhaarVerificationPendingScreen = "Shown_AadhaarVerificationPendingScreen"

    //Selfie
    const val Shown_SelfiePrerequisitesScreen = "Shown_SelfiePrerequisitesScreen"
    const val Shown_SelfieCaptureScreen = "Shown_SelfieCaptureScreen"
    const val Shown_SelfieEdgeCaseScreens = "Shown_SelfieEdgeCaseScreens"
    const val Shown_SelfieVerificationSuccessfulScreen = "Shown_SelfieVerificationSuccessfulScreen"
    const val Clicked_Button_TakingSometimeScreen = "Clicked_Button_TakingSometimeScreen"
    const val Clicked_ContactSupportOption = "Clicked_ContactSupportOption"
    const val Shown_KYCVerifiedInProfileScreen = "Shown_KYCVerifiedInProfileScreen"
    const val Shown_SelfieUploadFailedScreens = "Shown_SelfieUploadFailedScreens"

    //FAQ
    const val Shown_KYCFAQSection = "Shown_KYCFAQSection"
    const val Clicked_Button_KYCFAQSection = "Clicked_Button_KYCFAQSection"

    const val Shown_LendingKycVerificationSuccessfulScreen =
        "Shown_LendingKycVerificationSuccessfulScreen"

    const val PARAM_KYC_Landing_Screen = "KYC Landing Screen"

    const val PAN_OCR_FLOW = "PAN OCR Flow"
    const val AADHAAR_OCR_FLOW = "Aadhaar OCR Flow"

    //Lending V2

    const val Lending_PanCardFetchOtpSent = "Lending_PanCardFetchOtpSent"
    const val Lending_PanCardFetchOtpSubmittedSuccessfully =
        "Lending_PanCardFetchOtpSubmittedSuccessfully"
    const val Lending_AadharFetchOtpSubmittedSuccessfully =
        "Lending_AadharFetchOtpSubmittedSuccessfully"
    const val Lending_PanCardFetched = "Lending_PanCardFetched"
    const val Lending_FetchedPanCardConfirmed = "Lending_FetchedPanCardConfirmed"
    const val Lending_FetchedPanCardDenied = "Lending_FetchedPanCardDenied"
    const val Lending_PanManualEntryScreenLauched = "Lending_PanManualEntryScreenLauched"
    const val Lending_PanNumberManuallyEntered = "Lending_PanNumberManuallyEntered"
    const val Lending_PanNumberProceedClicked = "Lending_PanNumberProceedClicked"
    const val Lending_AadharManualEntryScreenLaunched = "Lending_AadharManualEntryScreenLaunched"
    const val Lending_AadharManualEntryScreenError = "Lending_AadharManualEntryScreenError"
    const val Lending_AadharManualVerificationSuccessful =
        "Lending_AadharManualVerificationSuccessful"
    const val Lending_AadharDetailsVerified = "Lending_AadharDetailsVerified"
    const val Lending_SelfieCheckSuccessfulScreenShown = "Lending_SelfieCheckSuccessfulScreenShown"
    const val Lending_AadharServerDownScreenShown = "Lending_AadharServerDownScreenShown"
    const val Lending_NoMobileLinkedToAadharScreenShown =
        "Lending_NoMobileLinkedToAadharScreenShown"
    const val Lending_SelfieScreenLaunched = "Lending_SelfieScreenLaunched"
    const val Lending_SelfieScreenClicked = "Lending_SelfieScreenClicked"
    const val Lending_SelfieScreenError = "Lending_SelfieScreenError"
    const val Lending_BackButtonClicked = "Lending_BackButtonClicked"
    const val Lending_CrossButtonClicked = "Lending_CrossButtonClicked"
    const val Lending_PANCreditReportNotFound = "Lending_PANCreditReportNotFound"
    const val Lending_Checkbox_Clicked = "lending_checkbox_clicked"

    const val screen_name = "screen_name"
    const val isFromLendingFlow = "isFromLendingFlow"
    const val PAN_OTP_SCREEN = "pan_otp_screen"
    const val manualEntry = "manual_entry"
    const val LendingConfirmKycScreen = "LendingConfirmKycScreen"
    const val Lending_MandateSetupScreen = "Lending_MandateSetupScreen"
    const val Lending_AADHAR_NUMBER_ENTRY = "Lending_aadharnumberentry"
    const val AADHAR_CAPTCHA_BOTTOM_SHEET = "aadhar_captcha_bottom_sheet"
    const val PAN_MANUAL_ENTRY_SCREEN = "pan_manual_entry_screen"
    const val PAN_CARD_FETCHED_SCREEN = "pan_card_fetched_screen"
    const val Lending_ENTRY_FIELD_SELECTED = "Lending_ENTRY_FIELD_SELECTED"
    const val AADHAR_MANUAL_OTP_SCREEN = "aadhar_manual_otp_screen"
    const val AADHAR_MANUAL_ENTRY_SCREEN = "aadhar_manual_entry_screen"
    const val AADHAR_OCR_FIRST_SCREEN = "aadhar_OCR_first_screen"
    const val AADHAR_ERROR_SCREEN = "aadhar_error_screen"
    const val AADHAR_MANUAL_ENTRY = "aadhar_manual_entry"
    const val SELFIE_LAUNCH_SCREEN = "selfie_launch_screen"
    const val Lending_PANOtpFlow = "lending_panotpflow"
    const val Lending_AadharOtpFlow = "lending_aadharotpflow"
    const val OTP_BOTTOMSHEET_SHOWN = "otp_bottomsheet_shown"
    const val OTP_FETCHED = "otp_fetched"
    const val OTP_TYPED = "otp_typed"
    const val OTP_VERIFY_CLICKED = "otp_verify_clicked"
    const val OTP_SENT = "otp_sent"
    const val OTP_RESENT = "otp_resent"
    const val OTP_VERIFICATION_SUCCESSFUL = "otp_verification_successful"
    const val INVALID_OTP_SHOWN = "invalid_otp_shown"
    const val CONSENT_BOX_CLICKED = "consent_box_clicked"
    const val JAR_RECORDS = "jar_records"
    const val MANUAL_ENTRY = "manual_entry"
    const val Aadhar_Consent = "Aadhar_Consent"
    const val KYC_Consent = "KYC_Consent"
    const val MandateSetupcheck = "MandateSetupcheck"
    const val BackButtonClicked = "BackButtonClicked"
    const val PANErrorScreen = "PANErrorScreen"

    //Lending KYC Choose Option
    const val Lending_KYCScreenLaunched = "Lending_KYCScreenLaunched"
    const val Lending_KYCDigiLockerErrorScreen = "Lending_AadharDigilockerErrorScreen"
    const val DigiLocker_Webview = "digilocker"
    const val DigiLocker_Status = "digilocker_status"
    const val Okyc_Status = "okyc_status"
    const val Server_Down = "server_down"
    const val Both_Down = "both_down"
    const val IsDigilockerPreferred = "isDigilockerPreferred"
    const val Lending_DigiLocker_Screen_Exit = "Lending_Digilocker_Screen_Exit"

    const val Enabled = "enabled"
    const val Shown = "shown"
    const val Shown_KYC_Screen = "shown_kyc_screen"
    const val Consent_Deselected = "consent_deselected"
    const val Consent_Selected = "consent_selected"
    const val DigiLocker_Selected = " digilocker_selected"
    const val Okyc_Selected = "okyc_selected"
    const val Clicked_Try_Again = "clicked_try_again"
    const val Clicked_Contact_Support = "clicked_contact_support"
    const val Lending_DigiLocker_Screen_Next_Page_Load = "Lending_Digilocker_Screen_NextPageLoad"
    const val Link = "link"
    const val chooseKycScreen = "chooseKycScreen"
    const val Shown_DigiLocker_Aadhar_Screen = "shown_digilocker_aadhaar_screen"
    const val Shown_DigiLocker_Otp_Screen = "shown_digilocker_otp_screen"
    const val Shown_DigiLocker_Pin_Screen = "shown_digilocker_pin_screen"
    const val Shown_DigiLocker_Consent_Screen = "shown_digilocker_consent_screen"
    const val Shown_DigiLocker_Authenticate_Screen = "shown_digilocker_Authenticate_screen"


}