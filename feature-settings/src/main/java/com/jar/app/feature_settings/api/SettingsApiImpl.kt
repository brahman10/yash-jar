package com.jar.app.feature_settings.api

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_settings.impl.ui.settings.SettingsFragmentV2
import dagger.Lazy
import javax.inject.Inject

internal class SettingsApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : SettingsApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openSettingFragment(): Fragment {
        return SettingsFragmentV2.newInstance()
    }

    override fun openAddUpiFragment() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/addUpi"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openRoundOffFragment() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/roundOff"),
            getNavOptions(shouldAnimate = true)
        )
    }
}