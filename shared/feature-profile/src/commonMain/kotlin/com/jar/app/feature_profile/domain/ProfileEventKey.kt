package com.jar.app.feature_profile.domain

object ProfileEventKey {

    object Props {
        const val TimeSpent = "timeSpent"
        const val ProfilePicture = "profilePicture"
        const val Yes = "yes"
        const val No = "no"
        const val Verified = "Verified"
        const val CompleteNow = "CompleteNow"
        const val FromScreen = "fromScreen"
        const val Name = "name"
        const val Email = "email"
        const val PhoneNumber = "phoneNumber"
        const val Enabled = "enabled"
        const val ErrorMsg = "errorMsg"
        const val SmsDetected = "smsDetected"
        const val Age = "age"
        const val Gender = "gender"
        const val NoOfSavedAddresses = "noOfSavedAddresses"
        const val Status = "status"
    }

    object Events {
        /********  Profile Fragment ********/
        const val Shown_ProfileScreen_Account = "Shown_ProfileScreen_Account"
        const val Exit_ProfileTab_Account = "Exit_ProfileTab_Account"
        const val Clicked_ProfilePicture_ProfileScreen = "Clicked_ProfilePicture_ProfileScreen"
        const val Clicked_Name_ProfileScreen = "Clicked_Name_ProfileScreen"
        const val Clicked_EditPhoneNumber_ProfileScreen = "Clicked_EditPhoneNumber_ProfileScreen"
        const val Clicked_Gender_ProfileScreen = "Clicked_Gender_ProfileScreen"
        const val Clicked_SavedAddresses_ProfileTab = "Clicked_SavedAddresses_ProfileTab"
        const val Clicked_KYC_ProfileScreen = "Clicked_KYC_ProfileScreen"
        /********  End Region ********/

        /********  Edit Profile Pic ********/
        const val Shown_ProfilePicture_ProfilePicturePopUp = "Shown_ProfilePicture_ProfilePicturePopUp"
        const val Clicked_Save_ProfilePicturePopUp = "Clicked_Save_ProfilePicturePopUp"
        const val Clicked_Cancel_ProfilePicturePopUp = "Clicked_Cancel_ProfilePicturePopUp"
        const val Clicked_ChooseGallery_UploadPicturePopUp = "Clicked_ChooseGallery_UploadPicturePopUp"
        const val Clicked_TakeSelfie_UploadPicturePopUp = "Clicked_TakeSelfie_UploadPicturePopUp"
        const val Shown_Success_ProfilePicturePopUp = "Shown_Success_ProfilePicturePopUp"
        /********  End Region ********/

        /********  Edit Profile Name ********/
        const val Shown_ChangeName_NamePopUp = "Shown_ChangeName_NamePopUp"
        const val Clicked_EnterName_NamePopUp = "Clicked_EnterName_NamePopUp"
        const val Clicked_Save_NamePopUp = "Clicked_Save_NamePopUp"
        const val Clicked_Cancel_NamePopUp = "Clicked_Cancel_NamePopUp"
        const val Shown_Success_NamePopUp = "Shown_Success_NamePopUp"
        /********  End Region ********/

        /********  Edit Profile Email ********/
        const val Shown_ChangeEmail_EmailPopUp = "Shown_ChangeEmail_EmailPopUp"
        const val Clicked_EnterEmail_EmailPopUp = "Clicked_EnterEmail_EmailPopUp"
        const val Clicked_Save_EmailPopUp = "Clicked_Save_EmailPopUp"
        const val Clicked_Cancel_EmailPopUp = "Clicked_Cancel_EmailPopUp"
        const val Shown_Success_EmailPopUp = "Shown_Success_EmailPopUp"
        /********  End Region ********/

        /********  Edit Profile Number ********/
        const val Shown_EditNumber_PhoneNumberPopUp = "Shown_EditNumber_PhoneNumberPopUp"
        const val Clicked_EnterNumber_PhoneNumberPopUp = "Clicked_EnterNumber_PhoneNumberPopUp"
        const val Clicked_GetOTP_PhoneNumberPopUp = "Clicked_GetOTP_PhoneNumberPopUp"
        const val Clicked_Cancel_PhoneNumberPopUp = "Clicked_Cancel_PhoneNumberPopUp"
        const val Shown_Error_PhoneNumberPopUp = "Shown_Error_PhoneNumberPopUp"
        const val Shown_OTPScreen_PhoneNumberPopUp = "Shown_OTPScreen_PhoneNumberPopUp"
        const val Clicked_EnterOTP_PhoneNumberPopUp = "Clicked_EnterOTP_PhoneNumberPopUp"
        const val Clicked_Verify_PhoneNumberPopUp = "Clicked_Verify_PhoneNumberPopUp"
        const val Shown_ErrorMessage_PhoneNumberPopUp = "Shown_ErrorMessage_PhoneNumberPopUp"
        const val Shown_Success_PhoneNumberPopUp = "Shown_Success_PhoneNumberPopUp"
        /********  End Region ********/

        /********  Edit Profile Age ********/
        const val Clicked_Age_ProfileScreen = "Clicked_Age_ProfileScreen"
        const val Shown_ChangeAge_AgePopUp = "Shown_ChangeAge_AgePopUp"
        const val Clicked_Save_AgePopUp = "Clicked_Save_AgePopUp"
        const val Clicked_Cancel_AgePopUp = "Clicked_Cancel_AgePopUp"
        const val Shown_Success_AgePopUp = "Shown_Success_AgePopUp"
        /********  End Region ********/

        /********  Edit Profile Gender ********/
        const val Shown_SelectGender_GenderPopUp = "Shown_SelectGender_GenderPopUp"
        const val Clicked_SaveGender_GenderPopUp = "Clicked_SaveGender_GenderPopUp"
        const val Clicked_Cancel_GenderPopUp = "Clicked_Cancel_GenderPopUp"
        const val Shown_Success_GenderPopUp = "Shown_Success_GenderPopUp"
        /********  End Region ********/
    }

}