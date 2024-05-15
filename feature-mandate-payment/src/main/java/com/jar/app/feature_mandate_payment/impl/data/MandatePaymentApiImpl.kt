package com.jar.app.feature_mandate_payment.impl.data

import android.content.Intent
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.getAppIconFromPkgName
import com.jar.app.base.util.getAppNameFromPkgName
import com.jar.app.base.util.isPackageInstalled
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.data.payment_gateway.MandatePaymentServiceAggregator
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.PaymentMethodUpiIntent
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchRecentlyUsedPaymentMethodUseCase
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentGateway
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class MandatePaymentApiImpl @Inject constructor(
    private val appScope: CoroutineScope,
    private val activity: FragmentActivity,
    private var mandatePaymentServiceAggregator: MandatePaymentServiceAggregator,
    private val fetchRecentlyUsedPaymentMethodUseCase: FetchRecentlyUsedPaymentMethodUseCase
) : MandatePaymentApi, BaseNavigation {

    private var recentUpiAppJob: Job? = null

    private val packageManager = activity.applicationContext.packageManager

    override suspend fun initiateMandatePayment(
        paymentPageHeaderDetails: PaymentPageHeaderDetail,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ): Flow<RestClientResult<Pair<MandatePaymentResultFromSDK, FetchMandatePaymentStatusResponse>>> =
        mandatePaymentServiceAggregator.initiateMandatePayment(
            paymentPageHeaderDetails,
            initiateMandatePaymentRequest
        )

    override suspend fun initiateMandatePaymentWithCustomUI(
        @IdRes customMandateUiFragmentId: Int,
        fragmentDeepLink: String,
        paymentPageHeaderDetails: PaymentPageHeaderDetail,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ): Flow<RestClientResult<Pair<MandatePaymentResultFromSDK, FetchMandatePaymentStatusResponse>>> =
        mandatePaymentServiceAggregator.initiateMandatePaymentWithCustomUI(
            customMandateUiFragmentId,
            fragmentDeepLink,
            paymentPageHeaderDetails,
            initiateMandatePaymentRequest
        )

    override suspend fun initiateMandatePaymentWithUpiApp(
        @IdRes initiateMandateFragmentId: Int,
        paymentPageHeaderDetails: PaymentPageHeaderDetail,
        upiApp: UpiApp,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ): Flow<RestClientResult<Pair<MandatePaymentResultFromSDK, FetchMandatePaymentStatusResponse>>> =
        mandatePaymentServiceAggregator.initiateMandatePaymentWithUpiApp(
            initiateMandateFragmentId,
            paymentPageHeaderDetails,
            upiApp,
            initiateMandatePaymentRequest
        )

    override suspend fun fetchLastUsedUpiApp(flowType: String?): Flow<RestClientResult<UpiApp?>> = callbackFlow {
        recentUpiAppJob?.cancel()
        recentUpiAppJob = appScope.launch {
            fetchRecentlyUsedPaymentMethodUseCase.fetchRecentlyUsedPaymentMethods(
                isPackageInstalled = {
                    activity.applicationContext.isPackageInstalled(it)
                },
                flowType = flowType
            ).collectUnwrapped(
                onLoading = {
                    trySend(RestClientResult.loading())
                },
                onSuccess = {
                    it?.let {
                        val paymentMethodUpiIntent =
                            it.filterIsInstance<PaymentMethodUpiIntent>().firstOrNull()

                        if (paymentMethodUpiIntent != null) {
                            val upiAppPackageName = paymentMethodUpiIntent.payerApp
                            val upiApp = UpiApp(
                                packageName = upiAppPackageName,
                                icon = upiAppPackageName.getAppIconFromPkgName(packageManager),
                                appName = upiAppPackageName.getAppNameFromPkgName(
                                    packageManager
                                ).orEmpty()
                            )
                            trySend(RestClientResult.success(upiApp))
                        } else {
                            trySend(RestClientResult.success(null))
                        }
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
            recentUpiAppJob?.cancel()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mandatePaymentServiceAggregator.onActivityResult(requestCode, resultCode, data)
    }

    override fun teardown() {
        recentUpiAppJob?.cancel()
        mandatePaymentServiceAggregator.teardown()
    }

    override fun getMandatePaymentGateway(): MandatePaymentGateway {
        return mandatePaymentServiceAggregator.getMandatePaymentGateway()
    }

}