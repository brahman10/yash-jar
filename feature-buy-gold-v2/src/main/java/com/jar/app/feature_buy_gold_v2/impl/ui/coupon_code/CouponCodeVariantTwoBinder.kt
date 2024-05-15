package com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.dp
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.R
import com.jar.app.feature_buy_gold_v2.databinding.CellCouponCodeVariant2Binding
import com.jar.app.feature_buy_gold_v2.impl.util.getExpiryTimeStampInMillis
import com.jar.app.feature_buy_gold_v2.impl.util.isCouponExpired
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.util.concurrent.TimeUnit

class CouponCodeVariantTwoBinder(
    private val binding: CellCouponCodeVariant2Binding,
    private val context: Context,
    private val uiScope: CoroutineScope,
    private val onApplyClick: (couponCode: CouponCode, position: Int, screenName: String) -> Unit,
    private val getCurrentAmount: () -> Float,
    private val onCouponExpired: (couponCode: CouponCode) -> Unit,
    private val screenName: String
) : BaseResources {
    private var countDownJob: Job? = null
    fun bind(couponCode: CouponCode) {
        couponCode.iconLink?.let {
            Glide.with(context).load(it).into(binding.ivCouponLogo)
        }
        if (couponCode.isCouponAmountEligible.not()) {
            binding.ivCouponLogo.alpha = 0.3f
        } else {
            binding.ivCouponLogo.alpha = 1f
        }
        val couponType = couponCode.getCouponType()
        binding.tvCouponTitle.text = couponCode.title
        val currentAmount = getCurrentAmount()
        val couponDescription = when {
            couponCode.isCouponAmountEligible.not() && currentAmount < couponCode.maxAmount.orZero() -> getCustomStringFormatted(
                context,
                MR.strings.feature_buy_gold_v2_coupon_eligibility,
                (couponCode.minimumAmount - getCurrentAmount()).toInt()
            )

            couponCode.isSelected -> couponCode.couponAppliedDescription
                ?: couponCode.getCouponDescription()

            else -> couponCode.getCouponDescription()
        }
        with(binding) {
            val color = if (couponCode.isSelected) R.color.white else R.color.color_ACA1D3
            val detailsColor = if (couponCode.isSelected) R.color.white else R.color.color_ACA1D3
            val showDownIcon = tvCouponDetailsList.isShown
            tvViewDetails.setTextColor(ContextCompat.getColor(context, color))
            tvCouponDetailsList.setTextColor(ContextCompat.getColor(context, detailsColor))
            ivArrowDown.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, color))


        }
        binding.tvCouponDescription.setHtmlText(
            couponDescription
        )
        binding.clCouponContent.isSelected = couponCode.isSelected

        binding.dashedLine.backgroundTintList = ColorStateList.valueOf(
            if (couponCode.isSelected) Color.parseColor(
                "#3BA089"
            ) else Color.parseColor("#595078")
        )
        binding.tvUseCoupon.text = if (couponType == CouponType.WINNINGS) getCustomString(
            context,
            MR.strings.feature_buyg_gold_v2_use
        ) else getCustomString(context, MR.strings.feature_buy_gold_v2_apply)
        couponCode.validityInMillis?.let {
            if (it > 0) {
                setExpiryText(couponCode)
            }
        } ?: kotlin.run {
            if (couponCode.getCouponType() == CouponType.WINNINGS) {
                binding.tvLeft.isVisible = couponCode.getCouponState() == CouponState.ACTIVE
                binding.tvLeft.setHtmlText(couponCode.couponCode.orEmpty())
            }
        }
        binding.llBestValue.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            marginStart = if (couponType == CouponType.WINNINGS) 4.dp else 0.dp
        }

        updatedCouponSelectedState(isSelected = couponCode.isSelected)
        setCouponType(couponType, couponCode.couponsLeftText, couponCode.getCouponState())
        setBestCouponTag(isBestCoupon = couponCode.isBestCoupon)
        setCouponState(couponCode)
        binding.tvUseCoupon.setOnClickListener {
            onApplyClick(couponCode, 0, screenName)
        }

        couponCode.couponCodeDetails?.let {
            binding.viewDetailsGroup.isVisible = true
            val formattedPoints = it.pointsList.mapIndexed { index, point ->
                "${index + 1}. $point"
            }.joinToString("\n")
            binding.tvCouponDetailsList.text = formattedPoints
            binding.couponViewDetails.setOnClickListener {
                val showDownIcon = !binding.tvCouponDetailsList.isShown
                binding.tvCouponDetailsList.isVisible = binding.tvCouponDetailsList.isShown.not()

                if (showDownIcon.not()) {
                    binding.ivArrowDown.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.core_ui_ic_arrow_down
                        )
                    )

                } else {
                    binding.ivArrowDown.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.core_ui_ic_arrow_up
                        )
                    )
                }
            }
        } ?: run {
            binding.viewDetailsGroup.isVisible = false
        }


        val textToUnderline = "View Details"
        val spannable = SpannableString(textToUnderline)
        spannable.setSpan(
            UnderlineSpan(),
            0,
            textToUnderline.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvViewDetails.text = spannable

        val tvLeftColor = if (couponCode.isSelected.not()) R.color.color_EBB46A else R.color.white
        binding.tvLeft.setTextColor(ContextCompat.getColor(context, tvLeftColor))
        val tvLeftColorTintColor =
            if (couponCode.isSelected.not()) R.color.color_43353B else R.color.white_20
        binding.tvLeft.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, tvLeftColorTintColor))
    }

    private fun setBestCouponTag(isBestCoupon: Boolean) {
        binding.llBestValue.isVisible = isBestCoupon
        binding.viewBestCouponBg.isVisible = isBestCoupon
        binding.clCouponContent.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = if (isBestCoupon) 0.dp else 22.dp
        }
    }

    private fun setCouponType(
        couponType: CouponType,
        couponsLeftText: String?,
        couponState: CouponState
    ) {
        when (couponType) {
            CouponType.WINNINGS -> {
                binding.tvLeft.isVisible = couponState == CouponState.ACTIVE
            }

            CouponType.JACKPOT -> {
                binding.tvLeft.isVisible = true
            }

            CouponType.REFERRAL -> {
                binding.tvLeft.isVisible = couponsLeftText != null
                binding.tvLeft.text = couponsLeftText
                binding.ivCouponLogo.isVisible = true
            }

            else -> {
                binding.tvLeft.isVisible = false
                binding.ivCouponLogo.isVisible = true
            }
        }
    }

    private fun updatedCouponSelectedState(isSelected: Boolean) {
        val textColor =
            if (isSelected) R.color.color_EEEAFF else R.color.color_ACA1D3
        binding.tvCouponDescription.setTextColor(
            ContextCompat.getColor(
                context, textColor
            )
        )
        binding.tvUseCoupon.text = if (isSelected) "REMOVE" else "Apply"
    }

    private fun setExpiryText(couponCode: CouponCode) {
        if (couponCode.isCouponExpired()) {
            onCouponExpired(couponCode)
        } else {
            val remainingDays =
                TimeUnit.MILLISECONDS.toDays(couponCode.getExpiryTimeStampInMillis()).toInt()
            if (remainingDays <= 0) {
                countDownJob?.cancel()
                countDownJob = uiScope.countDownTimer(
                    couponCode.getExpiryTimeStampInMillis(),
                    onInterval = {
                        binding.tvLeft.text = getCustomStringFormatted(
                            context,
                            MR.strings.feature_buy_gold_v2_expires_in_x_time,
                            it.milliSecondsToCountDown()
                        )
                    },
                    onFinished = {
                        onCouponExpired(couponCode)
                    }
                )
            } else {
                if (remainingDays > 1) {
                    binding.tvLeft.text = getCustomStringFormatted(
                        context,
                        MR.strings.feature_buy_gold_v2_expires_in_x_days,
                        remainingDays
                    )
                } else {
                    binding.tvLeft.text = getCustomStringFormatted(
                        context,
                        MR.strings.feature_buy_gold_v2_expires_in_x_day,
                        remainingDays
                    )
                }
            }
        }
    }


    private fun setCouponState(couponCode: CouponCode) {
        val couponType = couponCode.getCouponType()
        val couponState =
            if (couponCode.isCouponAmountEligible.not()) CouponState.INACTIVE else couponCode.getCouponState()
        val colorInactive = R.color.white_30
        binding.viewGrey.isVisible = couponState == CouponState.INACTIVE


        val applyTextColor = when {
            couponState == CouponState.INACTIVE -> colorInactive
            couponCode.isSelected -> R.color.white
            else -> R.color.color_1EA787
        }
        binding.tvUseCoupon.setTextColor(
            ContextCompat.getColor(
                context,
                applyTextColor
            )
        )
        binding.tvCouponTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (couponState == CouponState.INACTIVE) colorInactive else R.color.white
            )
        )
        binding.tvCouponDescription.setTextColor(
            ContextCompat.getColor(
                context,
                if (couponState == CouponState.INACTIVE) R.color.white else if (couponCode.isSelected) R.color.white else R.color.color_ACA1D3
            )
        )
    }

}