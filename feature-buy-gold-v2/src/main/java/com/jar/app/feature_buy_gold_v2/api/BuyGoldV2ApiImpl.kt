package com.jar.app.feature_buy_gold_v2.api

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldInputData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.OrderStatusModel
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchBuyGoldInfoUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class BuyGoldV2ApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val appScope: CoroutineScope,
    private val coreUiApi: CoreUiApi,
    private val serializer: Serializer,
    private val dispatcherProvider: DispatcherProvider,
    private val fetchBuyGoldInfoUseCase: FetchBuyGoldInfoUseCase
) : BaseNavigation, BuyGoldV2Api {

    private val navController by lazy {
        navControllerRef.get()
    }

    private var job: Job? = null

    override fun openBuyGoldFlow(buyGoldFlowContext: String) {
        val encoded = encodeUrl(serializer.encodeToString(BuyGoldInputData()))
        val currentTime = System.currentTimeMillis()
        navController.navigate(
            Uri.parse("${BaseConstants.InternalDeepLinks.BUY_GOLD_V2}/$encoded/${buyGoldFlowContext}/$currentTime"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openBuyGoldFlowWithCoupon(
        couponCode: String,
        couponType: String,
        isFromJackpotScreen: Boolean,
        buyGoldFlowContext: String
    ) {
        val encoded = encodeUrl(
            serializer.encodeToString(
                BuyGoldInputData(
                    couponCode = encodeUrl(couponCode),
                    couponType = couponType,
                    isFromJackpotScreen = isFromJackpotScreen
                )
            )
        )
        val currentTime = System.currentTimeMillis()
        navController.navigate(
            Uri.parse(
                "${BaseConstants.InternalDeepLinks.BUY_GOLD_V2}/$encoded/${buyGoldFlowContext}/$currentTime"
            ),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openBuyGoldFlowWithWeeklyChallengeAmount(
        amount: Float,
        buyGoldFlowContext: String
    ) {
        val encoded = encodeUrl(
            serializer.encodeToString(
                BuyGoldInputData(
                    challengeAmount = amount,
                    showWeeklyChallengeAnimation = true
                )
            )
        )
        val currentTime = System.currentTimeMillis()
        navController.navigate(
            Uri.parse("${BaseConstants.InternalDeepLinks.BUY_GOLD_V2}/$encoded/${buyGoldFlowContext}/$currentTime"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openBuyGoldFlowWithPrefillAmount(
        prefillAmount: Float,
        buyGoldFlowContext: String
    ) {
        val encoded =
            serializer.encodeToString(
                BuyGoldInputData(
                    prefillAmount = prefillAmount
                )
            )
        val currentTime = System.currentTimeMillis()
        navController.navigate(
            Uri.parse("${BaseConstants.InternalDeepLinks.BUY_GOLD_V2}/$encoded/${buyGoldFlowContext}/$currentTime"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openOrderStatusFlow(
        transactionId: String,
        paymentProvider: String,
        paymentFlowSource: String,
        isOneTimeInvestment: Boolean,
        buyGoldFlowContext: String
    ) {
        val orderStatusModel = OrderStatusModel(
            transactionId = transactionId,
            paymentProvider = paymentProvider,
            paymentFlowSource = paymentFlowSource,
            isOneTimeInvestment = isOneTimeInvestment,
            buyGoldFlowContext = buyGoldFlowContext
        )
        val orderStatusModelString = encodeUrl(serializer.encodeToString(orderStatusModel))
        navController.navigate(
            Uri.parse("android-app://com.jar.app/orderStatusV2/$orderStatusModelString"),
            getNavOptions(true)
        )
    }

    override fun openInfoDialog() {
        job?.cancel()
        job = appScope.launch {
            fetchBuyGoldInfoUseCase.fetchBuyGoldInfo().collect(
                onSuccess = {
                    withContext(dispatcherProvider.main) {
                        coreUiApi.openInfoDialog(it.helpWorkFlow)
                    }
                })
        }
    }

}