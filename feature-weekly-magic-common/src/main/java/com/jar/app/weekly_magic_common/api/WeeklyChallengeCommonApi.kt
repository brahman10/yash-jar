package com.jar.app.weekly_magic_common.api

import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData

/**
 * Weekly Challenge Common Api (to be used by other modules)
 * **/
interface WeeklyChallengeCommonApi {

    /**
     * Method to show weekly Challenge OnBoarding dialog,i.e to inform user
     * about the feature.
     * **/
    fun showWeeklyChallengeOnBoardingDialog(triggerReturnResult:Boolean, fromScreen:String)

    /**
     * Method to show previous week Challenge Story
     * **/
    fun showPreviousWeekChallengeStory(challengeId:String, fromScreen:String)

    /**
     * Method to show weekly Challenge dialog,i.e to inform user that,
     * there is a new mystery card.
     * **/
    fun showMysteryCardWonDialog(data:WeeklyChallengeMetaData)

    /**
     * Method to show weekly Challenge Win Screen,i.e to inform user that,
     * there is a new mystery card or the user has won the challenge
     * **/
    fun showMysteryCardOrChallengeWonScreen(
        challengeId: String,
        showPurchaseTextAnimation: Boolean,
        fromScreen:String,
        launchWeeklyHome:Boolean=false
    )

    /**
     * Method to show weekly Challenge Win Screen and then open Weekly Home Screen.
     * **/
    fun startWinAnimationAndWeeklyMagicHomeFlow(
        data:WeeklyChallengeMetaData,
        fromScreen: String,
        showPurchaseTextAnimation: Boolean
    )
    /**
     * Method to mark weekly Challenge or mystery card Won
     * **/
    fun markCardOrChallengeAsWon(data: WeeklyChallengeMetaData)

    /**
     * call from Activity Destroy Method.
     */
    fun tearDown()
}