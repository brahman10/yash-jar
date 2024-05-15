package com.jar.health_insurance.impl.ui

import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp as MandateUpiApp


data class MandateBottomSheetState(
    val initiatePaymentResponse: InitiatePaymentResponse? = null,
    val upiAppsList: List<UpiApp> = emptyList(),
    val mandateUpiList: List<MandateUpiApp> = emptyList(),
    val selectedIndex: Int = 0,
    val selectedAppPackageName: String = ""
)

sealed class MandateBottomSheetEvents {
    data class OnInitiatePaymentResponseChanged(val initiatePaymentResponse: InitiatePaymentResponse?): MandateBottomSheetEvents()
    data class OnUpiAppListChanged(val upiAppList: List<UpiApp>): MandateBottomSheetEvents()
    data class OnSelectedIndexChanged(val index: Int) : MandateBottomSheetEvents()
    data class OnMandateUpiAppsChanged(val upiApp: List<MandateUpiApp>): MandateBottomSheetEvents()
    data class OnChangeSelectedAppPackageName(val packageName: String): MandateBottomSheetEvents()
}