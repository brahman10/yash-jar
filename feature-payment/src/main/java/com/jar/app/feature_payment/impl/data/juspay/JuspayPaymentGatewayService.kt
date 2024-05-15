package com.jar.app.feature_payment.impl.data.juspay

import `in`.juspay.hypersdk.data.JuspayResponseHandler
import `in`.juspay.hypersdk.ui.HyperPaymentsCallbackAdapter
import `in`.juspay.services.HyperServices
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.jar.app.feature_payment.impl.data.base.PaymentGatewayService
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_payment.BuildConfig
import com.jar.app.feature_payment.R
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.OneTimePaymentResult
import com.jar.app.feature_one_time_payments.shared.data.model.juspay.JuspayPaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.event.AvailableAppEvent
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.InitiateSdkPayload
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.PrefetchPayload
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants.JuspayAction
import com.jar.app.feature_payment.impl.util.OneTimePaymentErrorCode
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_network.util.toJSONObject
import com.jar.app.core_network.util.toJsonArray
import com.jar.app.feature_one_time_payments.shared.domain.event.CardInfoEvent
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateGetAvailableUpiAppWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateGetCardInfoWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateGetSavedCardsWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateNewCardPaymentWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateSavedCardPaymentWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateUpiIntentWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.SavedCardsEvents
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.CardInfo
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.GetAvailableUpiApps
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.InitiateUpiIntent
import com.jar.app.feature_payment.impl.domain.BackPressedOnPaymentPageEvent
import com.jar.app.feature_payment.impl.domain.ManualPaymentStatusFetchedEvent
import com.jar.app.feature_payment.impl.domain.RetryManualPaymentEvent
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

