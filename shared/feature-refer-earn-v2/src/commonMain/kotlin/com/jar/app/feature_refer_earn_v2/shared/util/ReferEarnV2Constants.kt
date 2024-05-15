package com.jar.app.feature_refer_earn_v2.shared.util

object ReferEarnV2Constants {

    object Endpoints {
        const val FETCH_REFERRAL_INTRO = "v2/api/referral/intro/staticInfo"
        const val FETCH_REFERRALS = "v2/api/referral"
        const val POST_REFERRAL_ATTRIBUTION = "v2/api/referral/referralAttribution"
        const val FETCH_REFERRAL_SHARE_MESSAGES = "v2/api/referral/getShareMessages"
    }

    object Analytics {
        const val Refer_ScreenShown = "Refer_ScreenShown"
        const val Refer_ScreenClicked = "Refer_ScreenClicked"

        const val Gold = "Gold"
        const val View_Referrals = "View_Referrals"
        const val Invite_via_Whatsapp = "Invite_via_Whatsapp"
        const val Invite_Contacts = "Invite_Contacts"
        const val Share_via = "Share_via"
        const val Back_clicked = "Back_clicked"
        const val Contact_Support = "Contact_Support"
        const val Cross_clicked = "Cross_clicked"

        const val screenshown = "screenshown"
        const val screen_type = "screen_type"
        const val refer_intro = "refer_intro"
        const val buttonclicked = "buttonclicked"

        const val Reward_Screen = "Reward_Screen"
        const val Non_Reward_Screen = "Non_Reward_Screen"
    }
}