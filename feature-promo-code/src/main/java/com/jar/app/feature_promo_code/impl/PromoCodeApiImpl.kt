package com.jar.app.feature_promo_code.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_promo_code.api.PromoCodeApi
import dagger.Lazy
import javax.inject.Inject

internal class PromoCodeApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : PromoCodeApi, BaseNavigation {
    private val navController by lazy {
        navControllerRef.get()
    }
    override fun openPromoCodeDialog() {
        navController.navigate(Uri.parse("android-app://com.jar.app/submitPromoCode"))

    }
}