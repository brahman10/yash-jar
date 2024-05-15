package com.jar.health_insurance.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.health_insurance.api.HealthInsuranceApi
import dagger.Lazy
import javax.inject.Inject

class HealthInsuranceApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : HealthInsuranceApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openHealthInsuranceLandingPage(fromScreen: String) {
        val currentTime = System.currentTimeMillis()
        val url = "android-app://com.jar.app/healthInsurance/LandingPage/$fromScreen/$currentTime"
        navController.navigate(
            Uri.parse(url),
        )
    }

    override fun openHealthInsuranceAddDetailsPage() {
        val url = "android-app://com.jar.app/healthInsurance/addDetails"
        navController.navigate(
            Uri.parse(url)
        )
    }

    override fun openHealthInsurancePostPurchasePage(insuranceId: String) {
        val url = "android-app://com.jar.app/healthInsurance/paymentStatusPage/$insuranceId"
        navController.navigate(
            Uri.parse(url)
        )
    }

    override fun openHealthInsuranceSelectPlanScreen(orderId: String) {
        val url =
            "android-app://com.jar.app/healthInsurance/selectHealthInsurancePlanScreen/${orderId}"
        navController.navigate(
            Uri.parse(url)
        )
    }

    override fun openHealthInsuranceManageScreen(insuranceId: String) {
        val url = "android-app://com.jar.app/healthInsurance/manageInsurance/$insuranceId"
        navController.navigate(
            Uri.parse(url)
        )

    }
}