package com.jar.app.feature_round_off.impl

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_round_off.api.RoundOffApi
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import javax.inject.Inject

internal class RoundOffApiImpl @Inject constructor(
    private val activity: FragmentActivity,
    private val navControllerRef: Lazy<NavController>,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val appScope: CoroutineScope,
    private val serializer: Serializer
) : RoundOffApi, BaseNavigation {

    private var openRoundOff: Job? = null

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openRoundOffFragment() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/roundOff"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openRoundOffFlow() {
        openRoundOff?.cancel()
        openRoundOff = appScope.launch(dispatcherProvider.main) {
            if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect(
                    onSuccess = {
                        if (it.enabled.orFalse()) {
                            if (it.subscriptionStatus == null)
                            //Here sending success as a status to SetupRoundOffFragment means that the flow has not been initiated yet
                                openRoundOffDetails()
                            else if (it.subscriptionStatus != MandatePaymentProgressStatus.SUCCESS.name) {
                                val data =
                                    it.subscriptionStatus
                                        ?: MandatePaymentProgressStatus.SUCCESS.name
                                navController.navigate(
                                    Uri.parse(
                                        "android-app://com.jar.app/setupRoundOff/${
                                            encodeUrl(serializer.encodeToString(data))
                                        }"
                                    ),
                                    getNavOptions(true)
                                )
                            } else
                                openRoundOffDetails()
                        }else{
                            navController.navigate(
                                Uri.parse(
                                    "android-app://com.jar.app/setupRoundOff/${
                                        encodeUrl(serializer.encodeToString(MandatePaymentProgressStatus.SUCCESS))
                                    }"
                                ),
                                getNavOptions(true)
                            )
                        }
                    }
                )
            }
        }
    }

    override fun openRoundOffDetails(fromScreen: String?) {
        val currentTime = System.currentTimeMillis()
        navController.navigate(
            Uri.parse("android-app://com.jar.app/roundOffDetails/$currentTime/$fromScreen"),
            getNavOptions(true)
        )
    }

    override fun openRoundOffForAutoPaySetup(shouldRedirectToPreRoundOffAutopayScreen: Boolean, fromScreen: String) {
        navController.navigate(
            Uri.parse(if (shouldRedirectToPreRoundOffAutopayScreen) "android-app://com.jar.app/preRoundOffAutopaySetup" else "android-app://com.jar.app/roundOffExplanation/${false}/${System.currentTimeMillis()}/${fromScreen}"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openRoundOffPostPaymentStatusScreens(
        mandatePaymentResultFromSDK: MandatePaymentResultFromSDK,
        mandatePaymentStatusResponse: FetchMandatePaymentStatusResponse
    ) {
        val encodedMandateStatusResponse = encodeUrl(serializer.encodeToString(mandatePaymentStatusResponse))
        val navigation =
            if (mandatePaymentStatusResponse.getAutoInvestStatus() == com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS) {
                Uri.parse("android-app://com.jar.app/roundOffAutoPaySuccess/$encodedMandateStatusResponse")
            } else {
                val encodedMandateSdkResult = encodeUrl(serializer.encodeToString(mandatePaymentResultFromSDK))
                Uri.parse("android-app://com.jar.app/com.jar.app/roundOffAutoPayPendingOrFailure/$encodedMandateSdkResult/$encodedMandateStatusResponse")
            }
        navController.navigate(navigation, getNavOptions(shouldAnimate = true))
    }
}