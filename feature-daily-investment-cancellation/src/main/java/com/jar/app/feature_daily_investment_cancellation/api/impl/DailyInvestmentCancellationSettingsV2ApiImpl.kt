package com.jar.app.feature_daily_investment_cancellation.api.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import dagger.Lazy
import javax.inject.Inject

internal class DailyInvestmentCancellationSettingsV2ApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : com.jar.app.feature_daily_investment_cancellation.api.DailyInvestmentCancellationSettingsV2Api, BaseNavigation {

    private val navigation by lazy {
        navControllerRef.get()!!
    }

    override fun openDailyInvestmentCancellationSettingsV2Flow() {
        navigation.navigate(Uri.parse("android-app://com.jar.app/dailySavingsCancellation"))
    }
}