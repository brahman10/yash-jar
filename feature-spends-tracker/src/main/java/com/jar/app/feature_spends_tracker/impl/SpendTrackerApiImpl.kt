package com.jar.app.feature_spends_tracker.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_spends_tracker.api.SpendsTrackerApi
import dagger.Lazy
import javax.inject.Inject

class SpendTrackerApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : SpendsTrackerApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openSpendTrackerFlow() {

        navController.navigate(
            Uri.parse("android-app://com.jar.app/spendsEducationScreen"),
            getNavOptions(shouldAnimate = true)
        )
    }
}