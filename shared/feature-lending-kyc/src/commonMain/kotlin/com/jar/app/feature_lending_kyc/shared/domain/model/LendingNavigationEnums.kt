package com.jar.app.feature_lending_kyc.shared.domain.model

import dev.icerock.moko.resources.StringResource


@kotlinx.serialization.Serializable
enum class AadhaarErrorScreenPrimaryButtonAction {
    GO_HOME, GO_BACK, EDIT_AADHAAR
}

@kotlinx.serialization.Serializable
enum class AadhaarErrorScreenSecondaryButtonAction {
    CONTACT_SUPPORT, GO_BACK, EDIT_PAN, NONE
}

enum class PanErrorScreenPrimaryButtonAction(val stringRes: StringResource?) {
    GO_HOME(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_back_to_home),
    ENTER_PAN_MANUALLY(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_enter_pan_manually),
    ENTER_PAN_AGAIN(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_enter_pan_again),
    USE_PAN_SAVED_WITH_JAR(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_use_my_pan_saved_with_jar),
    RETAKE_PHOTO(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_image_picker_retake_photo),
    YES_THIS_IS_MY_PAN(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_yes_this_is_my_pan),
    YES_DETAILS_ARE_CORRECT(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_yes_details_are_correct),
    YES_USE_THIS_PAN(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_yes_use_this_pan),
    NONE(null)
}

enum class PanErrorScreenSecondaryButtonAction(val stringRes: StringResource?) {
    CONTACT_SUPPORT(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support),
    ENTER_PAN_MANUALLY(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_enter_pan_manually),
    ENTER_PAN_AGAIN(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_enter_pan_again),
    NO_THIS_IS_NOT_MY_PAN(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_this_is_not_my_pan),
    NO_ENTER_DETAILS_MANUALLY(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_enter_details_manually),
    NONE(null)
}