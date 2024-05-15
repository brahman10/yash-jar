package com.jar.app.feature_health_insurance.shared.ui

import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.ManageScreenData

data class ManageInsuranceState(
    val manageScreenData: ManageScreenData? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val transactionHeader:String? = null
)

sealed class ManageScreenEvent {
    data class LoadManageScreenData(val insuranceId: String) : ManageScreenEvent()
    data class InitiateManualPayment(val insuranceId: String) : ManageScreenEvent()
    object ErrorMessageDisplayed : ManageScreenEvent()
    data class TriggerAnalyticEvent(val eventType: ManageScreenAnalyticsEvents) :
        ManageScreenEvent()

}