package com.jar.app.feature_user_api.util

object UserApiConstants {

    internal object Endpoints {
        const val FETCH_KYC_STATUS = "v1/api/kyc/status"
        const val FETCH_USER_SETTINGS = "v2/api/user/settings"
        const val UPDATE_USER_SETTINGS = "v1/api/user/settings"
        const val UPDATE_USER_PHONE_NUMBER = "v2/api/user/update/phone"
        const val VERIFY_USER_PHONE_NUMBER = "v2/api/user/update/phone/verify"
        const val UPDATE_USER = "v2/api/user/update"
        const val UPDATE_USER_PROFILE_PICTURE = "v2/api/user/update/profilePic"
        const val FETCH_DETECTED_SPEND_INFO = "v1/api/dashboard/paymentInfo"
        const val VALIDATE_PIN_CODE = "v1/api/delivery/validatePin"
        const val FETCH_USER_GOLD_BALANCE = "v1/api/user/goldBalance"
        const val FETCH_USER_ADDRESSES = "v1/api/user/settings/address"
        const val DELETE_USER_ADDRESS = "v1/api/user/settings/address"
        const val ADD_USER_ADDRESS = "v1/api/user/settings/address"
        const val EDIT_USER_ADDRESS = "v1/api/user/settings/address"
        const val FETCH_ADDRESS_BY_ID = "v1/api/user/settings/address/id"
        const val FETCH_USER_SAVED_VPA = "v2/api/user/vpa/all"
        const val DELETE_VPA = "v2/api/user/vpa"
        const val ADD_NEW_VPA = "v2/api/user/vpa/add/vpa"
        const val IS_MANDATE_RESET_REQUIRED = "v1/api/autopay/mandateResetRequired"
        const val FETCH_USER_UPDATES = "v2/api/user/userUpdates"
        const val FETCH_USER_WINNINGS = "v1/api/wallet/winnings/info"
        const val FETCH_GOLD_SIP_DETAILS = "v1/api/user/settings/goldSipDetails"
        const val UPDATE_PAUSE_DURATION = "v2/api/user/savings"
    }

    object AnalyticsKeys {
        const val Shown_SavedAddressesScreen = "Shown_SavedAddressesScreen"
        const val ClickedSubmitDetails_GoldDeliveryScreen =
            "ClickedSubmitDetails_GoldDeliveryScreen"
        const val GoldInAccount = "GoldInAccount"
        const val Click_ShownSaveAddressPopUp_GoldDelivery =
            "Click_ShownSaveAddressPopUp_GoldDelivery"
        const val ShowNewAddressScreen_GoldDelivery = "ShowNewAddressScreen_GoldDelivery"
        const val ShownFillDetails_GoldDeliveryScreen = "ShownFillDetails_GoldDeliveryScreen"
        const val Click_AddNewAddressScreen = "Click_AddNewAddressScreenv"
    }
}