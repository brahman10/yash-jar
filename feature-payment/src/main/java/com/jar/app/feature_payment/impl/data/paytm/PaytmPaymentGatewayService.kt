package com.jar.app.feature_payment.impl.data.paytm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_payment.BuildConfig
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.impl.data.base.PaymentGatewayService
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.OneTimePaymentResult
import com.jar.app.feature_one_time_payments.shared.data.model.paytm.InitiatePaytmPaymentResponse
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_payment.impl.util.OneTimePaymentErrorCode
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.feature_one_time_payments.shared.data.model.base.PaytmPaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.GetAvailableUpiApps
import com.jar.internal.library.jar_core_network.api.util.collect
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.ref.WeakReference

internal class PaytmPaymentGatewayService(
    activityRef: WeakReference<FragmentActivity>,
    private val serializer: Serializer,
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase
) : PaymentGatewayService, PaytmPaymentTransactionCallback {

    companion object {
        private const val REQUEST_CODE_PAYTM_ONE_TIME_PAYMENT = 7384
    }

    private val activity = activityRef.get()

    private var listener: PaymentManager.OnPaymentResultListener? = null

    private var initiatePaymentResponse: InitiatePaymentResponse? = null

    private var job: Job? = null

    override fun init(userId: String) {

    }

    override fun initiate(
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener
    ) {
        this.listener = paymentListener
        this.initiatePaymentResponse = initiatePaymentResponse
        val transactionManager =
            TransactionManager(initiatePaymentResponse.paytm?.toPaytmOrder(), this)
        transactionManager.setShowPaymentUrl(BuildConfig.PAYTM_PAYMENT_URL)
        transactionManager.startTransaction(activity, REQUEST_CODE_PAYTM_ONE_TIME_PAYMENT)
    }

    override fun initiateWithCustomUI(
        customUiFragmentId: Int,
        fragmentDeepLink: String,
        isBottomSheet: Boolean,
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener
    ) {
        throw Exception("Custom UI Flow is not supported with Paytm PG")
    }

    override fun initiatePaymentWithUpiApp(
        initiatePaymentFragmentId: Int,
        upiApp: UpiApp,
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener
    ) {
        throw Exception("Strip Flow is not supported with Paytm PG")
    }

    override fun unregisterListener() {
        this.listener = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PAYTM_ONE_TIME_PAYMENT) {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (isBackPressedFromPaytm(data)) {
                    listener?.onError(
                        activity?.getString(R.string.transaction_cancelled),
                        errorCode = OneTimePaymentErrorCode.ERROR_CODE_BACK_PRESSED
                    )
                    return
                }

                val response = data?.getStringExtra("response")

                if (response.isNullOrBlank()) {
                    listener?.onError(activity?.getString(R.string.some_error_occurred))
                } else {
                    val paytmResponse =
                        serializer.decodeFromString<InitiatePaytmPaymentResponse>(response)

                    val oneTimePaymentResult = OneTimePaymentResult(
                        oneTimePaymentGateway = OneTimePaymentGateway.PAYTM,
                        orderId = paytmResponse.ORDERID!!,
                        amount = paytmResponse.TXNAMOUNT?.toFloatOrNull()!!,
                        fetchCurrentGoldPriceResponse = initiatePaymentResponse?.fetchCurrentGoldPriceResponse,
                        initiatePaytmPaymentResponse = paytmResponse
                    )

                    val fetchManualPaymentRequest = FetchManualPaymentRequest(
                        paymentProvider = OneTimePaymentGateway.PAYTM.name,
                        orderId = paytmResponse.ORDERID!!,
                        paytm = paytmResponse?.toPaytmPaymentResultData()
                    )

                    job?.cancel()
                    job = activity?.lifecycleScope?.launch {
                        fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                            fetchManualPaymentRequest
                        ).collect(
                            onLoading = {
                                listener?.onLoading(true)
                            },
                            onSuccess = {
                                listener?.onSuccess(it)
                            },
                            onError = { errorMessage, errorCode ->
                                listener?.onError(
                                    message = errorMessage,
                                    oneTimePaymentResult = oneTimePaymentResult,
                                    errorCode = errorCode
                                )
                            }
                        )
                    }
                }
            } else {
                listener?.onError(
                    activity?.getString(R.string.transaction_cancelled),
                    errorCode = OneTimePaymentErrorCode.ERROR_CODE_BACK_PRESSED
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

    }

    override fun onBackPress(): Boolean {
        return false
    }

    override fun tearDown() {

    }

    private fun isBackPressedFromPaytm(intent: Intent?): Boolean {
        return intent?.getStringExtra("nativeSdkForMerchantMessage")
            .equals("onBackPressedCancelTransaction", true)
    }

    override fun onTransactionResponse(inResponse: Bundle?) {
        if (inResponse != null && inResponse.isEmpty.not()) {
            val response = toPaytmPaymentResponse(inResponse)
            //This is being done to show error snackbar
            if (response.STATUS == "TXN_FAILURE")
                listener?.onError(response.RESPMSG.orEmpty())
            else {
                val oneTimePaymentResult = OneTimePaymentResult(
                    oneTimePaymentGateway = OneTimePaymentGateway.JUSPAY,
                    orderId = response.ORDERID!!,
                    amount = response.TXNAMOUNT?.toFloatOrNull()!!,
                    fetchCurrentGoldPriceResponse = initiatePaymentResponse?.fetchCurrentGoldPriceResponse!!,
                    initiatePaytmPaymentResponse = response
                )

                val fetchManualPaymentRequest = FetchManualPaymentRequest(
                    paymentProvider = OneTimePaymentGateway.PAYTM.name,
                    orderId = response.ORDERID!!,
                    paytm = response.toPaytmPaymentResultData()
                )

                job?.cancel()
                job = activity?.lifecycleScope?.launch {
                    fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                        fetchManualPaymentRequest
                    ).collect(
                        onLoading = {
                            listener?.onLoading(true)
                        },
                        onSuccess = {
                            listener?.onSuccess(it)
                        },
                        onError = { errorMessage, errorCode ->
                            listener?.onError(
                                message = errorMessage,
                                oneTimePaymentResult = oneTimePaymentResult,
                                errorCode = errorCode
                            )
                        }
                    )
                }

            }
        } else {
            listener?.onError(activity?.getString(R.string.some_error_occurred))
        }
    }

    override fun clientAuthenticationFailed(p0: String?) {
        listener?.onError(p0 ?: activity?.getString(R.string.some_error_occurred))
    }

    override fun someUIErrorOccurred(p0: String?) {
        listener?.onError(p0 ?: activity?.getString(R.string.some_error_occurred))
    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
        listener?.onError(p0 ?: activity?.getString(R.string.transaction_cancelled))
    }

    override fun networkNotAvailable() {
        listener?.onError(activity?.getString(R.string.internet_not_working_properly))
    }

    override fun onErrorProceed(p0: String?) {
        listener?.onError(p0 ?: activity?.getString(R.string.some_error_occurred))
    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
        listener?.onError(p1 ?: activity?.getString(R.string.some_error_occurred))
    }

    override fun onBackPressedCancelTransaction() {
        listener?.onError(
            activity?.getString(R.string.transaction_cancelled),
            errorCode = OneTimePaymentErrorCode.ERROR_CODE_BACK_PRESSED
        )
    }

    private fun toPaytmPaymentResponse(bundle: Bundle): InitiatePaytmPaymentResponse {
        return try {
            val data = JSONObject(bundle.get("body").toString()).get("txnInfo").toString()
            serializer.decodeFromString<InitiatePaytmPaymentResponse>(data)
        } catch (e: Exception) {
            getFromBundleKeys(bundle)
        }
    }

    private fun getFromBundleKeys(bundle: Bundle): InitiatePaytmPaymentResponse {
        return InitiatePaytmPaymentResponse(
            BANKNAME = bundle.getString("BANKNAME"),
            BANKTXNID = bundle.getString("BANKTXNID"),
            CHECKSUMHASH = bundle.getString("CHECKSUMHASH"),
            CURRENCY = bundle.getString("CURRENCY"),
            GATEWAYNAME = bundle.getString("GATEWAYNAME"),
            MID = bundle.getString("MID"),
            ORDERID = bundle.getString("ORDERID"),
            PAYMENTMODE = bundle.getString("PAYMENTMODE"),
            RESPCODE = bundle.getString("RESPCODE"),
            RESPMSG = bundle.getString("RESPMSG"),
            STATUS = bundle.getString("STATUS"),
            CHARGEAMOUNT = bundle.getString("CHARGEAMOUNT"),
            TXNAMOUNT = bundle.getString("TXNAMOUNT"),
            TXNDATE = bundle.getString("TXNDATE"),
            TXNID = bundle.getString("TXNID"),
            SUBS_ID = bundle.getString("SUBS_ID"),
        )
    }

    private fun PaytmPaymentResponse.toPaytmOrder() = PaytmOrder(
        this.orderId,
        BuildConfig.PAYTM_MID,
        this.txnToken,
        this.txnAmount.toString(),
        BuildConfig.PAYTM_CALLBACK_URL
    )

}