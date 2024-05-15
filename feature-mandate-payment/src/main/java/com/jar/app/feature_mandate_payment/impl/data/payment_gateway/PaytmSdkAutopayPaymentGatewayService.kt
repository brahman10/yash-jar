package com.jar.app.feature_mandate_payment.impl.data.payment_gateway

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.jar.app.base.ui.BaseResources
import com.jar.app.feature_mandate_payments_common.shared.MR
import com.jar.app.feature_mandate_payments_common.shared.MandatePaymentBuildKonfig
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_sdk.PaytmAutoPayPaymentResultData
import com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_sdk.PaytmAutopayPaymentResponseFromSdk
import com.jar.app.feature_mandate_payments_common.shared.util.MandateErrorCodes
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentGateway
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import java.lang.ref.WeakReference

class PaytmSdkAutopayPaymentGatewayService(
    private val activityRef: WeakReference<FragmentActivity>,
    private val serializer: Serializer,
    private val analyticsApi: AnalyticsApi
) : AutopayPaymentGatewayService,
    PaytmPaymentTransactionCallback,
    BaseResources {

    companion object {
        private const val REQUEST_CODE_PAYTM_AUTOPAY_SUBSCRIPTION = 7385
    }

    private val activity by lazy {
        activityRef.get()!!
    }

    private var listener: AutopayPaymentGatewayService.OnAutoPayListener? = null

    override fun initiateMandatePayment(
        packageName: String?,
        initiateMandatePaymentApiResponse: InitiateMandatePaymentApiResponse,
        listener: AutopayPaymentGatewayService.OnAutoPayListener
    ) {
        val paytmOrder = PaytmOrder(
            initiateMandatePaymentApiResponse.paytm?.orderId,
            initiateMandatePaymentApiResponse.paytm?.mid ?: MandatePaymentBuildKonfig.PAYTM_MID,
            initiateMandatePaymentApiResponse.paytm?.txnToken,
            initiateMandatePaymentApiResponse.paytm?.txnAmount.toString(),
            MandatePaymentBuildKonfig.PAYTM_CALLBACK_URL
        )
        this.listener = listener
        analyticsApi.postEvent(
            MandatePaymentEventKey.Mandate_UpiAppInitiated,
            mapOf(
                MandatePaymentEventKey.UpiApp to packageName.orEmpty(),
                MandatePaymentEventKey.MandatePaymentGateway to MandatePaymentGateway.PAYTM.name
            )
        )
        val transactionManager =
            TransactionManager(paytmOrder, this)
        transactionManager.setShowPaymentUrl(MandatePaymentBuildKonfig.PAYTM_PAYMENT_URL)
        transactionManager.startTransaction(activity, REQUEST_CODE_PAYTM_AUTOPAY_SUBSCRIPTION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PAYTM_AUTOPAY_SUBSCRIPTION) {
            if (isBackPressedFromPaytm(data)) {
                listener?.onResult(
                    RestClientResult.error(
                        getCustomString(
                            activity,
                            MR.strings.feature_mandate_payment_transaction_cancelled
                        ),
                        errorCode = MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN
                    )
                )
                return
            }

            val response = data?.getStringExtra("response")

            if (response.isNullOrBlank()) {
                listener?.onResult(RestClientResult.error(activity.getString(com.jar.app.base.R.string.app_name)))
            } else {
                val paytmResponse =
                    serializer.decodeFromString<PaytmAutopayPaymentResponseFromSdk>(response)
                val paymentResult =
                    MandatePaymentResultFromSDK(
                        paytmSdkAutoPayPaymentResultData = paytmResponse.toPaytmPaymentResultData()
                    )
                listener?.onResult(RestClientResult.success(paymentResult))
            }
        }
    }

    override fun unregisterListener() {
        listener = null
    }

    private fun isBackPressedFromPaytm(intent: Intent?): Boolean {
        return intent?.getStringExtra("nativeSdkForMerchantMessage")
            .equals("onBackPressedCancelTransaction", true)
    }

    override fun onTransactionResponse(inResponse: Bundle?) {
        if (inResponse != null && inResponse.isEmpty.not()) {
            val response = getFromBundleKeys(inResponse)
            if (response.respCode?.toIntOrNull() == 141) {
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
                val paymentResult =
                    MandatePaymentResultFromSDK(paytmSdkAutoPayPaymentResultData = response)
                listener?.onResult(RestClientResult.success(paymentResult))
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

    override fun clientAuthenticationFailed(p0: String?) {
        listener?.onResult(
            RestClientResult.error(
                p0 ?: getCustomString(
                    activity,
                    MR.strings.feature_mandate_payment_something_went_wrong
                )
            )
        )
    }

    override fun someUIErrorOccurred(p0: String?) {
        listener?.onResult(
            RestClientResult.error(
                p0 ?: getCustomString(
                    activity,
                    MR.strings.feature_mandate_payment_something_went_wrong
                )
            )
        )
    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
        listener?.onResult(
            RestClientResult.error(
                p0 ?: getCustomString(
                    activity,
                    MR.strings.feature_mandate_payment_something_went_wrong
                )
            )
        )
    }

    override fun networkNotAvailable() {
        listener?.onResult(RestClientResult.error(activity.getString(com.jar.app.base.R.string.please_check_your_internet_connection)))
    }

    override fun onErrorProceed(p0: String?) {
        listener?.onResult(
            RestClientResult.error(
                p0 ?: getCustomString(
                    activity,
                    MR.strings.feature_mandate_payment_something_went_wrong
                )
            )
        )
    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
        listener?.onResult(
            RestClientResult.error(
                p1 ?: getCustomString(
                    activity,
                    MR.strings.feature_mandate_payment_something_went_wrong
                )
            )
        )
    }

    override fun onBackPressedCancelTransaction() {
        listener?.onResult(
            RestClientResult.error(
                getCustomString(activity, MR.strings.feature_mandate_payment_transaction_cancelled),
                errorCode = MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN
            )
        )
    }

    private fun getFromBundleKeys(bundle: Bundle): PaytmAutoPayPaymentResultData {
        return PaytmAutoPayPaymentResultData(
            bankName = bundle.getString("BANKNAME"),
            bankTxnId = bundle.getString("BANKTXNID"),
            checksumHash = bundle.getString("CHECKSUMHASH"),
            currency = bundle.getString("CURRENCY"),
            gatewayName = bundle.getString("GATEWAYNAME"),
            mid = bundle.getString("MID"),
            orderId = bundle.getString("ORDERID"),
            paymentMode = bundle.getString("PAYMENTMODE"),
            respCode = bundle.getString("RESPCODE"),
            respMsg = bundle.getString("RESPMSG"),
            status = bundle.getString("STATUS"),
            chargeAmount = bundle.getString("CHARGEAMOUNT"),
            txnAmount = bundle.getString("TXNAMOUNT"),
            txnDate = bundle.getString("TXNDATE"),
            txnId = bundle.getString("TXNID"),
            subsId = bundle.getString("SUBS_ID"),
        )
    }

}