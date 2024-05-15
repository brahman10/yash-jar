package com.jar.app.feature_buy_gold_v2.impl.ui.breakdown

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.BaseResources
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FragmentBuyGoldV2BreakdownBottomSheetBinding
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.model.InitiateBuyGoldData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentType
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.app.feature_payment.api.PaymentManager
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class BuyGoldV2BreakdownBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentBuyGoldV2BreakdownBottomSheetBinding>(), BaseResources{

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var paymentManager: PaymentManager

    private val args by navArgs<BuyGoldV2BreakdownBottomSheetFragmentArgs>()

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

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBuyGoldV2BreakdownBottomSheetBinding
        get() = FragmentBuyGoldV2BreakdownBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val breakDownData = args.breakDownData

        binding.dottedLine3.isVisible = couponCode != null
        binding.tvCouponCode.isVisible = couponCode != null
        binding.tvRewardAmount.isVisible = couponCode != null
        binding.tvRewardTitle.isVisible = couponCode != null
        binding.clAmountWithReward.isVisible = couponCode != null

        couponCode?.let {
            val maxRewardThatCanBeAvailed = it.getMaxRewardThatCanBeAvailed(breakDownData.totalPayableAmount)
            binding.tvAmountWithReward.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_rupees_x_string, (breakDownData.totalPayableAmount+maxRewardThatCanBeAvailed).roundUp(2).getFormattedAmount(shouldRemoveTrailingZeros = true))
            binding.tvCouponCode.isVisible = it.getCouponType() != CouponType.WINNINGS
            binding.tvCouponCode.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_coupon_applied_x_code, it.couponCode)
            binding.tvRewardAmount.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_extra_x_of_gold, maxRewardThatCanBeAvailed.getFormattedAmount(shouldRemoveTrailingZeros = true))
            if (it.getCouponType() == CouponType.WINNINGS) {
                binding.tvRewardTitle.text = getCustomString(MR.strings.feature_buy_gold_v2_jar_winnings_applied)
            } else {
                //Show max reward amount along with gold percent
                if (maxRewardThatCanBeAvailed == it.maxAmount) {
                    binding.tvRewardTitle.text = getCustomString(
                        MR.strings.feature_buy_gold_v2_x_gold_extra_earned_with_max_reward
                    ).getFormattedTextForXStringValues(
                        listOf(
                            it.rewardPercentage?.orZero().toString(),
                            it.maxAmount.orZero().getFormattedAmount()
                        )
                    )
                } else {
                    binding.tvRewardTitle.text = getCustomString(
                        MR.strings.feature_buy_gold_v2_x_gold_extra_earned
                    ).getFormattedTextForOneStringValue(it.rewardPercentage?.orZero().toString())
                }
            }
        }

        binding.tvPayableAmount.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_rupees_x_string, breakDownData.totalPayableAmount.roundUp(2).getFormattedAmount(shouldRemoveTrailingZeros = true))
        binding.tvGoldQuantity.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_x_gm_string, breakDownData.goldVolume.volumeToStringWithoutTrailingZeros())
        binding.labelGst.text = getCustomString(
            MR.strings.feature_buy_gold_v2_gst_x
        ).getFormattedTextForOneStringValue(breakDownData.applicableTax.volumeToStringWithoutTrailingZeros())
        binding.tvGoldValue.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_rupees_x_string, breakDownData.goldValue.roundUp(2).getFormattedAmount(shouldRemoveTrailingZeros = true))
        binding.tvGst.text = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_rupees_x_string, (breakDownData.totalPayableAmount-breakDownData.goldValue).roundDown(2).getFormattedAmount(shouldRemoveTrailingZeros = true))
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