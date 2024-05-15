package com.jar.app.feature_buy_gold_v2.impl.ui.breakdown

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.getAppNameFromPkgName
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.getFormattedTextForOneStringValue
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.volumeToStringWithoutTrailingZeros
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FragmentBuyGoldV2BreakdownBottomSheetV2Binding
import com.jar.app.feature_buy_gold_v2.impl.ui.buy_gold.BuyGoldV2FragmentViewModelAndroid
import com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code.CouponCodeVariantTwoBinder
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.model.InitiateBuyGoldData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.NewPaymentStripForBreakdown
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentOptionsData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentSectionHeaderType
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentType
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldUpiApp
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_buy_gold_v2.shared.util.ScreenName
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.CouponCodeVariant
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_payment.impl.ui.payment_option.PayNowSection
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class BuyGoldV2BreakdownBottomSheetFragmentV2 : BaseBottomSheetDialogFragment<FragmentBuyGoldV2BreakdownBottomSheetV2Binding>(), BaseResources{

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider by hiltNavGraphViewModels<BuyGoldV2FragmentViewModelAndroid>(R.id.buy_gold_v2_navigation)

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<BuyGoldV2BreakdownBottomSheetFragmentV2Args>()

    private val couponCode by lazy {
        try {
            args.breakDownData.couponCode.takeIf { it.isNullOrEmpty().not() }?.let {
                return@lazy serializer.decodeFromString<CouponCode>(it)
            } ?: kotlin.run {
                return@lazy null
            }
        } catch (e: Exception) {
            return@lazy null
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBuyGoldV2BreakdownBottomSheetV2Binding
        get() = FragmentBuyGoldV2BreakdownBottomSheetV2Binding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUI(couponCode)
        setupListeners()
        observeFlows()
    }

    private fun observeFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.couponCodesFlow.collectUnwrapped(
                    onSuccess = {
                        val couponCode = it?.couponCodes?.find { it.isSelected }
                        setupUI(couponCode)
                    },
                )
            }
        }
    }

    private fun setupUI(couponCode:CouponCode?) {
        val breakDownData = args.breakDownData

        setPaymentStrip(breakDownData.newPaymentStripForBreakdown)
        binding.tvCouponCode.isVisible = couponCode != null
        binding.tvRewardAmount.isVisible = couponCode != null
        binding.tvRewardTitle.isVisible = couponCode != null
        binding.clAmountWithReward.isVisible = couponCode != null
        breakDownData.goldPurchasePrice?.let {
            binding.tvGoldPurchaseValue.text =
            getCustomStringFormatted(
                MR.strings.feature_buy_gold_v2_buy_price_v2, it
            )
        }?:run{
            binding.tvGoldPurchaseValue.isVisible = false
            binding.labelGoldPurchaseValue.isVisible = false

        }

        couponCode?.let {
            val maxRewardThatCanBeAvailed = it.getMaxRewardThatCanBeAvailed(breakDownData.totalPayableAmount)
            binding.tvAmountWithReward.text = "₹${maxRewardThatCanBeAvailed.getFormattedAmount(shouldRemoveTrailingZeros = true)}"//getCustomStringFormatted(MR.strings.feature_buy_gold_v2_rupees_x_string, (breakDownData.totalPayableAmount+maxRewardThatCanBeAvailed).roundUp(2).getFormattedAmount(shouldRemoveTrailingZeros = true))

            val couponCodeValue = couponCode.couponCode
            val fullText = "Offer applied: $couponCodeValue"
            val spannable = SpannableString(fullText)

            val boldSpan = StyleSpan(Typeface.BOLD)
            val start = fullText.indexOf(couponCodeValue)
            val end = start + couponCodeValue.length

            spannable.setSpan(boldSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

            binding.tvCouponCode.text = spannable//getCustomStringFormatted(MR.strings.feature_buy_gold_v2_offer_applied_x_code, it.couponCode)
            binding.tvRewardAmount.text = "₹${(breakDownData.totalPayableAmount+maxRewardThatCanBeAvailed).roundUp(2).getFormattedAmount(shouldRemoveTrailingZeros = true)}"//getCustomStringFormatted(MR.strings.feature_buy_gold_v2_extra_x_of_gold, maxRewardThatCanBeAvailed.getFormattedAmount(shouldRemoveTrailingZeros = true))
            binding.tvRewardTitle.text = "Gold to be added to Jar Locker"

        }

        binding.tvAmountValue.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_rupees_x_string, breakDownData.totalPayableAmount.roundUp(2).getFormattedAmount(shouldRemoveTrailingZeros = true))
        binding.tvGoldQuantity.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_x_gm_string, breakDownData.goldVolume.volumeToStringWithoutTrailingZeros())
        binding.labelGst.text = getCustomString(
            MR.strings.feature_buy_gold_v2_gst_x
        ).getFormattedTextForOneStringValue(breakDownData.applicableTax.volumeToStringWithoutTrailingZeros())
        binding.tvGoldValue.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_rupees_x_string, breakDownData.goldValue.roundUp(2).getFormattedAmount(shouldRemoveTrailingZeros = true))
        binding.tvGst.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_rupees_x_string, (breakDownData.totalPayableAmount-breakDownData.goldValue).roundDown(2).getFormattedAmount(shouldRemoveTrailingZeros = true))

        val isAnyCouponCodeEligible = viewModel.couponCodeResponse?.couponCodes?.any { it.isCouponAmountEligible }
        if(isAnyCouponCodeEligible.orFalse()) {
            binding.applyCouponSecion.root.isVisible = couponCode == null &&  (viewModel.couponCodeResponse?.couponCodes?.first()?.couponCodeVariant == CouponCodeVariant.COUPON_VARIANT_TWO.name)
            viewModel.couponCodeResponse?.couponCodes?.first()?.let {
                CouponCodeVariantTwoBinder(binding.applyCouponSecion, requireContext(), uiScope,
                    onApplyClick = { couponCode, _,screenName ->

                        analyticsHandler.postEvent(
                            BuyGoldV2EventKey.ClickedApply_CouponTextbox_OrderPreviewScreen,
                            mapOf(
                                BuyGoldV2EventKey.couponTitle to couponCode.title.orEmpty(),
                                BuyGoldV2EventKey.moneySavedByCoupon to couponCode.getMaxRewardThatCanBeAvailed(
                                    viewModel.buyAmount
                                ).orZero(),
                                BuyGoldV2EventKey.couponDiscountPercentage to couponCode.rewardPercentage.orZero(),
                                BuyGoldV2EventKey.isWinningsCoupon to if (couponCode.getCouponType() == CouponType.WINNINGS) BuyGoldV2EventKey.Buy_Gold_YES else BuyGoldV2EventKey.Buy_Gold_NO,
                                BuyGoldV2EventKey.Amount to viewModel.buyAmount,
                                BuyGoldV2EventKey.CouponCode to couponCode.couponCode.orEmpty(),
                                BuyGoldV2EventKey.Screen to screenName,

                                )
                        )
                        onCouponCodeClicked(couponCode)
                    },
                    onCouponExpired = {_->

                    },
                    getCurrentAmount = {
                        viewModel.buyAmount
                    },
                    screenName = ScreenName.Buy_Gold_Break_Down_Screen.name).bind(it)
            }
        }else{
            binding.applyCouponSecion.root.isVisible = false
        }
    }

    private fun setPaymentStrip(newPaymentStripForBreakdown: NewPaymentStripForBreakdown?) {
        binding.btnBuyNow.isVisible = newPaymentStripForBreakdown == null
        binding.composeView.isVisible = newPaymentStripForBreakdown != null

        newPaymentStripForBreakdown?.let {
            binding.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    Column() {
                        PayNowSection(
                            oneTimeUpiApp = newPaymentStripForBreakdown.lastUsedUpiApp,
                            payNowCtaText = newPaymentStripForBreakdown.ctaText,
                            appChooserText = newPaymentStripForBreakdown.paymentAppChooserText,
                            onAppChooserClicked ={
                                analyticsHandler.postEvent(
                                    BuyGoldV2EventKey.BuyGold_AutoPayMethod_Click,
                                    mapOf(
                                        BuyGoldV2EventKey.recommended_upi to args.breakDownData.newPaymentStripForBreakdown?.lastUsedUpiApp?.packageName.orEmpty().getAppNameFromPkgName(requireContext().applicationContext.packageManager).orEmpty()
                                    )
                                )
                                findNavController().navigateUp()
                                val buyGoldPaymentOptionsData = BuyGoldPaymentOptionsData(
                                    context = BaseConstants.BuyGoldFlowContext.BUY_GOLD,
                                    maxPaymentMethodsCount = newPaymentStripForBreakdown.maxPaymentMethodsCount,
                                    ctaText = newPaymentStripForBreakdown.ctaText
                                )
                                val encoded = serializer.encodeToString(buyGoldPaymentOptionsData)
                                navigateTo("android-app://com.jar.app/buyGoldPaymentOption/$encoded")
                            },
                            onPayNowClicked = {
                                val buyGoldUpiApp = BuyGoldUpiApp(
                                    payerApp = newPaymentStripForBreakdown.lastUsedUpiApp.packageName,
                                    headerType = BuyGoldPaymentSectionHeaderType.RECOMMENDED
                                )
                                findNavController().getBackStackEntry(R.id.buyGoldV2Fragment).savedStateHandle[BuyGoldV2Constants.INITIATE_BUY_GOLD_DATA] = InitiateBuyGoldData(
                                    buyGoldPaymentType = BuyGoldPaymentType.JUSPAY_UPI_INTENT,
                                    selectedUpiApp = buyGoldUpiApp
                                )
                                findNavController().navigateUp()
                            },
                            showPaymentSecureFooter = true
                        )
                    }
                }
            }
        }
    }

    private fun onCouponCodeClicked(couponCode: CouponCode) {
        if (viewModel.canApplyCoupon(couponCode)) {
            viewModel.applyCouponCode(
                couponCode,
                ScreenName.Buy_Gold_Break_Down_Screen.name
            )
        } else {
            viewModel.getApplyCouponErrorMessage(
                couponCode
            )?.let {
                getCustomStringFormatted(
                    it,
                    couponCode.minimumAmount.toInt()
                ).snackBar(
                    binding.root,
                    com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
                    progressColor = com.jar.app.core_ui.R.color.color_016AE1,
                    duration = 2000,
                    translationY = 0f
                )
            }
        }
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            dismiss()
        }

        binding.btnBuyNow.setDebounceClickListener {
            findNavController().getBackStackEntry(R.id.buyGoldV2Fragment).savedStateHandle[BuyGoldV2Constants.INITIATE_BUY_GOLD_DATA] = InitiateBuyGoldData(
                buyGoldPaymentType = BuyGoldPaymentType.PAYMENT_MANGER,
                selectedUpiApp = null
            )
            dismiss()
        }
    }
}