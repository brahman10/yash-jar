package com.jar.refer_earn_v2.impl.data

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.refer_earn_v2.api.ReferEarnV2Api
import dagger.Lazy
import javax.inject.Inject

internal class ReferEarnV2ApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
) : BaseNavigation, ReferEarnV2Api {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openIntroScreen(fromScreen: String?) {
        val currentTime = System.currentTimeMillis()
        navController.navigate(Uri.parse("android-app://com.jar.app/goldRedemption/$fromScreen/$currentTime"))
    }


}