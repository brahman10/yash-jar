package com.myjar.app.feature_graph_manual_buy.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.core_base.util.BaseConstants.InternalDeepLinks.MANUAL_BUY_GRAPH
import com.myjar.app.feature_graph_manual_buy.api.GraphManualBuyApi
import dagger.Lazy
import javax.inject.Inject

internal class GraphManualBuyImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
): GraphManualBuyApi, BaseNavigation {
    val navController by lazy {
        navControllerRef.get()
    }
    override fun openGraphManualBuy() {
        navController.navigate(
            Uri.parse(MANUAL_BUY_GRAPH),
            getNavOptions(shouldAnimate = true)
        )
    }
}