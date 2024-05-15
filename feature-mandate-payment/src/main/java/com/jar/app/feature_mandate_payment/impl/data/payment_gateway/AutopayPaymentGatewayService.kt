package com.jar.app.feature_mandate_payment.impl.data.payment_gateway

import android.content.Intent
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse

interface AutopayPaymentGatewayService {

    fun initiateMandatePayment(
        packageName: String?,
        initiateMandatePaymentApiResponse: InitiateMandatePaymentApiResponse,
        listener: OnAutoPayListener
    )

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun unregisterListener()

    interface OnAutoPayListener {
        fun onResult(result: RestClientResult<MandatePaymentResultFromSDK>)
    }
}