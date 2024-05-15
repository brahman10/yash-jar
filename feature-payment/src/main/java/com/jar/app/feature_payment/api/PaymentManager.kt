package com.jar.app.feature_payment.api

import android.content.Intent
import androidx.annotation.IdRes
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.OneTimePaymentResult
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.GetAvailableUpiApps
import kotlinx.coroutines.flow.Flow

interface PaymentManager {

    fun getDefaultPaymentGateway(): OneTimePaymentGateway

    fun getCurrentPaymentGateway(): OneTimePaymentGateway

    /** Should be called in onCreate of [activity] **/
    fun init(userId: String)

    @Deprecated("Replace with initiateOneTimePayment")
    fun initiate(
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: OnPaymentResultListener,
        paymentGateway: OneTimePaymentGateway = getDefaultPaymentGateway()
    )

    suspend fun initiateOneTimePayment(
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentGateway: OneTimePaymentGateway = getDefaultPaymentGateway()
    ): Flow<RestClientResult<FetchManualPaymentStatusResponse>>


    /**
     * Below method will only work when PG is [OneTimePaymentGateway.JUSPAY]..
     * */
    suspend fun initiateOneTimePaymentWithCustomUI(
        /**
         * This should be equal to the container ID in case the custom screen is bottom sheet
         * b/c bottom sheet gets auto-dismissed when we redirect to verifyFragment ,
         * **/
        @IdRes customUiFragmentId: Int,
        fragmentDeepLink: String,
        isBottomSheet: Boolean,
        initiatePaymentResponse: InitiatePaymentResponse
    ): Flow<RestClientResult<FetchManualPaymentStatusResponse>>

    suspend fun initiatePaymentWithUpiApp(
        initiatePageFragmentId: Int,
        initiatePaymentResponse: InitiatePaymentResponse,
        upiApp: UpiApp
    ): Flow<RestClientResult<FetchManualPaymentStatusResponse>>


    suspend fun fetchLastUsedUpiApp(flowContext: String? = null): Flow<RestClientResult<UpiApp?>>

    suspend fun fetchInstalledUpiApps(): List<UpiApp>

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )

    fun onBackPress(): Boolean

    /** Should be called inside onDestroy of [activity] **/
    fun tearDown()

    interface OnPaymentResultListener {
        fun onLoading(showLoading: Boolean)

        fun onSuccess(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse)

        fun onError(
            message: String?,
            errorCode: String? = null,
            oneTimePaymentResult: OneTimePaymentResult? = null,
            shouldFetchPaymentStatus: Boolean = false
        )
    }

}