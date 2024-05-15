package com.jar.app.feature_weekly_magic_common.shared.utils

object WeeklyMagicConstants {

    const val WINNINGS_ANIMATION_FINISHED = "WINNINGS_ANIMATION_FINISHED"
    const val ON_BOARDING_ANIMATION_FINISHED = "ON_BOARDING_ANIMATION_FINISHED"
    const val ON_STORY_MODE_FINISHED = "ON_STORY_MODE_FINISHED"
    const val On_MYSTERY_CARD_WON_DIALOG_FINISHED = "On_MYSTERY_CARD_WON_DIALOG_FINISHED"

    object TransactionType {
        const val TYPE_MANUAL = "MANUAL"
    }

    internal object Endpoints {
        const val FETCH_WEEKLY_CHALLENGE_DETAIL = "v1/api/weeklyChallenges/details"
        const val FETCH_WEEKLY_CHALLENGE_META_DATA = "v1/api/weeklyChallenges"
        const val MARK_CURRENT_WEEKLY_CHALLENGE_VIEWED =
            "v1/api/weeklyChallenges/isChallengeResultViewed"
        const val MARK_WEEKLY_CHALLENGE_STORY_VIEWED = "v1/api/weeklyChallenges/isStoryViewed"
        const val MARK_PREVIOUS_WEEKLY_CHALLENGE_VIEWED = "v1/api/weeklyChallenges/isViewed"
        const val MARK_WEEKLY_CHALLENGE_ONBOARDED = "v1/api/weeklyChallenges/onboard"
    }

    object AnalyticsKeys {
        const val Completed_WeeklyChallengeOnBoarding = "Completed_WeeklyChallengeOnBoarding"

        const val WeeklyMagic_BSClicked = "WeeklyMagic_BSClicked"
        const val WeeklyMagic_BSShown = "WeeklyMagic_BSShown"
        const val WeeklyMagic_Shown_Ts = "WeeklyMagic_Shown_Ts"
        const val Clicked_ChallengeRulesBottomSheet = "Clicked_ChallengeRulesBottomSheet"
        const val Clicked_Button_RewardBottomSheet = "Clicked_Button_RewardBottomSheet"
        const val Clicked_Button_WeeklyMagicScreen = "Clicked_Button_WeeklyMagicScreen"
        const val Clicked_WeeklyMagicBanner_BuyGoldPostOrderScreen =
            "Clicked_WeeklyMagicBanner_BuyGoldPostOrderScreen"

        const val Shown_ChallengeRulesBottomSheet = "Shown_ChallengeRulesBottomSheet"
        const val Shown_RewardBottomSheet = "Shown_RewardBottomSheet"
        const val Shown_OnboardingStory = "Shown_OnboardingStory"
        const val Shown_WeeklyMagicScreen = "Shown_WeeklyMagicScreen"
        const val Shown_WeeklyMagicCompletionScreen = "Shown_WeeklyMagicCompletionScreen"
        const val Shown_MysteryCardAnimationScreen = "Shown_MysteryCardAnimationScreen"
        const val Shown_MysteryCardsCollectionScreen = "Shown_MysteryCardsCollectionScreen"

        object Screens {
            const val Buy_Gold_Post_Order_Screen = "Buy_Gold_Post_Order_Screen"
            const val Home_Screen_Bottom_Sheet = "Home_Screen_Bottom_Sheet"
            const val Weekly_Magic_Screen = "Weekly_Magic_Screen"
            const val Via_PN_Deeplink = "Via_PN_Deeplink"
        }

        object Parameters {
            const val daysLeft = "daysLeft"
            const val optionChosen = "optionChosen"
            const val scenario = "scenario"
            const val cardsCollected = "cardsCollected"
            const val minimumOrderValue = "minimumOrderValue"
            const val screenNumber = "screenNumber"
            const val clickaction = "clickaction"
            const val weeklymagictips = "weeklymagictips"
            const val timespentbeforeclick = "timespentbeforeclick"
            const val resultStatus = "resultStatus"
            const val challengelostmessage = "challengelostmessage"
            const val fromScreen = "fromScreen"
            const val shownCards = "shownCards"
        }

        object Values {
            const val Weekly_Magic = "Weekly_Magic"
            const val Show_me = "Show me"
            const val I_ll_check_later = "I’ll check later"
            const val DateClicked = "Date Clicked"
            const val HatClicked = "Hat Clicked"
            const val MessageClicked = "Message Clicked"
        }
    }
}