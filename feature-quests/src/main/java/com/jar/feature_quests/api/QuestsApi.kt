package com.jar.feature_quests.api

/**
 * Quests feature API
 * **/
interface QuestsApi {

    /**
     * Method to start the intro screen
     * **/
    fun openIntroScreen(fromScreen: String)

    fun openCouponDetails(fromScreen: String, brandCouponId: String)

    fun openRewardsScreen(fromScreen: String)

    fun openDashboard(fromScreen: String)
}