package com.jar.app.feature_spin.impl.ui

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_TEXT
import android.content.Intent.createChooser
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.getDateShortMonthNameAndYear
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_spin.databinding.FeatureSpinsJackpotPopupBinding
import com.jar.app.feature_spin.impl.custom.util.dpToPx
import com.jar.app.feature_spin.impl.custom.util.getWidthAndHeight
import com.jar.app.feature_spin.shared.util.SpinsEventKeys
import com.jar.app.core_base.domain.model.SpinBrandCouponOutcomeResponse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class SpinJackPotWithBrandCoupons : BaseDialogFragment<FeatureSpinsJackpotPopupBinding>() {
    private val args by navArgs<SpinJackPotWithBrandCouponsArgs>()

    private val jackpotResponse: SpinBrandCouponOutcomeResponse by lazy {
        args.resultJackpot
    }

    companion object {
        const val TAG = "SpinJackPotWithBrandCoupons"
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val widthAndHeight = getWidthAndHeight(this.requireActivity())
        val verticalMargin = widthAndHeight.first * 0.1
        val horizontalMargin = widthAndHeight.second * 0.1
        dialog?.window?.setLayout(
            widthAndHeight.first - verticalMargin.toInt(),
            widthAndHeight.second - horizontalMargin.toInt()
        )
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureSpinsJackpotPopupBinding
        get() = FeatureSpinsJackpotPopupBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(
            true,
            shouldShowFullScreen = true
        )

    override fun setup() {
        analyticsHandler.postEvent(
            com.jar.app.feature_spin.shared.util.SpinsEventKeys.ShownSpinRewardsScreen,
            mapOf(
                EventKey.FeatureType to com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureTypeSpinAndWin,
                EventKey.Screen to com.jar.app.feature_spin.shared.util.SpinsEventKeys.ScreenJackpotOtherBrands,
                com.jar.app.feature_spin.shared.util.SpinsEventKeys.Brand to jackpotResponse.externalBrandCouponInfo?.brandName.orEmpty(),
                com.jar.app.feature_spin.shared.util.SpinsEventKeys.Coupon to jackpotResponse.externalBrandCouponInfo?.brandCouponCodeId.orEmpty(),
                com.jar.app.feature_spin.shared.util.SpinsEventKeys.Title to jackpotResponse.externalBrandCouponInfo?.title.orEmpty()

            )
        )
        setupUI()

    }

    private fun setupUI() {
        with(binding) {
            tvHeading.text = jackpotResponse.header
            jackpotResponse.externalBrandCouponInfo?.brandIconLink?.let { imageUrl ->

                Glide.with(requireContext())
                    .load(imageUrl)
                    .override(56f.dpToPx(requireContext()))
                    .into(ivBrandIconCouponDetails)
            }
            tvCouponDetailsBrandTitle.text = jackpotResponse.externalBrandCouponInfo?.brandName
            tvCouponDetailsHeading.text = jackpotResponse.externalBrandCouponInfo?.title
            tvCouponDetailsDescription.text = jackpotResponse.externalBrandCouponInfo?.description

            btnShare.text = jackpotResponse.shareCta?.text
            tvNavigationText.text = jackpotResponse.navigationText
            btnCta.text = jackpotResponse.spinAgainCta?.text
            jackpotResponse.externalBrandCouponInfo?.expiry?.let { expiryTime ->
                tvCouponDetailsValidTill.text = String.format(
                    getCustomString(MR.strings.coupon_validity),
                    expiryTime.getDateShortMonthNameAndYear()
                )
            }
            tvNavigationText.text = jackpotResponse.navigationText

            jackpotResponse.shareCta?.iconLink?.let { imageUrl ->
                Glide.with(requireContext())
                    .load(imageUrl)
                    .override(18f.dpToPx(requireContext()))
                    .into(ButtonDrawableStartTarget(btnShare))

            }

            btnShare.setDebounceClickListener {
                analyticsHandler.postEvent(
                    com.jar.app.feature_spin.shared.util.SpinsEventKeys.ClickedButtonRewardsScreen,
                    mapOf(
                        EventKey.FeatureType to com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureTypeSpinAndWin,
                        EventKey.Screen to com.jar.app.feature_spin.shared.util.SpinsEventKeys.ScreenJackpotOtherBrands,
                        com.jar.app.feature_spin.shared.util.SpinsEventKeys.Brand to jackpotResponse.externalBrandCouponInfo?.brandName.orEmpty(),
                        com.jar.app.feature_spin.shared.util.SpinsEventKeys.CTA to com.jar.app.feature_spin.shared.util.SpinsEventKeys.Share
                    )
                )

                jackpotResponse.shareMsg?.let { shareMsg ->
                    shareMessageViaIntentChooser(shareMsg)
                }
            }
            ivClose.setDebounceClickListener {
                analyticsHandler.postEvent(
                    com.jar.app.feature_spin.shared.util.SpinsEventKeys.ClickedButtonRewardsScreen,
                    mapOf(
                        EventKey.FeatureType to com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureTypeSpinAndWin,
                        EventKey.Screen to com.jar.app.feature_spin.shared.util.SpinsEventKeys.ScreenJackpotOtherBrands,
                        com.jar.app.feature_spin.shared.util.SpinsEventKeys.Brand to jackpotResponse.externalBrandCouponInfo?.brandName.orEmpty(),
                        com.jar.app.feature_spin.shared.util.SpinsEventKeys.CTA to com.jar.app.feature_spin.shared.util.SpinsEventKeys.Cross
                    )
                )
                popBackStack()
            }

            btnCta.setDebounceClickListener {
                analyticsHandler.postEvent(
                    com.jar.app.feature_spin.shared.util.SpinsEventKeys.ClickedButtonRewardsScreen,
                    mapOf(
                        EventKey.FeatureType to com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureTypeSpinAndWin,
                        EventKey.Screen to com.jar.app.feature_spin.shared.util.SpinsEventKeys.ScreenJackpotOtherBrands,
                        com.jar.app.feature_spin.shared.util.SpinsEventKeys.Brand to jackpotResponse.externalBrandCouponInfo?.brandName.orEmpty(),
                        com.jar.app.feature_spin.shared.util.SpinsEventKeys.CTA to jackpotResponse.spinAgainCta?.text.orEmpty()
                    )
                )
                jackpotResponse.spinAgainCta?.deeplink?.let { deepLink ->
                    popBackStack()
                    EventBus.getDefault().post(HandleDeepLinkEvent(deepLink))
                }
            }

        }
    }

    private fun shareMessageViaIntentChooser(shareText: String) {
        val intent = Intent(ACTION_SEND).apply {
            type = "text/plain"
            val message = shareText
            putExtra(EXTRA_TEXT, message)
        }
        val chooserIntent = createChooser(intent, "Share with")
        startActivity(chooserIntent)
    }


}

class ButtonDrawableStartTarget(private val button: AppCompatButton) : CustomTarget<Drawable>() {
    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        button.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        // You can set a placeholder drawable here if necessary
    }


}