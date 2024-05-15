package com.jar.health_insurance.api

interface HealthInsuranceApi {
    fun openHealthInsuranceLandingPage(fromScreen:String)

    fun openHealthInsuranceAddDetailsPage()

    fun openHealthInsurancePostPurchasePage(insuranceId:String)

    fun openHealthInsuranceSelectPlanScreen(orderId:String)

    fun openHealthInsuranceManageScreen(insuranceId: String)
}