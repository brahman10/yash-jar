package com.jar.app.feature_payment.impl

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.jar.app.base.util.getAppNameFromPkgName
import com.jar.app.base.util.isPackageInstalled
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.core_preferences.api.RetainedPrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.OneTimePaymentResult
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethodUpiIntent
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchRecentlyUsedPaymentMethodsUseCase
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_payment.BuildConfig
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_payment.impl.data.base.PaymentGatewayService
import com.jar.app.feature_payment.impl.data.juspay.JuspayPaymentGatewayService
import com.jar.app.feature_payment.impl.data.paytm.PaytmPaymentGatewayService
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.Lazy
import `in`.juspay.services.HyperServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.properties.Delegates

internal class PaymentManagerImpl @Inject constructor(
    private val activity: FragmentActivity,
    private val serializer: Serializer,
    private val remoteConfigApi: RemoteConfigApi,
    private val navControllerRef: Lazy<NavController>,
    private val hyperServices: HyperServices,
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase,
    private val fetchRecentlyUsedPaymentMethodsUseCase: FetchRecentlyUsedPaymentMethodsUseCase,
    private val appScope: CoroutineScope,
    private val retainedPrefs: RetainedPrefsApi
) : PaymentManager {

    private var fetchPaymentStatusJob: Job? = null

    private var fetchRecentlyUsedPaymentMethodJob: Job? = null

    private var packageManager = activity.applicationContext.packageManager

    private lateinit var paymentGatewayService: PaymentGatewayService

    private var paymentGateway: OneTimePaymentGateway? by Delegates.observable(null) { _, _, newValue ->
        this.paymentGatewayService = getPaymentGatewayService(newValue!!)
    }

    override fun getDefaultPaymentGateway(): OneTimePaymentGateway {
        return OneTimePaymentGateway.valueOf(remoteConfigApi.getPaymentGateway())
    }

    override fun getCurrentPaymentGateway() = paymentGateway ?: getDefaultPaymentGateway()

    private val paytmPaymentGatewayService by lazy {
        PaytmPaymentGatewayService(
            WeakReference(activity),
            serializer,
            fetchManualPaymentStatusUseCase
        )
    }

    private val juspayPaymentGatewayService by lazy {
        JuspayPaymentGatewayService(
            WeakReference(activity),
            WeakReference(navControllerRef.get()),
            hyperServices,
            serializer
        )
    }

    private fun getPaymentGatewayService(paymentGateway: OneTimePaymentGateway): PaymentGatewayService {
        return when (paymentGateway) {
            OneTimePaymentGateway.PAYTM -> {
                paytmPaymentGatewayService
            }

            OneTimePaymentGateway.JUSPAY -> {
                juspayPaymentGatewayService
            }
        }
    }

    override fun init(userId: String) {
        paytmPaymentGatewayService.init(userId)
        juspayPaymentGatewayService.init(userId)
    }

    override fun initiate(
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentListener: PaymentManager.OnPaymentResultListener,
        paymentGateway: OneTimePaymentGateway
    ) {
        this.paymentGateway = paymentGateway
        if (retainedPrefs.getIsAutomationEnabled()) {
            fetchPaymentStatusJob?.cancel()
            fetchPaymentStatusJob = appScope.launch {
                fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                    FetchManualPaymentRequest(
                        paymentProvider = initiatePaymentResponse.getPaymentProvider().name,
                        orderId = initiatePaymentResponse.orderId,
                        juspay = null,
                        paytm = null
                    )
                ).collect(
                    onLoading = {
                        paymentListener.onLoading(true)
                    }, onSuccess = {
                        paymentListener.onSuccess(it)
                    }, onError = { errorMessage, _ ->
                        paymentListener.onError(errorMessage)
                    })
            }
        } else {
            paymentGatewayService.initiate(initiatePaymentResponse, paymentListener)
        }
    }

    override suspend fun initiateOneTimePayment(
        initiatePaymentResponse: InitiatePaymentResponse,
        paymentGateway: OneTimePaymentGateway
    ): Flow<RestClientResult<FetchManualPaymentStatusResponse>> = callbackFlow {
        this@PaymentManagerImpl.paymentGateway = paymentGateway
        if (retainedPrefs.getIsAutomationEnabled()) {
            fetchPaymentStatusJob?.cancel()
            fetchPaymentStatusJob = appScope.launch {
                fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                    FetchManualPaymentRequest(
                        paymentProvider = initiatePaymentResponse.getPaymentProvider().name,
                        orderId = initiatePaymentResponse.orderId,
                        juspay = null,
                        paytm = null
                    )
                ).collect(
                    onLoading = {
                        trySend(RestClientResult.loading())
                    }, onSuccess = {
                        trySend(RestClientResult.success(it))
                    }, onError = { errorMessage, _ ->
                        trySend(RestClientResult.error(errorMessage))
                    })
            }
        } else {
            val listener = object : PaymentManager.OnPaymentResultListener {
                override fun onLoading(showLoading: Boolean) {
                    if (showLoading)
                        trySend(RestClientResult.loading())
                }

                override fun onSuccess(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
                    trySend(RestClientResult.success(fetchManualPaymentStatusResponse))
                }

                override fun onError(
                    message: String?,
                    errorCode: String?,
                    oneTimePaymentResult: OneTimePaymentResult?,
                    shouldFetchPaymentStatus: Boolean
                ) {
                    trySend(
                        RestClientResult.error(
                            message.orEmpty(),
                            errorCode = errorCode
                        )
                    )
                }
            }
            paymentGatewayService.initiate(initiatePaymentResponse, listener)
        }
        awaitClose {
            paymentGatewayService.unregisterListener()
        }
    }

    override suspend fun initiateOneTimePaymentWithCustomUI(
        customUiFragmentId: Int,
        fragmentDeepLink: String,
        isBottomSheet: Boolean,
        initiatePaymentResponse: InitiatePaymentResponse
    ): Flow<RestClientResult<FetchManualPaymentStatusResponse>> = callbackFlow {
        this@PaymentManagerImpl.paymentGateway =
            OneTimePaymentGateway.JUSPAY // This flow only supports JUSPAY
        if (retainedPrefs.getIsAutomationEnabled()) {
            fetchPaymentStatusJob?.cancel()
            fetchPaymentStatusJob = appScope.launch {
                fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                    FetchManualPaymentRequest(
                        paymentProvider = initiatePaymentResponse.getPaymentProvider().name,
                        orderId = initiatePaymentResponse.orderId,
                        juspay = null,
                        paytm = null
                    )
                ).collect(
                    onLoading = {
                        trySend(RestClientResult.loading())
                    }, onSuccess = {
                        trySend(RestClientResult.success(it))
                    }, onError = { errorMessage, _ ->
                        trySend(RestClientResult.error(errorMessage))
                    })
            }
        } else {
            val listener = object : PaymentManager.OnPaymentResultListener {
                override fun onLoading(showLoading: Boolean) {
                    if (showLoading)
                        trySend(RestClientResult.loading())
                }

                override fun onSuccess(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
                    trySend(RestClientResult.success(fetchManualPaymentStatusResponse))
                }

                override fun onError(
                    message: String?,
                    errorCode: String?,
                    oneTimePaymentResult: OneTimePaymentResult?,
                    shouldFetchPaymentStatus: Boolean
                ) {
                    trySend(
                        RestClientResult.error(
                            message.orEmpty(),
                            errorCode = errorCode
                        )
                    )
                }
            }
            paymentGatewayService.initiateWithCustomUI(
                customUiFragmentId = customUiFragmentId,
                fragmentDeepLink = fragmentDeepLink,
                isBottomSheet = isBottomSheet,
                initiatePaymentResponse = initiatePaymentResponse,
                paymentListener = listener
            )
        }
        awaitClose {
            paymentGatewayService.unregisterListener()
        }
    }

    override suspend fun fetchLastUsedUpiApp(flowContext: String?): Flow<RestClientResult<UpiApp?>> = callbackFlow {
        fetchRecentlyUsedPaymentMethodJob?.cancel()
        fetchRecentlyUsedPaymentMethodJob = appScope.launch {
            fetchRecentlyUsedPaymentMethodsUseCase.fetchRecentlyUsedPaymentMethods(
                isPackageInstalled = {
                    activity.isPackageInstalled(it)
                },
                flowContext = flowContext
            ).collectUnwrapped(
                onLoading = {
                    trySend(RestClientResult.loading())
                },
                onSuccess = {
                    val paymentMethodUpiIntent =
                        it.filterIsInstance<PaymentMethodUpiIntent>().firstOrNull()
                    if (paymentMethodUpiIntent != null) {
                        trySend(
                            RestClientResult.success(
                                UpiApp(
                                    packageName = paymentMethodUpiIntent.payerApp,
                                    appName = paymentMethodUpiIntent.payerApp.getAppNameFromPkgName(
                                        packageManager
                                    ).orEmpty()
                                )
                            )
                        )
                    } else {
                        trySend(
                            RestClientResult.success(null)
                        )
                    }
                },
                onSuccessWithNullData = {
                    trySend(RestClientResult.success(null))
                },
                onError = { errorMessage, errorCode ->
                    trySend(
                        RestClientResult.error(
                            message = errorMessage,
                            errorCode = errorCode
                        )
                    )
                }
            )
        }

        awaitClose {
            fetchRecentlyUsedPaymentMethodJob?.cancel()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override suspend fun fetchInstalledUpiApps(): List<UpiApp> {
        val uri = Uri.parse(String.format("%s://%s", "upi", "pay"))
        val upiUriIntent = Intent()
        upiUriIntent.data = uri
        val resolveInfoList =
                packageManager.queryIntentActivities(
                upiUriIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        val upiAppsList: ArrayList<UpiApp> = ArrayList()
        resolveInfoList.forEach {
            upiAppsList.add(
                UpiApp(
                    packageName = it.activityInfo.packageName,
                    appName = it.activityInfo.packageName.getAppNameFromPkgName(packageManager) ?: it.activityInfo.packageName
                )
            )
        }
        return upiAppsList
    }

    override suspend fun initiatePaymentWithUpiApp(
        initiatePageFragmentId: Int,
        initiatePaymentResponse: InitiatePaymentResponse,
        upiApp: UpiApp
    ): Flow<RestClientResult<FetchManualPaymentStatusResponse>> = callbackFlow {
        this@PaymentManagerImpl.paymentGateway =
            OneTimePaymentGateway.JUSPAY // This flow only supports JUSPAY
        if (retainedPrefs.getIsAutomationEnabled()) {
            fetchPaymentStatusJob?.cancel()
            fetchPaymentStatusJob = appScope.launch {
                fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                    FetchManualPaymentRequest(
                        paymentProvider = initiatePaymentResponse.getPaymentProvider().name,
                        orderId = initiatePaymentResponse.orderId,
                        juspay = null,
                        paytm = null
                    )
                ).collect(
                    onLoading = {
                        trySend(RestClientResult.loading())
                    }, onSuccess = {
                        trySend(RestClientResult.success(it))
                    }, onError = { errorMessage, _ ->
                        trySend(RestClientResult.error(errorMessage))
                    })
            }
        } else {
            val listener = object : PaymentManager.OnPaymentResultListener {
                override fun onLoading(showLoading: Boolean) {
                    if (showLoading)
                        trySend(RestClientResult.loading())
                }

                override fun onSuccess(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
                    trySend(RestClientResult.success(fetchManualPaymentStatusResponse))
                }

                override fun onError(
                    message: String?,
                    errorCode: String?,
                    oneTimePaymentResult: OneTimePaymentResult?,
                    shouldFetchPaymentStatus: Boolean
                ) {
                    trySend(
                        RestClientResult.error(
                            message.orEmpty(),
                            errorCode = errorCode
                        )
                    )
                }
            }
            paymentGatewayService.initiatePaymentWithUpiApp(
                initiatePaymentFragmentId = initiatePageFragmentId,
                upiApp = upiApp,
                initiatePaymentResponse = initiatePaymentResponse,
                paymentListener = listener
            )
        }

        awaitClose {
            paymentGatewayService.unregisterListener()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (::paymentGatewayService.isInitialized)
            paymentGatewayService.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (::paymentGatewayService.isInitialized)
            paymentGatewayService.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPress(): Boolean {
        return if (::paymentGatewayService.isInitialized)
            paymentGatewayService.onBackPress()
        else false
    }

    override fun tearDown() {
        if (::paymentGatewayService.isInitialized) {
            paymentGatewayService.tearDown()
        }
        fetchPaymentStatusJob?.cancel()
        fetchRecentlyUsedPaymentMethodJob?.cancel()
    }

}