package com.jar.app.feature_vasooli.impl.data

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_vasooli.api.VasooliApi
import dagger.Lazy
import javax.inject.Inject

internal class VasooliApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val prefs: PrefsApi
) : VasooliApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openVasooli() {
        if (prefs.shouldShowVasooliIntro()) {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/vasooliIntroFragment"),
                getNavOptions(shouldAnimate = true)
            )
        } else {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/vasooliHomeFragment"),
                getNavOptions(shouldAnimate = true)
            )
        }
    }
}