internal class JuspayPaymentGatewayService(
    activityRef: WeakReference<FragmentActivity>,
    navControllerRef: WeakReference<NavController>,
    private val hyperServices: HyperServices,
    private val serializer: Serializer
) : PaymentGatewayService, BaseNavigation, HyperPaymentsCallbackAdapter() {

    companion object {
        private const val ACTION = "initiate"

        private const val TAG = "#JusPay_SDK#"
    }

    private var job: Job? = null

    private val activity = activityRef.get()!!

    private val navController = navControllerRef.get()!!

    private var initiatePaymentResponse: InitiatePaymentResponse? = null

    private var paymentStatusResponse: FetchManualPaymentStatusResponse? = null

    private var listener: PaymentManager.OnPaymentResultListener? = null

    private var paymentPageFragmentId: Int? = null
    private var customUIPaymentPageDeepLink: String? = null
    private var isBottomSheetFlow: Boolean? = null

    init {
        EventBus.getDefault().register(this)
    }

    override fun init(userId: String) {
        val prefetchPayload = PrefetchPayload(
            service = BuildConfig.JUSPAY_SERVICE, clientId = BuildConfig.JUSPAY_CLIENT_ID
        )
        HyperServices.preFetch(activity, prefetchPayload.toJsonObject().toJSONObject())

        val initiationPayload = InitiateSdkPayload(
            requestId = UUID.randomUUID().toString(),
            service = BuildConfig.JUSPAY_SERVICE,
            action = ACTION,
            merchantId = BuildConfig.JUSPAY_MID,
            clientId = BuildConfig.JUSPAY_CLIENT_ID,
            customerId = userId,
            environment = BuildConfig.JUSPAY_ENVIRONMENT
        )
        Timber.tag(TAG)
            .d("INITIATE PAYLOAD  %s", initiationPayload.toJsonObject().toJSONObject().toString(4))
        hyperServices.initiate(initiationPayload.toJsonObject().toJSONObject(), this)
    }

    override fun initiate(
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener
    ) {
        this.listener = paymentListener
        this.paymentStatusResponse = null
        this.paymentPageFragmentId = R.id.paymentOptionPageFragment
        this.initiatePaymentResponse = initiatePaymentResponse
        val juspay = initiatePaymentResponse.juspay
        if (hyperServices.isInitialised) {
            Timber.tag(TAG).d("PROCESS PAYLOAD  %s", juspay)
            val encoded = encodeUrl(serializer.encodeToString(initiatePaymentResponse))
            activity.navigateTo(
                navController, "android-app://com.jar.app/paymentPage/${encoded}"
            )
        }
    }

    override fun initiateWithCustomUI(
        customUiFragmentId: Int,
        fragmentDeepLink: String,
        isBottomSheet: Boolean,
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener
    ) {
        this.paymentStatusResponse = null
        this.paymentPageFragmentId = customUiFragmentId
        this.customUIPaymentPageDeepLink = fragmentDeepLink
        this.isBottomSheetFlow = isBottomSheet
        this.initiatePaymentResponse = initiatePaymentResponse
        this.listener = paymentListener

        val juspay = initiatePaymentResponse.juspay
        if (hyperServices.isInitialised) {
            Timber.tag(TAG).d("PROCESS PAYLOAD  %s", juspay)
            activity.navigateTo(navController, fragmentDeepLink)
        }
    }

    override fun initiatePaymentWithUpiApp(
        initiatePaymentFragmentId: Int,
        upiApp: UpiApp,
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener
    ) {
        this.paymentStatusResponse = null
        this.paymentPageFragmentId = initiatePaymentFragmentId
        this.initiatePaymentResponse = initiatePaymentResponse
        this.listener = paymentListener

        val juspay = initiatePaymentResponse.juspay
        if (hyperServices.isInitialised) {
            Timber.tag(TAG).d("PROCESS PAYLOAD  %s", juspay)
            hyperServices.process(
                InitiateUpiIntent(
                    UUID.randomUUID().toString(),
                    initiatePaymentResponse.juspay?.orderId!!,
                    upiApp.packageName,
                    initiatePaymentResponse.juspay?.clientAuthToken!!
                ).toJsonObject().toJSONObject()
            )
        }
    }

    override fun unregisterListener() {
        this.listener = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let {
            hyperServices.onActivityResult(requestCode, resultCode, it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        hyperServices.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPress(): Boolean {
        return hyperServices.onBackPressed()
    }

    override fun tearDown() {
        job?.cancel()
        hyperServices.terminate()
        EventBus.getDefault().unregister(this)
    }

    override fun onEvent(data: JSONObject?, p1: JuspayResponseHandler?) {
        val event = data?.optString("event")
        Timber.tag(TAG).d("$event")
        when (event) {
            "show_loader" -> {
                listener?.onLoading(showLoading = true)
            }

            "hide_loader" -> {
                listener?.onLoading(showLoading = false)
            }

            "initiate_result" -> {
                Timber.tag(TAG).d("Juspay SDK initialized successfully")
            }

            "process_result" -> {
                val error: Boolean = data.optBoolean("error")
                val innerPayload: JSONObject? = data.optJSONObject("payload")
                val status: String? = innerPayload?.optString("status")
                val action = innerPayload?.optString("action")
                when (action) {
                    JuspayAction.GET_PAYMENT_METHODS -> {
//                        val paymentMethodArray = innerPayload.optJSONArray("paymentMethods")
//                        EventBus.getDefault().post(AvailablePaymentMethodEvent(paymentMethodArray))
                    }

                    JuspayAction.UPI_TXN -> {
                        val availableAppsArray = innerPayload.optJSONArray("availableApps")

                        if (availableAppsArray != null) {
                            EventBus.getDefault()
                                .post(AvailableAppEvent(availableAppsArray.toJsonArray()))
                        } else {
                            // Means it could be charge txn case
                            val orderId = innerPayload.getString("orderId")
                            val juspayPaymentResponse = JuspayPaymentResponse(
                                action = innerPayload.getString("action"),
                                status = innerPayload.getString("status"),
                                orderId = orderId,
                            )

                            val paymentResult = OneTimePaymentResult(
                                oneTimePaymentGateway = com.jar.app.core_base.domain.model.OneTimePaymentGateway.JUSPAY,
                                orderId = orderId,
                                amount = initiatePaymentResponse?.amount!!,
                                fetchCurrentGoldPriceResponse = initiatePaymentResponse?.fetchCurrentGoldPriceResponse,
                                juspayPaymentResponse = juspayPaymentResponse,
                                transactionType = initiatePaymentResponse?.transactionType,
                                isRetryAllowed = initiatePaymentResponse?.isRetryAllowed
                            )

                            if (this.paymentPageFragmentId != null) {
                                val oneTimePaymentResultEncoded =
                                    encodeUrl(serializer.encodeToString(paymentResult))
                                val deepLink =
                                    "android-app://com.jar.app/verifyPaymentStatusFragment/$oneTimePaymentResultEncoded/$paymentPageFragmentId"
                                activity.navigateTo(navController, deepLink)
                            }
                        }
                    }

                    JuspayAction.CARD_INFO -> {
                        EventBus.getDefault().postSticky(
                            CardInfoEvent(
                                CardInfo(
                                    type = innerPayload.optString("type"),
                                    brand = innerPayload.optString("brand"),
                                    bank = innerPayload.optString("bank"),
                                )
                            )
                        )
                    }

                    JuspayAction.CARD_TXN -> {
                        val orderId = innerPayload.getString("orderId")

                        val juspayPaymentResponse = JuspayPaymentResponse(
                            action = innerPayload.optString("action"),
                            status = innerPayload.optString("status"),
                            orderId = orderId,
                        )

                        val paymentResult = OneTimePaymentResult(
                            oneTimePaymentGateway = com.jar.app.core_base.domain.model.OneTimePaymentGateway.JUSPAY,
                            orderId = orderId,
                            amount = initiatePaymentResponse?.amount!!,
                            fetchCurrentGoldPriceResponse = initiatePaymentResponse?.fetchCurrentGoldPriceResponse,
                            juspayPaymentResponse = juspayPaymentResponse,
                            transactionType = initiatePaymentResponse?.transactionType,
                            isRetryAllowed = initiatePaymentResponse?.isRetryAllowed
                        )

                        if (this.paymentPageFragmentId != null) {
                            val oneTimePaymentResultEncoded =
                                encodeUrl(serializer.encodeToString(paymentResult))
                            val deepLink =
                                "android-app://com.jar.app/verifyPaymentStatusFragment/$oneTimePaymentResultEncoded/$paymentPageFragmentId"
                            activity.navigateTo(navController, deepLink)
                        }
                    }

                    JuspayAction.CARD_LIST -> {
                        val cardsArray = innerPayload.optJSONArray("cards")
                        if (cardsArray != null) {
                            EventBus.getDefault()
                                .postSticky(SavedCardsEvents(cardsArray.toJsonArray()))
                        }
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onManualPaymentStatusFetchedEvent(manualPaymentStatusFetchedEvent: ManualPaymentStatusFetchedEvent) {
        this.paymentStatusResponse =
            manualPaymentStatusFetchedEvent.fetchManualPaymentStatusResponse
        this.listener?.onSuccess(manualPaymentStatusFetchedEvent.fetchManualPaymentStatusResponse)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackPressedOnPaymentPageEvent(backPressedOnPaymentPageEvent: BackPressedOnPaymentPageEvent) {
        if (paymentStatusResponse == null) {
            this.listener?.onError(
                message = activity.getString(R.string.transaction_cancelled),
                errorCode = OneTimePaymentErrorCode.ERROR_CODE_BACK_PRESSED
            )
            this.paymentStatusResponse = null
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitiateUpiIntentWithJuspayEvent(initiateUpiIntentWithJuspay: InitiateUpiIntentWithJuspay) {
        if (hyperServices.isInitialised) {
            hyperServices.process(
                initiateUpiIntentWithJuspay.initiateUpiIntent.toJsonObject().toJSONObject()
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitiateGetSavedCardsWithJuspay(initiateGetSavedCardsWithJuspay: InitiateGetSavedCardsWithJuspay) {
        if (hyperServices.isInitialised) {
            hyperServices.process(
                initiateGetSavedCardsWithJuspay.getSavedCardsPayload.toJsonObject().toJSONObject()
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitiateGetAvailableUpiAppWithJuspay(initiateGetAvailableUpiAppWithJuspay: InitiateGetAvailableUpiAppWithJuspay) {
        if (hyperServices.isInitialised) {
            hyperServices.process(
                initiateGetAvailableUpiAppWithJuspay.getAvailableUpiApps.toJsonObject()
                    .toJSONObject()
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitiateNewCardPaymentWithJuspay(initiateNewCardPaymentWithJuspay: InitiateNewCardPaymentWithJuspay) {
        if (hyperServices.isInitialised) {
            hyperServices.process(
                initiateNewCardPaymentWithJuspay.initiateNewCardPaymentPayload.toJsonObject()
                    .toJSONObject()
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitiateGetCardInfoWithJuspay(initiateGetCardInfoWithJuspay: InitiateGetCardInfoWithJuspay) {
        if (hyperServices.isInitialised) {
            hyperServices.process(
                initiateGetCardInfoWithJuspay.getCardInfoPayload.toJsonObject().toJSONObject()
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitiateSavedCardPaymentWithJuspay(initiateSavedCardPaymentWithJuspay: InitiateSavedCardPaymentWithJuspay) {
        if (hyperServices.isInitialised) {
            hyperServices.process(
                initiateSavedCardPaymentWithJuspay.initiateSavedCardPaymentPayload.toJsonObject()
                    .toJSONObject()
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRetryManualPaymentEvent(retryManualPaymentEvent: RetryManualPaymentEvent) {
        // We have to reopen custom payment page bottom sheet in case of retry..
        // In case of full payment page fragment , it is opened automatically..
        if (
            paymentPageFragmentId != null
            && customUIPaymentPageDeepLink != null
            && listener != null
            && initiatePaymentResponse != null
            && isBottomSheetFlow.orFalse()
        ) {
            initiateWithCustomUI(
                customUiFragmentId = paymentPageFragmentId!!,
                fragmentDeepLink = customUIPaymentPageDeepLink!!,
                isBottomSheet = true,
                initiatePaymentResponse = initiatePaymentResponse!!,
                paymentListener = listener!!
            )
        }
    }

}