package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.dp
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.OverlayType
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellCouponCodeDiscoveryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.util.concurrent.TimeUnit
import com.jar.app.core_ui.R as R1

class CouponCodeDiscoveryCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val couponCode: CouponCode,
    private val couponCodeDiscoveryData: com.jar.app.feature_homepage.shared.domain.model.CouponCodeDiscoveryData,
    private val isSingleCouponFlow: Boolean,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,BaseResources,
    CustomViewBindingEpoxyModel<FeatureHomepageCellCouponCodeDiscoveryBinding>(R.layout.feature_homepage_cell_coupon_code_discovery) {

    private var visibilityState: Int? = null
    private var countDownJob: Job? = null

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to couponCodeDiscoveryData.cardType,
                DynamicCardEventKey.FeatureType to couponCodeDiscoveryData.featureType,
                DynamicCardEventKey.Data to couponCode.couponCode,
                DynamicCardEventKey.CardTitle to couponCode.title.toString(),
                DynamicCardEventKey.CardDescription to couponCode.getCouponDescription(),
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bindItem(binding: FeatureHomepageCellCouponCodeDiscoveryBinding) {
        binding.root.setPlotlineViewTag(tag = couponCodeDiscoveryData.featureType + "_" + couponCode.couponCode)
        binding.tvCouponCode.text = couponCode.couponCode
        couponCode.title?.let {
            binding.tvCouponCodeTitle.text = it
        }
        binding.tvCouponCodeDescription.text = couponCode.getCouponDescription()
        val background = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor(couponCode.startColor ?: "#B85E99"),
                Color.parseColor(couponCode.endColor ?: "#F4AC69"),
            )
        )
        background.cornerRadius = 16f.dp
        binding.clRootContainer.background = background
        if (couponCode.validityInMillis != null) {
            binding.tvExpiryDate.isVisible = true
            setExpiryText(couponCode.validityInMillis!!,binding)
        } else {
            binding.tvExpiryDate.isVisible = false

        }
        binding.ivBackgroundOverlay.setImageResource(
            when (couponCode.getOverlayType()) {
                OverlayType.CIRCLE.name -> {
                    if (isSingleCouponFlow)
                        R1.drawable.core_ui_bg_circle_overlay_single_cc
                    else
                        R1.drawable.core_ui_bg_circle_overlay_cc
                }
                OverlayType.BACKWARD_INCLINE.name -> {
                    if (isSingleCouponFlow)
                        R1.drawable.core_ui_bg_backward_incline_overlay_single_cc
                    else
                        R1.drawable.core_ui_bg_backward_incline_overlay_cc
                }
                else -> {
                    if (isSingleCouponFlow)
                        R1.drawable.core_ui_bg_circle_overlay_single_cc
                    else
                        R1.drawable.core_ui_bg_circle_overlay_cc
                }
            }
        )

        couponCode.iconLink?.let {icon->
            Glide.with(binding.root.context)
                .asDrawable()
                .load(icon)
                .override(16.dp)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        binding.tvCouponCode.setCompoundDrawablesWithIntrinsicBounds(
                            resource,
                            null,
                            null,
                            null
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
        binding.clRootContainer.setDebounceClickListener {
            val data = PrimaryActionData(
                type = PrimaryActionType.DEEPLINK,
                value = couponCode.getBuyGoldPreApplyCouponDeeplink(BaseConstants.BuyGoldFlowContext.HOME_SCREEN),
                order = couponCodeDiscoveryData.getSortKey(),
                cardType = couponCodeDiscoveryData.getCardType(),
                featureType = couponCodeDiscoveryData.featureType
            )
            onActionClick.invoke(data, cardEventData)
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            this.visibilityState = visibilityState
            startShowEventJob(
                uiScope,
                isCardFullyVisible = {
                    this.visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
                },
                onCardShownEvent = {
                    onCardShown.invoke(cardEventData)
                }
            )
        }
    }

    override fun getBinding(view: View): FeatureHomepageCellCouponCodeDiscoveryBinding {
        return FeatureHomepageCellCouponCodeDiscoveryBinding.bind(view)
    }
    private fun setExpiryText(
        expiryTimeStamp: Long,
        binding: FeatureHomepageCellCouponCodeDiscoveryBinding
    ) {
        val systemMillis = System.currentTimeMillis()
        val expiryTimeStampInMillis = expiryTimeStamp - systemMillis

        val remainingDays = TimeUnit.MILLISECONDS.toDays(expiryTimeStampInMillis).toInt()
        if (remainingDays <= 0) {
            countDownJob?.cancel()
            countDownJob = uiScope.countDownTimer(
                expiryTimeStampInMillis,
                onInterval = {
                  binding.tvExpiryDate.text = String.format(
                       getCustomStringFormatted(binding.root.context, MR.strings.expires_in, it.milliSecondsToCountDown())
                    )
                },
                onFinished = {

                }
            )
        } else {
            if (remainingDays > 1) {
                binding.tvExpiryDate.text = getCustomStringFormatted(binding.root.context, MR.strings.feature_buy_gold_v2_expires_in_x_days, remainingDays)
            } else {
                binding.tvExpiryDate.text = getCustomStringFormatted(binding.root.context, MR.strings.feature_buy_gold_v2_expires_in_x_day, remainingDays)
            }
        }

    }
}