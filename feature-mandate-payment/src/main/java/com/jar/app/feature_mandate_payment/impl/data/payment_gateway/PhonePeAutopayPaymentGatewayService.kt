package com.jar.app.feature_mandate_payment.impl.data.payment_gateway

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.jar.app.base.ui.BaseResources
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.phonepe.PhonePeAutoPayResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.phonepe.PhonePeAutoPayResultData
import com.jar.app.feature_mandate_payments_common.shared.MR
import com.jar.app.feature_mandate_payments_common.shared.util.MandateErrorCodes
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentGateway
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import java.lang.ref.WeakReference

class PhonePeAutopayPaymentGatewayService(
    private val activityRef: WeakReference<FragmentActivity>,
    private val analyticsApi: AnalyticsApi
) : AutopayPaymentGatewayService,
    BaseResources {

    companion object {
        private const val REQUEST_CODE_PHONEPE_AUTOPAY_SUBSCRIPTION = 7386
    }

    private val activity by lazy {
        activityRef.get()!!
    }

    private var listener: AutopayPaymentGatewayService.OnAutoPayListener? = null

    private var isPhonePeLaunched = false
    private var authRequestId: String? = null

    override fun initiateMandatePayment(
        packageName: String?,
        initiateMandatePaymentApiResponse: InitiateMandatePaymentApiResponse,
        listener: AutopayPaymentGatewayService.OnAutoPayListener
    ) {

        this.listener = listener
        this.authRequestId = initiateMandatePaymentApiResponse.phonePe?.authReqId!!
        initPhonePeAutoPay(
            initiateMandatePaymentApiResponse.phonePe!!,
            packageName!!
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PHONEPE_AUTOPAY_SUBSCRIPTION) {
            if (isPhonePeLaunched) {
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
                                phonePeAutoPayResultData = PhonePeAutoPayResultData(authRequestId!!)
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

    private fun initPhonePeAutoPay(
        phonePeAutoPayResponse: PhonePeAutoPayResponse,
        packageName: String
    ) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(phonePeAutoPayResponse.redirectUrl)
        intent.setPackage(packageName)
        isPhonePeLaunched = try {
            activity.startActivityForResult(
                intent,
                REQUEST_CODE_PHONEPE_AUTOPAY_SUBSCRIPTION, null
            )
            analyticsApi.postEvent(
                MandatePaymentEventKey.Mandate_UpiAppInitiated,
                mapOf(
                    MandatePaymentEventKey.UpiApp to packageName,
                    MandatePaymentEventKey.MandatePaymentGateway to MandatePaymentGateway.PHONE_PE.name
                )
            )
            true
        } catch (e: Exception) {
            analyticsApi.postEvent(
                MandatePaymentEventKey.Mandate_UpiAppInitiated,
                mapOf(
                    MandatePaymentEventKey.UpiApp to packageName,
                    MandatePaymentEventKey.MandatePaymentGateway to MandatePaymentGateway.PHONE_PE.name,
                    MandatePaymentEventKey.Error to e.message.orEmpty()
                )
            )
            false
        }
    }
}