package com.jar.app.feature_lending.shared.domain

object LendingEventKey {
    /**Props**/
    const val goldAmount = "goldAmount"
    const val entryPoint = "entryPoint"
    const val step = "step"
    const val employmentType = "employmentType"
    const val companyName = "companyName"
    const val monthlyIncome = "monthlyIncome"
    const val errorText = "errorText"
    const val salaried = "salaried"
    const val selfEmployed = "selfEmployed"
    const val search = "search"
    const val locate = "locate"
    const val existing = "existing"
    const val new = "new"
    const val chooseAddress = "chooseAddress"
    const val addNewAddressVia = "addNewAddressVia"
    const val selectedAddress = "selectedAddress"
    const val type = "type"
    const val permission = "permission"
    const val allow = "allow"
    const val denied = "denied"
    const val eligibilityAmount = "eligibilityAmount"
    const val selectedAmount = "selectedAmount"
    const val address = "address"
    const val emiTenure = "emiTenure"
    const val emiAmount = "emiAmount"
    const val accountHolderName = "accountHolderName"
    const val ifscCode = "ifscCode"
    const val accountType = "accountType"
    const val verificationStatus = "verificationStatus"
    const val success = "success"
    const val failed = "failed"
    const val failureReason = "failureReason"
    const val termsAndConditions = "termsAndConditions"
    const val yes = "yes"
    const val no = "no"
    const val otpStatus = "otpStatus"
    const val sent = "sent"
    const val resendOtp = "resendOtp"
    const val agreement = "agreement"
    const val signed = "signed"
    const val unSigned = "unSigned"
    const val verified = "verified"
    const val notSent = "notSent"
    const val mandate = "mandate"
    const val set = "set"
    const val notSet = "notSet"
    const val loanName = "loanName"
    const val loanPurpose = "loanPurpose"

    /**Events**/
    const val Shown_SellGold_GetReadyCash = "Shown_SellGold_GetReadyCash"
    const val SellGold_JarReadyCash_Apply = "SellGold_JarReadyCash_Apply"
    const val SellGold_WithdrawCash = "SellGold_WithdrawCash"

    const val OnClick_ReadyCash_Overview_Back = "OnClick_ReadyCash_Overview_Back"
    const val OnClick_ReadyCash_Overview_Next = "OnClick_ReadyCash_Overview_Next"

    const val OnClick_ReadyCash_StartKyc_Back = "OnClick_ReadyCash_StartKyc_Back"
    const val OnClick_ReadyCash_StartKyc = "OnClick_ReadyCash_StartKyc"

    const val OnClick_ReadyCash_Continue_Back = "OnClick_ReadyCash_Continue_Back"
    const val OnClick_ReadyCash_Continue = "OnClick_ReadyCash_Continue"

    const val Exit_ReadyCash_BottomSheet_IllDoItLater = "Exit_ReadyCash_BottomSheet_IllDoItLater"
    const val Exit_ReadyCash_BottomSheet_Cancel = "Exit_ReadyCash_BottomSheet_Cancel"
    const val Lending_CrossBSCancelClicked = "Lending_CrossBSCancelClicked"

    const val Shown_ReadyCash_PersonalDetails = "Shown_ReadyCash_PersonalDetails"
    const val OnClick_ReadyCash_PersonalDetails_Back = "OnClick_ReadyCash_PersonalDetails_Back"
    const val OnClick_ReadyCash_PersonalDetails_Continue =
        "OnClick_ReadyCash_PersonalDetails_Continue"
    const val Error_ReadyCash_PersonalDetails = "Error_ReadyCash_PersonalDetails"

    const val Shown_ReadyCash_PersonalDetails_Address = "Shown_ReadyCash_PersonalDetails_Address"
    const val OnClick_ReadyCash_PersonalDetails_Address_Back =
        "OnClick_ReadyCash_PersonalDetails_Address_Back"
    const val OnClick_ReadyCash_PersonalDetails_Address_Continue =
        "OnClick_ReadyCash_PersonalDetails_Address_Continue"
    const val OnClick_ReadyCash_PersonalDetails_AddAddress =
        "OnClick_ReadyCash_PersonalDetails_AddAddress"
    const val OnClick_ReadyCash_PersonalDetails_AddAddressOptions_Back =
        "OnClick_ReadyCash_PersonalDetails_AddAddressOptions_Back"
    const val OnClick_ReadyCash_PersonalDetails_AddAddressOptions_Via =
        "OnClick_ReadyCash_PersonalDetails_AddAddressOptions_Via"
    const val OnClick_ReadyCash_PersonalDetails_EditAddress =
        "OnClick_ReadyCash_PersonalDetails_EditAddress"

