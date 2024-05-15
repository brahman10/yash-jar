package com.jar.app.feature_payment.impl.data.base

import android.content.Intent
import androidx.annotation.IdRes
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.GetAvailableUpiApps
import com.jar.app.feature_payment.api.PaymentManager

internal interface PaymentGatewayService {

    fun init(userId: String)

    fun initiate(
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener
    )

    fun initiateWithCustomUI(
        @IdRes customUiFragmentId: Int,
        fragmentDeepLink: String,
        isBottomSheet: Boolean, // Need this flag to open the bottom sheet again in case of retry flow
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener
    )

    fun initiatePaymentWithUpiApp(
        @IdRes initiatePaymentFragmentId: Int,
        upiApp: UpiApp,
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener
    )

    fun unregisterListener()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )

    fun onBackPress(): Boolean

    fun tearDown()
}