package com.jar.gold_redemption.impl.data

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.core_base.util.BaseConstants
import com.jar.gold_redemption.api.GoldRedemptionApi
import dagger.Lazy
import javax.inject.Inject

internal class GoldRedemptionApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
) : BaseNavigation, GoldRedemptionApi {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openIntroScreen(fromScreen: String?) {
        val currentTime by lazy { System.currentTimeMillis() }
        navController.navigate(Uri.parse("android-app://com.jar.app/goldRedemption/$fromScreen/$currentTime"))
    }

    override fun openBrandCatalogueScreen() {
        navController.navigate(Uri.parse("android-app://com.jar.app/brandCatalougeFragment/DEEPLINK"))
    }

    override fun openMyOrdersScreen(tabType: String?) {
        if (tabType.isNullOrBlank()) {
            navController.navigate(Uri.parse("android-app://com.jar.app/myOrdersFragment/ALL"))
        } else {
            navController.navigate(Uri.parse("android-app://com.jar.app/myOrdersFragment/${tabType}"))
        }
    }

    override fun openVoucherStatusScreen(voucherId: String?, orderType: String?) {
        if (orderType.isNullOrBlank()) {
            navController.navigate(Uri.parse("android-app://com.jar.app/voucherStatusFragment/DEEPLINK/${voucherId}"))
        } else {
            navController.navigate(Uri.parse("android-app://com.jar.app/voucherStatusFragment/DEEPLINK/${voucherId}/${orderType}"))
        }
    }

    override fun openVoucherPurchaseScreen(voucherId: String) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/${BaseConstants.ExternalDeepLinks.GOLD_REDEMPTION_VOUCHER_PURCHASE}/$voucherId"),
            getNavOptions(shouldAnimate = true)
        )
    }
}