    const val OnClick_ReadyCash_PersonalDetails_AddAddress_Change =
        "OnClick_ReadyCash_PersonalDetails_AddAddress_Change"
    const val OnClick_ReadyCash_PersonalDetails_AddAddress_Save =
        "OnClick_ReadyCash_PersonalDetails_AddAddress_Save"
    const val Error_ReadyCash_PersonalDetails_AddAddress_Save =
        "Error_ReadyCash_PersonalDetails_AddAddress_Save"

    const val Fetching_ReadyCash_PersonalDetails_AddAddress =
        "Fetching_ReadyCash_PersonalDetails_AddAddress"

    const val OnClick_ReadyCash_PersonalDetails_Confirm =
        "OnClick_ReadyCash_PersonalDetails_Confirm"
    const val OnClick_ReadyCash_PersonalDetails_Review = "OnClick_ReadyCash_PersonalDetails_Review"

    const val Checking_ReadyCash_Eligibility = "Checking_ReadyCash_Eligibility"
    const val Eligibility_ReadyCash_Pending = "Eligibility_ReadyCash_Pending"
    const val Eligibility_ReadyCash_Rejected = "Eligibility_ReadyCash_Rejected"
    const val Eligibility_ReadyCash_Success = "Eligibility_ReadyCash_Success"

    const val OnClick_ReadyCash_Eligibility_ChooseAmount =
        "OnClick_ReadyCash_Eligibility_ChooseAmount"
    const val OnClick_ReadyCash_Eligibility_AmountSelected =
        "OnClick_ReadyCash_Eligibility_AmountSelected"
    const val OnClick_ReadyCash_Eligibility_Amount_Back =
        "OnClick_ReadyCash_Eligibility_Amount_Back"

    const val OnClick_ReadyCash_EMI_Option = "OnClick_ReadyCash_EMI_Option"
    const val OnClick_ReadyCash_EMI_Confirm = "OnClick_ReadyCash_EMI_Confirm"

    const val OnClick_ReadyCash_BankDetails = "OnClick_ReadyCash_BankDetails"
    const val OnClick_ReadyCash_BankDetails_Back = "OnClick_ReadyCash_BankDetails_Back"
    const val OnClick_ReadyCash_BankDetails_Continue = "OnClick_ReadyCash_BankDetails_Continue"
    const val OnClick_ReadyCash_BankDetails_Change = "OnClick_ReadyCash_BankDetails_Change"
    const val OnClick_ReadyCash_BankDetails_Verify = "OnClick_ReadyCash_BankDetails_Verify"

    const val OnClick_ReadyCash_Agreement_Proceed = "OnClick_ReadyCash_Agreement_Proceed"
    const val OnClick_ReadyCash_Agreement_Back = "OnClick_ReadyCash_Agreement_Back"

    const val OnClick_ReadyCash_AgreementSign = "OnClick_ReadyCash_AgreementSign"
    const val OnClick_ReadyCash_AgreementSign_Status = "OnClick_ReadyCash_AgreementSign_Status"

    const val OnClick_ReadyCash_AutomateEMI = "OnClick_ReadyCash_AutomateEMI"
    const val Shown_MandateFailed_Screen = "Shown_MandateFailed_Screen"
    const val Shown_MandatePending_Screen = "Shown_MandatePending_Screen"
    const val Lending_MandateApplicationPendingShown = "Lending_MandateApplicationPendingShown"

    const val OnClick_ReadyCash_NameOfLoan = "OnClick_ReadyCash_NameOfLoan"
    const val Lending_CROSS_BOTTOMSHEET_SHOWN = "Lending_CROSS_BOTTOMSHEET_SHOWN"
}