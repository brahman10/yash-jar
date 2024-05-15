package com.jar.gold_price_alerts.impl.data

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.core_base.util.BaseConstants
import com.jar.gold_price_alerts.api.GoldPriceAlertsApi
import dagger.Lazy
import javax.inject.Inject

internal class GoldPriceAlertsApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
) : BaseNavigation, GoldPriceAlertsApi {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openIntroScreen(fromScreen: String?) {
        navController.navigate(Uri.parse("${BaseConstants.InternalDeepLinks.GOLD_PRICE_DETAILS}/${fromScreen}"))
    }


}