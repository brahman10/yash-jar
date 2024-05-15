package com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_buy_gold_v2.databinding.FragmentCouponCodeAppliedDialogBinding
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class CouponCodeAppliedDialogFragment : BaseDialogFragment<FragmentCouponCodeAppliedDialogBinding>(){

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<CouponCodeAppliedDialogFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCouponCodeAppliedDialogBinding
        get() = FragmentCouponCodeAppliedDialogBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    override fun setup() {
        setupUI()
        setupListeners()
        analyticsApi.postEvent(
            BuyGoldV2EventKey.BuyNow_CouponPopupShown,
            mapOf(
                BuyGoldV2EventKey.couponTitle to args.couponApplied.couponTile,
                BuyGoldV2EventKey.isUserWinningsApplied to if (args.couponApplied.couponType == CouponType.WINNINGS.name) BuyGoldV2EventKey.Buy_Gold_YES else BuyGoldV2EventKey.Buy_Gold_NO,
                BuyGoldV2EventKey.POSITION to args.couponApplied.couponPosition
            )
        )
    }

    private fun setupUI() {
        binding.tvCouponCode.text = if (args.couponApplied.couponType == CouponType.WINNINGS.name) getCustomString(MR.strings.feature_buy_gold_v2_jar_winnings_applied) else getCustomStringFormatted(MR.strings.feature_buy_gold_v2_x_coupon_code_applied, args.couponApplied.couponCode)
        binding.tvCouponDescription.setHtmlText(decodeUrl(args.couponApplied.couponDescription))
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            analyticsApi.postEvent(
                BuyGoldV2EventKey.BuyNow_CouponPopupClicked,
                mapOf(
                    BuyGoldV2EventKey.clickType to BuyGoldV2EventKey.cancelButton
                )
            )
            dismissAllowingStateLoss()
        }

        binding.tvOkay.setDebounceClickListener {
            analyticsApi.postEvent(
                BuyGoldV2EventKey.BuyNow_CouponPopupClicked,
                mapOf(
                    BuyGoldV2EventKey.clickType to BuyGoldV2EventKey.yayButton
                )
            )
            dismiss()
        }
    }
}