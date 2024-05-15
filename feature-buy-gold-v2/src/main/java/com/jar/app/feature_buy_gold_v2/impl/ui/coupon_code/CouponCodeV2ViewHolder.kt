package com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.jar.app.base.util.*
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.CellCouponCodeBuyGoldV2Binding
import com.jar.app.feature_buy_gold_v2.impl.util.getExpiryTimeStampInMillis
import com.jar.app.feature_buy_gold_v2.impl.util.isCouponExpired
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.util.concurrent.TimeUnit

internal class CouponCodeV2ViewHolder(
    private val binding: CellCouponCodeBuyGoldV2Binding,
    private val uiScope: CoroutineScope,
    private val onApplyClick: (couponCode: CouponCode, position: Int) -> Unit,
    private val onRemoveCouponClick: (couponCode: CouponCode, position: Int) -> Unit,
    private val onCouponExpired: (couponCode: CouponCode) -> Unit
): BaseViewHolder(binding.root) {

    private var couponCode: CouponCode? = null
    private var countDownJob: Job? = null

    init {
        binding.root.setDebounceClickListener {
            couponCode?.let {
                if (it.getCouponState() == CouponState.ACTIVE && it.isCouponAmountEligible) {
                    onApplyClick.invoke(it, bindingAdapterPosition)
                }
            }
        }

        binding.ivCross.setDebounceClickListener {
            couponCode?.let {
                onRemoveCouponClick.invoke(it, bindingAdapterPosition)
            }
        }
    }

    fun bind(couponCode: CouponCode) {
        this.couponCode = couponCode
        val couponType = couponCode.getCouponType()
        binding.tvCouponCode.isVisible = couponCode.couponCode.isNotEmpty()
        binding.tvCouponCode.text = if (couponType == CouponType.WINNINGS) couponCode.couponCode else "(${couponCode.couponCode})"
        binding.tvCouponTitle.text = couponCode.title
        binding.tvCouponDescription.setHtmlText(if (couponCode.isSelected) couponCode.couponAppliedDescription ?: couponCode.getCouponDescription() else couponCode.getCouponDescription())
        binding.clCouponContent.isSelected = couponCode.isSelected
        binding.tvUseCoupon.isVisible = !couponCode.isSelected
        binding.ivCross.isVisible = couponCode.isSelected
        binding.tvCouponCodeExpired.isVisible = false
        binding.tvUseCoupon.text = if (couponType == CouponType.WINNINGS) getCustomString(context, MR.strings.feature_buyg_gold_v2_use) else getCustomString(context, MR.strings.feature_buy_gold_v2_apply)
        couponCode.validityInMillis?.let {
            if (it > 0) {
                binding.tvCouponTimer.isVisible = true
                binding.tvCouponCode.isVisible = false
                setExpiryText(couponCode)
            } else {
                if (couponCode.getCouponState() == CouponState.INACTIVE) {
                    binding.tvCouponCode.isVisible = false
                    binding.tvCouponTimer.isVisible = false
                    binding.tvCouponCodeExpired.isVisible = true
                } else {
                    binding.tvCouponTimer.isVisible = false
                    binding.tvCouponCode.isVisible = true
                }
            }
        } ?: kotlin.run {
            binding.tvCouponTimer.isVisible = false
            binding.tvCouponCode.isVisible = true
        }
        binding.llBestValue.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            marginStart = if (couponType == CouponType.WINNINGS) 4.dp else 0.dp
        }

        updatedCouponSelectedState(isSelected = couponCode.isSelected, couponType)
        setCouponType(couponType, couponCode.couponsLeftText)
        setBestCouponTag(isBestCoupon = couponCode.isBestCoupon)
        setCouponState(couponCode)
    }

    private fun setBestCouponTag(isBestCoupon: Boolean) {
        binding.llBestValue.isVisible = isBestCoupon
        binding.viewBestCouponBg.isVisible = isBestCoupon
        binding.clCouponContent.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = if (isBestCoupon) 0.dp else 22.dp
        }
        binding.ivWinningsLogo.translationY = if (isBestCoupon) -22.dp.toFloat() else -4.dp.toFloat()
    }

    private fun setCouponType(couponType: CouponType, couponsLeftText: String?) {
        when (couponType) {
            CouponType.WINNINGS -> {
                binding.ivWinningsLogo.isVisible = true
                binding.tvLeft.isVisible = false
                binding.ivCouponLogo.isVisible = false
                binding.tvCouponCode.isVisible = true
                binding.tvCouponType.text = getCustomString(MR.strings.feature_buy_gold_v2_winnings)
                binding.tvCouponType.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.color_FFDA2D
                    )
                )
            }
            CouponType.REFERRAL -> {
                binding.tvLeft.isVisible = couponsLeftText != null
                binding.tvCouponCode.isVisible = couponsLeftText == null
                binding.tvLeft.text = couponsLeftText
                binding.ivWinningsLogo.isVisible = false
                binding.ivCouponLogo.isVisible = true
                binding.tvCouponType.text = getCustomString(MR.strings.feature_buy_gold_v2_referrals)
                binding.tvCouponType.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.color_FFDA2D
                    )
                )
            }
            else -> {
                binding.ivWinningsLogo.isVisible = false
                binding.tvLeft.isVisible = false
                binding.ivCouponLogo.isVisible = true
                binding.tvCouponType.text = getCustomString(MR.strings.feature_buy_gold_v2_coupon)
                binding.tvCouponType.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.white
                    )
                )
            }
        }
        binding.tvCouponCode.setPadding(
            if (couponType == CouponType.WINNINGS) 8.dp else 0.dp,
            if (couponType == CouponType.WINNINGS) 4.dp else 0.dp,
            if (couponType == CouponType.WINNINGS) 8.dp else 0.dp,
            if (couponType == CouponType.WINNINGS) 4.dp else 0.dp,
        )
    }

    private fun updatedCouponSelectedState(isSelected: Boolean, couponType: CouponType) {
        val textColor = if (isSelected) com.jar.app.core_ui.R.color.color_EEEAFF else com.jar.app.core_ui.R.color.color_ACA1D3
        binding.tvCouponCode.setTextColor(
            ContextCompat.getColor(
                context, textColor
            )
        )
        binding.tvCouponDescription.setTextColor(
            ContextCompat.getColor(
                context, textColor
            )
        )
        binding.tvCouponTimer.setTextColor(
            ContextCompat.getColor(
                context, if (isSelected) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_D2C7F6
            )
        )

        val borderBg = if (isSelected) R.drawable.feature_buy_gold_v2_bg_rounded_stroke_ffffff_4dp else R.drawable.feature_buy_gold_v2_bg_rounded_stroke_d5cdf2_4dp
        binding.tvCouponCode.background =
            if (couponType == CouponType.WINNINGS)
                ContextCompat.getDrawable(
                    context, borderBg
                )
            else null
        binding.tvCouponTimer.setBackgroundResource(borderBg)
    }

    private fun setExpiryText(couponCode: CouponCode) {
        if (couponCode.isCouponExpired()) {
            setCouponExpired()
        } else {
            val remainingDays = TimeUnit.MILLISECONDS.toDays(couponCode.getExpiryTimeStampInMillis()).toInt()
            if (remainingDays <= 0) {
                countDownJob?.cancel()
                countDownJob = uiScope.countDownTimer(
                    couponCode.getExpiryTimeStampInMillis(),
                    onInterval = {
                        binding.tvCouponTimer.text = getCustomStringFormatted(
                            context,
                            MR.strings.feature_buy_gold_v2_expires_in_x_time,
                            it.milliSecondsToCountDown()
                        )
                    },
                    onFinished = {
                        setCouponExpired()
                        couponCode?.let {
                            if (it.isSelected)
                                onRemoveCouponClick(it, bindingAdapterPosition)
                        }
                    }
                )
            } else {
                if (remainingDays > 1) {
                    binding.tvCouponTimer.text = getCustomStringFormatted(
                        context,
                        MR.strings.feature_buy_gold_v2_expires_in_x_days,
                        remainingDays
                    )
                } else {
                    binding.tvCouponTimer.text = getCustomStringFormatted(
                        context,
                        MR.strings.feature_buy_gold_v2_expires_in_x_day,
                        remainingDays
                    )
                }
            }
        }
    }

    private fun setCouponExpired() {
        couponCode?.let {
            onCouponExpired.invoke(it)
        }
    }

    private fun setCouponState(couponCode: CouponCode) {
        val couponType = couponCode.getCouponType()
        val couponState = if(couponCode.isCouponAmountEligible.not()) CouponState.INACTIVE else couponCode.getCouponState()
        val colorInactive = com.jar.app.core_ui.R.color.white_30
        binding.viewGrey.isVisible = couponState == CouponState.INACTIVE
        binding.tvCouponType.setTextColor(
            ContextCompat.getColor(
                context, if (couponState == CouponState.INACTIVE) colorInactive else if (couponType == CouponType.WINNINGS) com.jar.app.core_ui.R.color.color_FFDA2D else com.jar.app.core_ui.R.color.white
            )
        )
        binding.tvUseCoupon.setTextColor(
            ContextCompat.getColor(
                context, if (couponState == CouponState.INACTIVE) colorInactive else com.jar.app.core_ui.R.color.color_1EA787
            )
        )
        binding.tvCouponTitle.setTextColor(
            ContextCompat.getColor(
                context, if (couponState == CouponState.INACTIVE) colorInactive else com.jar.app.core_ui.R.color.white
            )
        )
        binding.tvCouponDescription.setTextColor(
            ContextCompat.getColor(
                context, if (couponState == CouponState.INACTIVE) colorInactive else if (couponCode.isSelected) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )
        binding.tvCouponTimer.setTextColor(
            ContextCompat.getColor(
                context, if (couponState == CouponState.INACTIVE) colorInactive else if (couponCode.isSelected) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_D2C7F6
            )
        )
        binding.tvCouponCode.setTextColor(
            ContextCompat.getColor(
                context, if (couponState == CouponState.INACTIVE) colorInactive else if (couponCode.isSelected) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )
    }
}