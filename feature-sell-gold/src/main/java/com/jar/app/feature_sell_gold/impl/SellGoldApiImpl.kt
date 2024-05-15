package com.jar.app.feature_sell_gold.impl

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_sell_gold.api.SellGoldApi
import com.jar.app.feature_sell_gold.impl.ui.model.VpaSelectionArgument
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalBottomSheetDataUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class SellGoldApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val fetchWithdrawalBottomSheetDataUseCase: IFetchWithdrawalBottomSheetDataUseCase,
    private val appScope: CoroutineScope,
    private val dispatcher: DispatcherProvider,
    private val serializer: Serializer,
    private val activity: FragmentActivity,
) : SellGoldApi, BaseNavigation {

    private var job: Job? = null

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openSellGoldFlow() {
        job?.cancel()
        job = appScope.launch {
            fetchWithdrawalBottomSheetDataUseCase.fetchWithdrawBottomSheetData().collect(
                onSuccess = { withdrawHelpData ->
                    withdrawHelpData ?: return@collect
                    withContext(dispatcher.main) {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            when {
                                with(withdrawHelpData.quickActionWithdraw) {
                                    !title.isNullOrBlank() || withdrawalLimitBottomSheet != null
                                } -> openWithdrawBottomSheet()

                                else -> navController.navigate(
                                    Uri.parse(BaseConstants.InternalDeepLinks.SELL_GOLD_REVAMP),
                                    getNavOptions(shouldAnimate = true)
                                )
                            }
                        }
                    }
                }
            )
        }
    }


    override fun openWithdrawBottomSheet() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/withdrawalHelpBottomSheet"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openVpaSelectionFragment(
        isRetryFlow: Boolean,
        withdrawalPrice: String?,
        orderId: String?,
        popUpTo: Int?
    ) {
        val vpaSelectionArgs =
            VpaSelectionArgument(isRetryFlow, withdrawalPrice.orEmpty(), orderId.orEmpty())
        navController.navigate(
            Uri.parse(
                "${BaseConstants.InternalDeepLinks.SELL_GOLD_REVAMP}/upiSelectionFragment/${
                    encodeUrl(serializer.encodeToString(vpaSelectionArgs))
                }"
            ),
            navOptions = if (popUpTo != null) {
                getNavOptions(shouldAnimate = true, popUpToId = popUpTo, inclusive = true)
            } else {
                getNavOptions(shouldAnimate = true)
            }
        )
    }
}