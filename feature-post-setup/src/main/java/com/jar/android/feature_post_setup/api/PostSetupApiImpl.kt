package com.jar.android.feature_post_setup.api

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import dagger.Lazy
import javax.inject.Inject

internal class PostSetupApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : PostSetupApi, BaseNavigation {

    private val navigation by lazy {
        navControllerRef.get()!!
    }

    override fun openPostSetupDetails() {
        navigation.navigate(
            Uri.parse("android-app://com.jar.app/postSetupDetails"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun updateDSAutopayBank() {
        navigation.navigate(
            Uri.parse("android-app://com.jar.app/failedRenewalBottomSheet/${null}"),
            getNavOptions(shouldAnimate = true)
        )
    }
}