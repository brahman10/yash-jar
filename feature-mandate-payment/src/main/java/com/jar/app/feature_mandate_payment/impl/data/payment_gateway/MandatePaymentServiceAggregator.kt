package com.jar.app.feature_mandate_payment.impl.data.payment_gateway

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.getPhonePeVersionCode
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_mandate_payment.BuildConfig
import com.jar.app.feature_mandate_payment.R
import com.jar.app.feature_mandate_payment.impl.util.MandateErrorCodes
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp
import com.jar.app.feature_mandate_payments_common.shared.domain.mapper.toInitiateMandatePaymentApiRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.InitiateMandatePaymentUseCase
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentGateway
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.Lazy
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@ActivityScoped
internal class MandatePaymentServiceAggregator @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val activity: FragmentActivity,
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    private val appScope: CoroutineScope,
    private val analyticsApi: AnalyticsApi,
    private val dispatcherProvider: DispatcherProvider,
    private val initiateMandatePaymentUseCase: InitiateMandatePaymentUseCase
) : BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()!!
    }

    private val activityRef by lazy {
        WeakReference(activity)
    }

    private val paytmSdkAutopayPaymentGatewayService by lazy {
        PaytmSdkAutopayPaymentGatewayService(activityRef, serializer, analyticsApi)
    }

    private val paytmIntentAutopayPaymentGatewayService by lazy {
        PaytmIntentAutopayPaymentGatewayService(activityRef, analyticsApi)
    }

    private val phonePeAutopayPaymentGatewayService by lazy {
        PhonePeAutopayPaymentGatewayService(activityRef, analyticsApi)
    }

    private var initiateMandatePaymentJob: Job? = null

    fun getMandatePaymentGateway(): MandatePaymentGateway {
        return if (isPhonePeUpiRegistered())
            MandatePaymentGateway.PHONE_PE
        else
            MandatePaymentGateway.PAYTM
    }

    suspend fun initiateMandatePayment(
        paymentPageHeaderDetails: PaymentPageHeaderDetail,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ): Flow<RestClientResult<Pair<MandatePaymentResultFromSDK, FetchMandatePaymentStatusResponse>>> =
        callbackFlow {
            var mandatePaymentResultFromSDK: MandatePaymentResultFromSDK? = null
            val encodedPaymentPageHeader =
                encodeUrl(serializer.encodeToString(paymentPageHeaderDetails))
            val encodedMandateRequest =
                encodeUrl(serializer.encodeToString(initiateMandatePaymentRequest))
            val appSelectionFragmentId = R.id.paymentPageFragment
            val listener = object : AutopayPaymentGatewayService.OnAutoPayListener {
                override fun onResult(result: RestClientResult<MandatePaymentResultFromSDK>) {
                    if (result.status == RestClientResult.Status.SUCCESS) {
                        mandatePaymentResultFromSDK = result.data!!
                        appScope.launch(dispatcherProvider.main) {
                            // This delay is required as in paytm web flow it takes some time for webpage to dismiss & app to resume..
                            delay(500)
                            val encodedMandatePaymentResultFromSDK =
                                encodeUrl(serializer.encodeToString(result.data))
                            navController.navigate(
                                Uri.parse("android-app://com.jar.app/verifyMandatePaymentStatusFragment/$appSelectionFragmentId/$encodedPaymentPageHeader/$encodedMandatePaymentResultFromSDK"),
                                getNavOptions(shouldAnimate = true)
                            )
                        }
                    } else {
                        trySend(
                            RestClientResult.error(result.message!!, errorCode = result.errorCode)
                        )
                    }
                }
            }

            withContext(dispatcherProvider.main) {
                navController.navigate(
                    Uri.parse("android-app://com.jar.app/mandatePaymentPage/$encodedPaymentPageHeader/$encodedMandateRequest"),
                    getNavOptions(shouldAnimate = true)
                )

                navController.currentBackStackEntry?.savedStateHandle
                    ?.getLiveData<InitiateMandatePaymentApiResponse>(MandatePaymentCommonConstants.MANDATE_PAYMENT_RESPONSE_FROM_SDK)
                    ?.observe(activity) {
                        if (it.paytm != null) {
                            paytmSdkAutopayPaymentGatewayService.initiateMandatePayment(
                                it.packageName,
                                it,
                                listener
                            )
                        } else if (it.phonePe != null) {
                            phonePeAutopayPaymentGatewayService.initiateMandatePayment(
                                it.packageName,
                                it,
                                listener
                            )
                        } else if (it.paytmIntent != null) {
                            paytmIntentAutopayPaymentGatewayService.initiateMandatePayment(
                                it.packageName,
                                it,
                                listener
                            )
                        }
                    }

                navController.currentBackStackEntry?.savedStateHandle
                    ?.getLiveData<FetchMandatePaymentStatusResponse>(MandatePaymentCommonConstants.MANDATE_PAYMENT_STATUS_FROM_API)
                    ?.observe(activity) {
                        navController.popBackStack(appSelectionFragmentId, true)
                        val userLifeCycle = paymentPageHeaderDetails.userLifecycle
                            ?: prefs.getUserLifeCycleForMandate().orEmpty()
                        analyticsApi.postEvent(
                            MandatePaymentEventKey.Shown_AutopayCompleteScreen,
                            mapOf(
                                MandatePaymentEventKey.MandateAmount to initiateMandatePaymentRequest.mandateAmount,
                                MandatePaymentEventKey.AuthWorkflowType to initiateMandatePaymentRequest.authWorkflowType.name,
                                MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetails.featureFlow,
                                MandatePaymentEventKey.UserLifecycle to userLifeCycle,
                                MandatePaymentEventKey.UpiApp to it.provider.orEmpty(),
                                MandatePaymentEventKey.Status to it.getAutoInvestStatus().name
                            )
                        )
                        if (mandatePaymentResultFromSDK != null) {
                            trySend(
                                RestClientResult.success(
                                    Pair(
                                        mandatePaymentResultFromSDK!!,
                                        it
                                    )
                                )
                            )
                        }
                    }

                navController.currentBackStackEntry?.savedStateHandle
                    ?.getLiveData<Boolean>(MandatePaymentCommonConstants.BACK_PRESSED_FROM_PAYMENT_SCREEN)
                    ?.observe(activity) {
                        trySend(
                            RestClientResult.error(
                                message = "", //Passing empty as check will be based on error code..
                                errorCode = MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN
                            )
                        )
                    }
            }

            awaitClose {
                phonePeAutopayPaymentGatewayService.unregisterListener()
                paytmSdkAutopayPaymentGatewayService.unregisterListener()
                paytmIntentAutopayPaymentGatewayService.unregisterListener()
            }
        }

    suspend fun initiateMandatePaymentWithCustomUI(
        @IdRes customMandateUiFragmentId: Int,
        fragmentDeepLink: String,
        paymentPageHeaderDetails: PaymentPageHeaderDetail,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ): Flow<RestClientResult<Pair<MandatePaymentResultFromSDK, FetchMandatePaymentStatusResponse>>> =
        callbackFlow {
            var mandatePaymentResultFromSDK: MandatePaymentResultFromSDK? = null
            val encodedPaymentPageHeader =
                encodeUrl(serializer.encodeToString(paymentPageHeaderDetails))
            val listener = object : AutopayPaymentGatewayService.OnAutoPayListener {
                override fun onResult(result: RestClientResult<MandatePaymentResultFromSDK>) {
                    if (result.status == RestClientResult.Status.SUCCESS) {
                        mandatePaymentResultFromSDK = result.data!!
                        appScope.launch(dispatcherProvider.main) {
                            // This delay is required as in paytm web flow it takes some time for webpage to dismiss & app to resume..
                            delay(500)
                            val encodedMandatePaymentResultFromSDK =
                                encodeUrl(serializer.encodeToString(result.data))
                            navController.navigate(
                                Uri.parse("android-app://com.jar.app/verifyMandatePaymentStatusFragment/$customMandateUiFragmentId/$encodedPaymentPageHeader/$encodedMandatePaymentResultFromSDK"),
                                getNavOptions(shouldAnimate = true)
                            )
                        }
                    } else {
                        // As the mandate has failed, removing any data which was loaded from backstack for proper loading on next load
                        trySend(
                            RestClientResult.error(result.message!!, errorCode = result.errorCode)
                        )
                    }
                }
            }

            withContext(dispatcherProvider.main) {
                navController.navigate(
                    Uri.parse(fragmentDeepLink),
                    getNavOptions(shouldAnimate = true)
                )

                navController.getBackStackEntry(customMandateUiFragmentId).savedStateHandle
                    .getLiveData<InitiateMandatePaymentApiResponse?>(MandatePaymentCommonConstants.MANDATE_PAYMENT_RESPONSE_FROM_SDK)
                    .observe(activity) {
                        if (it?.paytm != null) {
                            paytmSdkAutopayPaymentGatewayService.initiateMandatePayment(
                                it.packageName,
                                it,
                                listener
                            )
                        } else if (it?.phonePe != null) {
                            phonePeAutopayPaymentGatewayService.initiateMandatePayment(
                                it.packageName,
                                it,
                                listener
                            )
                        } else if (it?.paytmIntent != null) {
                            paytmIntentAutopayPaymentGatewayService.initiateMandatePayment(
                                it.packageName,
                                it,
                                listener
                            )
                        }
                    }

                navController.getBackStackEntry(customMandateUiFragmentId).savedStateHandle
                    .getLiveData<FetchMandatePaymentStatusResponse>(MandatePaymentCommonConstants.MANDATE_PAYMENT_STATUS_FROM_API)
                    .observe(activity) {
                        val userLifeCycle = paymentPageHeaderDetails.userLifecycle
                            ?: prefs.getUserLifeCycleForMandate().orEmpty()
                        analyticsApi.postEvent(
                            MandatePaymentEventKey.Shown_AutopayCompleteScreen,
                            mapOf(
                                MandatePaymentEventKey.MandateAmount to initiateMandatePaymentRequest.mandateAmount,
                                MandatePaymentEventKey.AuthWorkflowType to initiateMandatePaymentRequest.authWorkflowType.name,
                                MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetails.featureFlow,
                                MandatePaymentEventKey.UserLifecycle to userLifeCycle,
                                MandatePaymentEventKey.Status to it.getAutoInvestStatus().name
                            )
                        )
                        if (mandatePaymentResultFromSDK != null) {
                            trySend(
                                RestClientResult.success(
                                    Pair(
                                        mandatePaymentResultFromSDK!!,
                                        it
                                    )
                                )
                            )
                        }
                    }

                navController.getBackStackEntry(customMandateUiFragmentId).savedStateHandle
                    .getLiveData<Boolean>(MandatePaymentCommonConstants.BACK_PRESSED_FROM_PAYMENT_SCREEN)
                    .observe(activity) {
                        trySend(
                            RestClientResult.error(
                                message = "", //Passing empty as check will be based on error code..
                                errorCode = MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN
                            )
                        )
                    }
            }

            awaitClose {
                phonePeAutopayPaymentGatewayService.unregisterListener()
                paytmSdkAutopayPaymentGatewayService.unregisterListener()
                paytmIntentAutopayPaymentGatewayService.unregisterListener()
            }
        }


    suspend fun initiateMandatePaymentWithUpiApp(
        @IdRes initiateMandateFragmentId: Int,
        paymentPageHeaderDetails: PaymentPageHeaderDetail,
        upiApp: UpiApp,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ): Flow<RestClientResult<Pair<MandatePaymentResultFromSDK, FetchMandatePaymentStatusResponse>>> =
        callbackFlow {
            var mandatePaymentResultFromSDK: MandatePaymentResultFromSDK? = null
            val encodedPaymentPageHeader =
                encodeUrl(serializer.encodeToString(paymentPageHeaderDetails))

            val listener = object : AutopayPaymentGatewayService.OnAutoPayListener {
                override fun onResult(result: RestClientResult<MandatePaymentResultFromSDK>) {
                    if (result.status == RestClientResult.Status.SUCCESS) {
                        mandatePaymentResultFromSDK = result.data!!
                        appScope.launch(dispatcherProvider.main) {
                            // This delay is required as in paytm web flow it takes some time for webpage to dismiss & app to resume..
                            delay(500)
                            val encodedMandatePaymentResultFromSDK =
                                encodeUrl(serializer.encodeToString(result.data))
                            navController.navigate(
                                Uri.parse("android-app://com.jar.app/verifyMandatePaymentStatusFragment/$initiateMandateFragmentId/$encodedPaymentPageHeader/$encodedMandatePaymentResultFromSDK"),
                                getNavOptions(shouldAnimate = true)
                            )
                        }
                    } else {
                        // As the mandate has failed, removing any data which was loaded from backstack for proper loading on next load
                        trySend(
                            RestClientResult.error(result.message!!, errorCode = result.errorCode)
                        )
                    }
                }
            }

            initiateMandatePaymentJob?.cancel()
            initiateMandatePaymentJob = appScope.launch(dispatcherProvider.main) {
                initiateMandatePaymentUseCase.initiateMandatePayment(
                    initiateMandatePaymentRequest.toInitiateMandatePaymentApiRequest(
                        mandatePaymentGateway = getMandatePaymentGateway(),
                        packageName = upiApp.packageName,
                        phonePeVersionCode = activity.getPhonePeVersionCode("com.phonepe.app")?.toString()
                    )
                ).collect(
                    onLoading = {
                        trySend(RestClientResult.loading())
                    },
                    onSuccess = {
                        it?.packageName = upiApp.packageName
                        if (it?.paytm != null) {
                            paytmSdkAutopayPaymentGatewayService.initiateMandatePayment(
                                it.packageName,
                                it,
                                listener
                            )
                        } else if (it?.phonePe != null) {
                            phonePeAutopayPaymentGatewayService.initiateMandatePayment(
                                it.packageName,
                                it,
                                listener
                            )
                        } else if (it?.paytmIntent != null) {
                            paytmIntentAutopayPaymentGatewayService.initiateMandatePayment(
                                it.packageName,
                                it,
                                listener
                            )
                        }
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

                navController.getBackStackEntry(initiateMandateFragmentId).savedStateHandle
                    .getLiveData<FetchMandatePaymentStatusResponse>(MandatePaymentCommonConstants.MANDATE_PAYMENT_STATUS_FROM_API)
                    .observe(activity) {
                        val userLifeCycle = paymentPageHeaderDetails.userLifecycle
                            ?: prefs.getUserLifeCycleForMandate().orEmpty()
                        analyticsApi.postEvent(
                            MandatePaymentEventKey.Shown_AutopayCompleteScreen,
                            mapOf(
                                MandatePaymentEventKey.MandateAmount to initiateMandatePaymentRequest.mandateAmount,
                                MandatePaymentEventKey.AuthWorkflowType to initiateMandatePaymentRequest.authWorkflowType.name,
                                MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetails.featureFlow,
                                MandatePaymentEventKey.UserLifecycle to userLifeCycle,
                                MandatePaymentEventKey.Status to it.getAutoInvestStatus().name
                            )
                        )
                        if (mandatePaymentResultFromSDK != null) {
                            trySend(
                                RestClientResult.success(
                                    Pair(
                                        mandatePaymentResultFromSDK!!,
                                        it
                                    )
                                )
                            )
                        }
                    }
            }

            awaitClose {
                phonePeAutopayPaymentGatewayService.unregisterListener()
                paytmSdkAutopayPaymentGatewayService.unregisterListener()
                paytmIntentAutopayPaymentGatewayService.unregisterListener()
            }
        }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        phonePeAutopayPaymentGatewayService.onActivityResult(requestCode, resultCode, data)
        paytmSdkAutopayPaymentGatewayService.onActivityResult(requestCode, resultCode, data)
        paytmIntentAutopayPaymentGatewayService.onActivityResult(requestCode, resultCode, data)
    }

    fun teardown() {
        phonePeAutopayPaymentGatewayService.unregisterListener()
        paytmSdkAutopayPaymentGatewayService.unregisterListener()
        paytmIntentAutopayPaymentGatewayService.unregisterListener()
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun isPhonePeUpiRegistered(): Boolean {
        val uri = Uri.parse(String.format("%s://%s", "upi", "mandate"))
        val upiUriIntent = Intent()
        upiUriIntent.data = uri
        val resolveInfoList =
            activity.applicationContext.packageManager.queryIntentActivities(
                upiUriIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            if (
                packageName.isNullOrBlank().not() &&
                BuildConfig.PHONEPE_PACKAGE.matches(packageName.toRegex())
            ) {
                return true
            }
        }
        return false
    }

}