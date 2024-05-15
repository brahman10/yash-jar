package com.jar.app.feature_mandate_payment.api

import android.content.Intent
import androidx.annotation.IdRes
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentGateway
import kotlinx.coroutines.flow.Flow

interface MandatePaymentApi {

    suspend fun initiateMandatePayment(
        paymentPageHeaderDetails: PaymentPageHeaderDetail,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ): Flow<RestClientResult<Pair<MandatePaymentResultFromSDK, FetchMandatePaymentStatusResponse>>>

    suspend fun initiateMandatePaymentWithCustomUI(
        /**
         * This should be equal to the container ID in case the custom screen is bottom sheet
         * b/c bottom sheet gets auto-dismissed when we redirect to verifyFragment ,
         * and we use app selection fragment's savedStateHandle to pass the data back to MandatePaymentApiImpl
         * so in case of bottom sheet savedStateHandle becomes null that's why we pass container ID in case of bottom sheet
         * **/
        @IdRes customMandateUiFragmentId: Int,
        fragmentDeepLink: String,
        paymentPageHeaderDetails: PaymentPageHeaderDetail,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ): Flow<RestClientResult<Pair<MandatePaymentResultFromSDK, FetchMandatePaymentStatusResponse>>>

    suspend fun fetchLastUsedUpiApp(flowType: String?): Flow<RestClientResult<UpiApp?>>

    suspend fun initiateMandatePaymentWithUpiApp(
        @IdRes initiateMandateFragmentId: Int,
        paymentPageHeaderDetails: PaymentPageHeaderDetail,
        upiApp: UpiApp,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ): Flow<RestClientResult<Pair<MandatePaymentResultFromSDK, FetchMandatePaymentStatusResponse>>>

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun teardown()

    fun getMandatePaymentGateway(): MandatePaymentGateway
}