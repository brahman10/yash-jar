package com.jar.app.feature_sell_gold.impl.ui.amount

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jar.app.base.data.event.OpenUpiSelectionScreen
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openUrlInChromeTabOrExternalBrowser
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_base.util.BaseConstants.SellGoldFlow.FROM_SELL_GOLD_REVAMP
import com.jar.app.core_base.util.BaseConstants.SellGoldFlow.FROM_SELL_GOLD_REVAMP_PAN_ONLY
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.feature_kyc.api.KycApi
import com.jar.app.feature_kyc.shared.domain.model.KycVerificationStatus
import com.jar.app.feature_sell_gold.impl.ui.model.VpaSelectionArgument
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

internal const val MIN_WITHDRAWAL_AMOUNT_ALLOWED_WITHOUT_ID = 30f
internal const val MAX_WITHDRAWAL_AMOUNT_ALLOWED_WITHOUT_PAN = 50_000f

@AndroidEntryPoint
class AmountEntryFragment : BaseComposeFragment() {

    private val viewModelProvider by viewModels<AmountEntryViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy { viewModelProvider.getInstance() }

    @Inject
    lateinit var serializer: Serializer

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.postSellGoldBackButtonClickedEvent()
            popBackStack()
        }
    }

    @Inject
    lateinit var kycApi: KycApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }


    @Composable
    override fun RenderScreen() {
        val goldPriceState by viewModel.currentGoldSellPriceFlow.collectAsStateWithLifecycle()
        val volumeFromAmount by viewModel.volumeFromAmountFlow.collectAsStateWithLifecycle()
        val drawerDetails by viewModel.drawerDetailsFlow.collectAsStateWithLifecycle()
        val kycDetails by viewModel.kycDetailsFlow.collectAsStateWithLifecycle()
        var hasGoldPriceUpdatedAtLeastOnce by remember { mutableStateOf(false) }

        LaunchedEffect(drawerDetails) {
            drawerDetails.data?.data?.let {
                viewModel.postSellGoldScreenShownEvent(it, kycDetails.data?.data)
            }
        }

        LaunchedEffect(goldPriceState?.rateId) {
            goldPriceState?.rateId.takeIf { it.isNullOrBlank().not() }?.also {
                hasGoldPriceUpdatedAtLeastOnce = true
            }
        }

        AmountEntryScreen(
            goldPriceState = goldPriceState,
            volumeFromAmount = volumeFromAmount.orZero(),
            drawerDetailsResponse = drawerDetails.data?.data,
            kycDetails = kycDetails.data?.data,
            onBackClick = {
                viewModel.postSellGoldBackButtonClickedEvent()
                popBackStack()
            },
            onFaqClick = { link ->
                viewModel.postSellGoldFaqButtonClickedEvent()
                openUrlInChromeTabOrExternalBrowser(requireContext(), link, false)
            },
            onInitiateIdVerification = { enteredAmount ->
                kycApi.initiateUserIdVerification(
                    fromScreen = if (enteredAmount.toFloatOrZero() > MAX_WITHDRAWAL_AMOUNT_ALLOWED_WITHOUT_PAN) {
                        viewModel.postVerifyPanButtonClicked()
                        FROM_SELL_GOLD_REVAMP_PAN_ONLY
                    } else {
                        FROM_SELL_GOLD_REVAMP
                    },
                    shouldShowOnlyPan = false,
                    onKycFlowExecution = { kycStatus ->
                        viewModel.fetchKycDetails()
                        if (kycStatus == KycVerificationStatus.VERIFIED.name) {
                            EventBus.getDefault().post(
                                OpenUpiSelectionScreen(enteredAmount = enteredAmount)
                            )
                        }
                    }
                )
            },
            onProceedClick = { enteredAmount, hasViewedDrawerDetailsOnce, hasEnteredValidAmountOnce, currentCarouselPage ->
                drawerDetails.data?.data?.let { drawerDetailsResponse ->
                    viewModel.postSavingsValueScreenEvent(
                        hasViewedDrawerDetailsOnce,
                        hasEnteredValidAmountOnce,
                        hasGoldPriceUpdatedAtLeastOnce,
                        currentCarouselPage,
                        drawerDetailsResponse,
                        kycDetails.data?.data
                    )
                }
                navigateTo(
                    AmountEntryFragmentDirections.actionSellGoldFragmentToUpiSelectionFragment(
                        vpaSelectionArgs = encodeUrl(
                            serializer.encodeToString(
                                VpaSelectionArgument(withdrawalPrice = enteredAmount)
                            )
                        )
                    )
                )
            },
            onVerifyIdClick = {
                viewModel.postVerifyIdButtonClickedEvent()
            },
            onContactUsClick = { link ->
                viewModel.postContactUsButtonClicked()
                openUrlInChromeTabOrExternalBrowser(requireContext(), link, false)
            },
            onWithdrawDetailsToggle = {
                viewModel.postWithdrawDetailsToggledEvent()
            },
            onEnteredAmountChange = { amount, hasShownVerificationSheetAtLeastOnce ->
                viewModel.calculateVolumeFromAmount(amount)
                viewModel.postSellGoldMoneyEnteredEvent(
                    amount,
                    hasShownVerificationSheetAtLeastOnce
                )
            },
            onVerificationBottomSheetToggled = {
                viewModel.postWithdrawalVerificationSheetOpenedEvent()
            }
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.fetchData()
        registerBackPressDispatcher()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }
}