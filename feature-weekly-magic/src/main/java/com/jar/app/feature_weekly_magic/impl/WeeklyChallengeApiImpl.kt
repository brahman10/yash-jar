package com.jar.app.feature_weekly_magic.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_weekly_magic.api.WeeklyChallengeApi
import dagger.Lazy
import javax.inject.Inject

internal class WeeklyChallengeApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : WeeklyChallengeApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun startWeeklyChallengeFlow(fromScreen: String) {
        val currentTime = System.currentTimeMillis()
        navController.navigate(
            Uri.parse("android-app://com.jar.app/weeklyChallenge/weeklyChallengeHomeFragment/${fromScreen}/${currentTime}"),
            getNavOptions(shouldAnimate = false)
        )
    }

}