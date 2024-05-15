package com.jar.health_insurance.impl.ui

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.util.Constants
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.UpiAppPaymentPageItem
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PaymentBottomSheetViewModel @Inject constructor(
    private val analyticsApi: AnalyticsApi
): ViewModel() {
    private val _uiState = MutableStateFlow(MandateBottomSheetState())
    val uiState = _uiState.asStateFlow()

    var amount: String? = null
    var paymentType: String? = null

    private var preSelectedUpiApp: String? = null

    private val _paymentOptionFlow =
        MutableStateFlow<List<DailySavingsMandatePaymentOption>?>(emptyList())
    val paymentOptionFlow: StateFlow<List<DailySavingsMandatePaymentOption>?> get() = _paymentOptionFlow


    fun onTriggerEvent(eventType: MandateBottomSheetEvents) {
        when (eventType) {
            is MandateBottomSheetEvents.OnInitiatePaymentResponseChanged -> onInitiatePaymentResponseChanged(eventType.initiatePaymentResponse)
            is MandateBottomSheetEvents.OnUpiAppListChanged -> onUpiAppsChanged(eventType.upiAppList)
            is MandateBottomSheetEvents.OnSelectedIndexChanged -> onSelectedIndexChanged(eventType.index)
            is MandateBottomSheetEvents.OnMandateUpiAppsChanged -> onMandateUpiAppsChanged(eventType.upiApp)
            is MandateBottomSheetEvents.OnChangeSelectedAppPackageName -> onChangeSelectedAppPackageName(eventType.packageName)
            else -> {}
        }
    }

    private fun onChangeSelectedAppPackageName(packageName: String) {
        _uiState.update {
            it.copy(
                selectedAppPackageName = packageName
            )
        }
    }

    private fun onMandateUpiAppsChanged(upiApp: List<com.jar.app.feature_mandate_payment_common.impl.model.UpiApp>) {

        upiApp.forEachIndexed{ index, upiApp ->
            if(upiApp.packageName == _uiState.value.selectedAppPackageName){
                preSelectedUpiApp = upiApp.appName
                _uiState.update {
                    it.copy(
                        selectedIndex = index
                    )
                }
            }
        }

        _uiState.update {
            it.copy(
                mandateUpiList = upiApp
            )
        }
    }

    private fun onSelectedIndexChanged(index: Int) {
        _uiState.update {
            it.copy(
                selectedIndex = index
            )
        }
    }

    suspend fun createPaymentOptionList(
        data: List<BasePaymentPageItem>,
        packageManager: PackageManager
    ) {
        val paymentOptionList = mutableListOf<DailySavingsMandatePaymentOption>()

        data.forEach {
            if (it is UpiAppPaymentPageItem) {
                val upiApp = getUpiAppFromPackageName(
                    it.upiAppPackageName,
                    packageManager
                )
                val dailySavingsMandateItem = DailySavingsMandatePaymentOption(
                    packageName = upiApp.packageName,
                    optionIcon = upiApp.icon!!,
                    optionName = upiApp.appName
                )
                paymentOptionList.add(dailySavingsMandateItem)
            }

        }
        _paymentOptionFlow.emit(paymentOptionList)
    }

    private fun getUpiAppFromPackageName(
        packageName: String,
        packageManager: PackageManager
    ): com.jar.app.feature_mandate_payment_common.impl.model.UpiApp {
        return com.jar.app.feature_mandate_payment_common.impl.model.UpiApp(
            packageName = packageName,
            icon = packageManager.getApplicationIcon(packageName),
            appName = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.GET_META_DATA
                )
            ).toString()
        )
    }

    private fun onUpiAppsChanged(upiAppList: List<UpiApp>) {
        upiAppList.forEachIndexed{ index, upiApp ->
            if(upiApp.packageName == _uiState.value.selectedAppPackageName){
                preSelectedUpiApp = upiApp.appName

                _uiState.update {
                    it.copy(
                        selectedIndex = index
                    )
                }
            }
        }
        _uiState.update {
            it.copy(
                upiAppsList = upiAppList
            )
        }
    }

    fun onClickedAnalyticsEvent(buttonType: String){
        analyticsApi.postEvent(
            HealthInsuranceEvents.Health_Insurance_PaymentBSScreenClicked,
            mapOf(
                HealthInsuranceEvents.Premium_Amount to amount!!,
                HealthInsuranceEvents.NUMBER_OF_AVAILABLE_UPI_APP to if(paymentType == Constants.MANUAL_PAYMENT) uiState.value.upiAppsList.toString() else uiState.value.mandateUpiList.toString(),
                HealthInsuranceEvents.SELECTED_UPI to if(paymentType == Constants.MANUAL_PAYMENT) uiState.value.upiAppsList[uiState.value.selectedIndex].appName else uiState.value.mandateUpiList[uiState.value.selectedIndex].appName,
                HealthInsuranceEvents.PRE_SELECTED_UPI to preSelectedUpiApp.orEmpty(),
                HealthInsuranceEvents.Button to buttonType,
                HealthInsuranceEvents.Payment_type to if(paymentType == Constants.MANUAL_PAYMENT) HealthInsuranceEvents.PAYMENT_TYPE_MANUAL else HealthInsuranceEvents.PAYMENT_TYPE_MANDATE_SETUP
            )
        )
    }

    private fun onInitiatePaymentResponseChanged(initiatePaymentResponse: InitiatePaymentResponse?) {
        _uiState.update {
            it.copy(
                initiatePaymentResponse = initiatePaymentResponse
            )
        }
    }



    fun onPaymentScreenShown(){
        analyticsApi.postEvent(
            HealthInsuranceEvents.Health_Insurance_PaymentBSScreenShown,
            mapOf(
                HealthInsuranceEvents.Premium_Amount to amount!!,
                HealthInsuranceEvents.NUMBER_OF_AVAILABLE_UPI_APP to if(paymentType == Constants.MANUAL_PAYMENT) _uiState.value.upiAppsList.toString() else _uiState.value.mandateUpiList.toString(),
                HealthInsuranceEvents.PRE_SELECTED_UPI to if(paymentType == Constants.MANUAL_PAYMENT) _uiState.value.upiAppsList[uiState.value.selectedIndex].appName else _uiState.value.mandateUpiList[uiState.value.selectedIndex].appName,
                HealthInsuranceEvents.Payment_type to if(paymentType == Constants.MANUAL_PAYMENT) HealthInsuranceEvents.PAYMENT_TYPE_MANUAL else HealthInsuranceEvents.PAYMENT_TYPE_MANDATE_SETUP
            )
        )
    }
}

data class DailySavingsMandatePaymentOption(
    val packageName: String,
    val optionIcon: Drawable,
    val optionName: String
)