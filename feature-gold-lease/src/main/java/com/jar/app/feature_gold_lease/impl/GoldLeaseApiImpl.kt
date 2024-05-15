package com.jar.app.feature_gold_lease.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.BaseAppDeeplink
import com.jar.app.feature_gold_lease.api.GoldLeaseApi
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2OrderSummaryArgs
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.Lazy
import javax.inject.Inject

internal class GoldLeaseApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val serializer: Serializer
) : GoldLeaseApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openGoldLeaseV2Flow(flowType: String, tabPosition: Int, isNewLeaseUser: Boolean) {
        val currentTime = System.currentTimeMillis()
        navController.navigate(
            Uri.parse("${BaseAppDeeplink.GoldLease.GOLD_LEASE_SPLASH_SCREEN}/$flowType/${tabPosition}/${isNewLeaseUser}/${currentTime}"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openGoldLeasePlans(flowType: String, isNewLeaseUser: Boolean) {
        navController.navigate(
            Uri.parse("${BaseAppDeeplink.GoldLease.GOLD_LEASE_PLANS_SCREEN}/$flowType/$isNewLeaseUser"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openGoldLeaseUserLeaseDetails(flowType: String, leaseId: String) {
        navController.navigate(
            Uri.parse("${BaseAppDeeplink.GoldLease.GOLD_LEASE_USER_LEASE_DETAILS_SCREEN}/$flowType/$leaseId"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openGoldLeaseSummaryRetryFlow(flowType: String, leaseId: String, isNewLeaseUser: Boolean) {
        val goldLeaseV2OrderSummaryArgs = GoldLeaseV2OrderSummaryArgs(
            isNewLeaseUser = isNewLeaseUser,
            flowType = flowType,
            leaseId = leaseId,
            goldLeaseV2OrderSummaryScreenData = null
        )
        val encoded = encodeUrl(
            serializer.encodeToString(
                goldLeaseV2OrderSummaryArgs
            )
        )
        navController.navigate(
            Uri.parse("${BaseAppDeeplink.GoldLease.GOLD_LEASE_ORDER_SUMMARY_SCREEN}/$encoded"),
            getNavOptions(shouldAnimate = true)
        )
    }
}