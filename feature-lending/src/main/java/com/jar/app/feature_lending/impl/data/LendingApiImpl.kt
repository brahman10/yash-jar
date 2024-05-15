package com.jar.app.feature_lending.impl.data

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_lending.api.LendingApi
import com.jar.app.feature_lending.shared.domain.model.LendingFlowStatusResponse
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationStatusV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchRealTimeLeadStatusUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LendingApiImpl @Inject constructor(
    private val fetchRealTimeLeadStatusUseCase: FetchRealTimeLeadStatusUseCase,
    private val navControllerRef: Lazy<NavController>,
    private val appScope: CoroutineScope,
    private val dispatcher: DispatcherProvider,
    private val activity: FragmentActivity,
) : LendingApi, BaseNavigation {

    private var job: Job? = null
    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openLendingFlowV2(flowType: String, apiCallback: (String?, Boolean) -> Unit) {
        navigateToHostFragment(flowType)
    }

    override fun openRealTimeLendingFlow(flowType: String, apiStateCallback: (String?,Boolean) -> Unit) {
        job?.cancel()
        job = appScope.launch{
            fetchRealTimeLeadStatusUseCase.fetchRealTimeLeadStatus().collect(
                onLoading = {
                    withContext(dispatcher.main) {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback.invoke(null,true)
                        }
                    }
                },
                onSuccess = {
                    withContext(dispatcher.main) {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback.invoke(null,false)
                            val uri = when (it?.status) {
                                LendingConstants.RealTimeLeadStatus.BANK_DETAILS_SUBMITTED ->
                                    "android-app://com.jar.app/realTimeReadyCashLandingFragment/$flowType"
                                LendingConstants.RealTimeLeadStatus.BANK_STATEMENT_UPLOADED ->
                                    "android-app://com.jar.app/findingBestOfferFragment"
                                null->"android-app://com.jar.app/realTimeReadyCashLandingFragment/$flowType"
                                else -> "android-app://com.jar.app/realTimeReadyCashLandingFragment/$flowType"
                            }
                            navController.navigate(
                                Uri.parse(uri),
                                getNavOptions(shouldAnimate = true)
                            )
                        }
                    }
                },
                onSuccessWithNullData = {
                    withContext(dispatcher.main) {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback.invoke(null,false)
                            navController.navigate(
                                Uri.parse( "android-app://com.jar.app/realTimeReadyCashLandingFragment/$flowType"),
                                getNavOptions(shouldAnimate = true)
                            )
                        }
                    }
                },
                onError = { message, errorCode ->
                    withContext(dispatcher.main) {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback.invoke(message,false)
                        }
                    }
                }
            )
        }
    }

    override fun openRealTimeFindingBestOfferScreen(flowType: String) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/findingBestOfferFragment"),
            getNavOptions(shouldAnimate = true)
        )
    }

    private fun navigateToHostFragment(flowType: String) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/lendingHostFragment/$flowType"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openLendingRepeatWithdrawal(
        flowType: String
    ) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/lendingRepeatWithdrawal/$flowType"),
            getNavOptions(shouldAnimate = true)
        )
    }

    private fun navigateToIntroFragment(flowType: String) {
        if (flowType == BaseConstants.LendingFlowType.SELL_GOLD) {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/sellGoldLandingFragment/$flowType"),
                getNavOptions(shouldAnimate = true)
            )
        } else {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/readyCashLandingFragment/$flowType"),
                getNavOptions(shouldAnimate = true)
            )
        }
    }

    private fun navigateV2(
        loanId: String,
        flowType: String,
        lendingFlowStatusResponse: LendingFlowStatusResponse,
        isRepeatWithdrawal: Boolean = false
    ) {
        if (flowType == BaseConstants.LendingFlowType.SELL_GOLD) {
            navigateToIntroFragment(flowType)
        } else if (isRepeatWithdrawal) {
            openLendingRepeatWithdrawal(flowType)
        } else {
            val uriString = when {
                lendingFlowStatusResponse.status == LoanApplicationStatusV2.DISBURSED.name -> {
                    "android-app://com.jar.app/lendingRepeatWithdrawal/$flowType"
                }

                lendingFlowStatusResponse.status == LoanApplicationStatusV2.FORECLOSED.name -> {
                    "android-app://com.jar.app/lendingRepeatWithdrawal/$flowType"
                }

                lendingFlowStatusResponse.status == LoanApplicationStatusV2.CLOSED.name -> {
                    "android-app://com.jar.app/lendingRepeatWithdrawal/$flowType"
                }

                lendingFlowStatusResponse.checkpoints.employment == LoanStatus.VERIFIED.name &&
                        lendingFlowStatusResponse.checkpoints.pan == LoanStatus.VERIFIED.name -> {
                    "android-app://com.jar.app/lendingHostFragment"
                }

                else -> "android-app://com.jar.app/readyCashLandingFragment/$flowType"
            }
            navController.navigate(
                Uri.parse(uriString),
                getNavOptions(shouldAnimate = true)
            )
        }
    }

    override fun openCheckCreditReport(flowType: String) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/checkCreditScoreFragment/$flowType"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openEmiCalculatorLaunchingSoonScreen() {
        navController.navigate(
            Uri.parse(BaseConstants.InternalDeepLinks.EMI_CALCULATOR),
            getNavOptions(shouldAnimate = true)
        )
    }

}