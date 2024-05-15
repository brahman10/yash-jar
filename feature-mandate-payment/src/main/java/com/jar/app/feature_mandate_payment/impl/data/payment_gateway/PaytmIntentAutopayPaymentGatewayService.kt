package com.jar.app.feature_mandate_payment.impl.data.payment_gateway

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.jar.app.base.ui.BaseResources
import com.jar.app.feature_mandate_payments_common.shared.MR
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_intent.PaytmIntentAutoPayPaymentResultData
import com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_intent.PaytmIntentAutopayPaymentResponse
import com.jar.app.feature_mandate_payments_common.shared.util.MandateErrorCodes
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentGateway
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import java.lang.ref.WeakReference

class PaytmIntentAutopayPaymentGatewayService(
    private val activityRef: WeakReference<FragmentActivity>,
    private val analyticsApi: AnalyticsApi
) : AutopayPaymentGatewayService, BaseResources {

    companion object {
        private const val REQUEST_CODE_PAYTM_AUTOPAY_SUBSCRIPTION = 7387
    }

    private val activity by lazy {
        activityRef.get()!!
    }

    private var listener: AutopayPaymentGatewayService.OnAutoPayListener? = null

    private var orderId: String? = null

    private var isIntentAppLaunched = false

    override fun initiateMandatePayment(
        packageName: String?,
        initiateMandatePaymentApiResponse: InitiateMandatePaymentApiResponse,
        listener: AutopayPaymentGatewayService.OnAutoPayListener
    ) {
        this.listener = listener
        this.orderId = initiateMandatePaymentApiResponse.paytmIntent?.orderId
        if (initiateMandatePaymentApiResponse.paytmIntent != null && packageName != null) {
            initiateIntentApp(
                initiateMandatePaymentApiResponse.paytmIntent!!,
                packageName
            )
        } else {
            listener.onResult(
                RestClientResult.error(
                    getCustomString(
                        activity,
                        MR.strings.feature_mandate_payment_something_went_wrong
                    )
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PAYTM_AUTOPAY_SUBSCRIPTION) {
            if (isIntentAppLaunched) {
                if (resultCode == Activity.RESULT_CANCELED) {
                    listener?.onResult(
                        RestClientResult.error(
                            getCustomString(
                                activity,
                                MR.strings.feature_mandate_payment_transaction_cancelled
                            ),
                            errorCode = MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN
                        )
                    )
                } else {
                    listener?.onResult(
                        RestClientResult.success(
                            MandatePaymentResultFromSDK(
                                paytmIntentAutoPayPaymentResultData =
                                PaytmIntentAutoPayPaymentResultData(orderId!!)
                            )
                        )
                    )
                }
            } else {
                listener?.onResult(
                    RestClientResult.error(
                        getCustomString(
                            activity,
                            MR.strings.feature_mandate_payment_something_went_wrong
                        )
                    )
                )
            }
        }
    }

    override fun unregisterListener() {
        listener = null
    }

    private fun initiateIntentApp(
        paytmIntentAutopayPaymentResponse: PaytmIntentAutopayPaymentResponse,
        packageName: String
    ) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(paytmIntentAutopayPaymentResponse.redirectUrl)
        intent.setPackage(packageName)
        isIntentAppLaunched = try {
            activity.startActivityForResult(
                intent,
                REQUEST_CODE_PAYTM_AUTOPAY_SUBSCRIPTION, null
            )
            analyticsApi.postEvent(
                MandatePaymentEventKey.Mandate_UpiAppInitiated,
                mapOf(
                    MandatePaymentEventKey.UpiApp to packageName,
                    MandatePaymentEventKey.MandatePaymentGateway
                            to "${MandatePaymentGateway.PAYTM.name} Intent"
                )
            )
            true
        } catch (e: Exception) {
            analyticsApi.postEvent(
                MandatePaymentEventKey.Mandate_UpiAppInitiated,
                mapOf(
                    MandatePaymentEventKey.UpiApp to packageName,
                    MandatePaymentEventKey.MandatePaymentGateway to "${MandatePaymentGateway.PAYTM.name} Intent",
                    MandatePaymentEventKey.Error to e.message.orEmpty()
                )
            )
            false
        }
    }
}