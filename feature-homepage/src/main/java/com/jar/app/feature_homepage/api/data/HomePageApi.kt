package com.jar.app.feature_homepage.api.data

import androidx.fragment.app.Fragment

interface HomePageApi {

    fun openHomeFragment(): Fragment

    fun openHelpVideosListingScreen()

    fun openFirstCoinTransitionScreen()

    fun openFirstCoinProgressScreen()

    fun openFirstCoinDeliveryScreen(orderId: String)
}