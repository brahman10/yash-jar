package com.jar.app.feature_homepage.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_homepage.api.data.HomePageApi
import com.jar.app.feature_homepage.impl.ui.homepage.HomeFragment
import dagger.Lazy
import javax.inject.Inject

internal class HomePageApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
) : HomePageApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openHomeFragment() = HomeFragment.newInstance()


    override fun openHelpVideosListingScreen() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/helpVideosListing"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openFirstCoinTransitionScreen() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/firstCoin/transition"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openFirstCoinProgressScreen() {
        val currentTime = System.currentTimeMillis()
        navController.navigate(
            Uri.parse("android-app://com.jar.app/firstCoin/progress/$currentTime"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openFirstCoinDeliveryScreen(orderId: String) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/firstCoin/delivery/$orderId"),
            getNavOptions(shouldAnimate = true)
        )
    }


}