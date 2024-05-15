package com.jar.health_insurance.impl.ui.select_insurance_plan

import androidx.annotation.DrawableRes
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.PremiumOption
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.SelectPremiumResponse
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp as MandateUpiApp
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp as OneTimeUpiApp
import javax.annotation.concurrent.Immutable

@Immutable
data class SelectInsuranceState(
    val selectPremiumResponse: SelectPremiumResponse? = null,
    val planSelected: Int = 1,
    val subscriptionSelected: Int = 0,
    val paymentConfigResponse: InitiatePaymentResponse? = null,
    val errorMessage: String? = null,
    val shouldShowPopUp: Boolean = false,
    val mandateUpiApp: MandateUpiApp? = null,
    val oneTimeUpiApp: OneTimeUpiApp? = null
)

sealed class SelectInsuranceEvents{
    data class OnDataLoad(val orderId: String, val mandateUpiApp: MandateUpiApp?, val oneTimeUpiApp: OneTimeUpiApp?) : SelectInsuranceEvents()
    data class OnButtonClick(@DrawableRes val icon: Int): SelectInsuranceEvents()
    data class OnPlanSelection(val planSelected: Int, val premiumOptionList: List<PremiumOption?>) : SelectInsuranceEvents()
    data class OnSubscriptionSelected(val subscriptionSelected: Int): SelectInsuranceEvents()
    object OnErrorMessageDisplayed: SelectInsuranceEvents()
}