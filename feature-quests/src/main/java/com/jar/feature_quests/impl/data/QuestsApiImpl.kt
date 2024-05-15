package com.jar.feature_quests.impl.data

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.feature_quests.api.QuestsApi
import dagger.Lazy
import javax.inject.Inject

internal class QuestsApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
) : BaseNavigation, QuestsApi {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openIntroScreen(fromScreen: String) {
        navController.navigate(Uri.parse("android-app://com.jar.app/splashScreen/$fromScreen"))
    }

    override fun openCouponDetails(fromScreen: String, brandCouponId: String) {
        navController.navigate(Uri.parse("android-app://com.jar.app/questCouponDetails/$fromScreen/$brandCouponId"))
    }

    override fun openRewardsScreen(fromScreen: String) {
        navController.navigate(Uri.parse("android-app://com.jar.app/questRewards/$fromScreen"))
    }

    override fun openDashboard(fromScreen: String) {
        navController.navigate(Uri.parse("android-app://com.jar.app/dashboardScreen/$fromScreen"))
    }
}