package com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.jar_coupons

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.dp
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.databinding.FeatureOffersCellJarCouponBinding
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_coupon_api.domain.model.OverlayType
import com.jar.app.feature_coupon_api.domain.model.jar_coupon.JarCouponInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.util.concurrent.TimeUnit
import com.jar.app.core_ui.R as R1

internal class  JarCouponsAdapter(  private val uiScope: CoroutineScope,private val onCardClicked: (JarCouponInfo) -> Unit) :
    ListAdapter<JarCouponInfo, JarCouponsAdapter.JarCouponsViewholder>(
        ITEM_CALLBACK
    ), BaseResources {

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<JarCouponInfo>() {
            override fun areItemsTheSame(oldItem: JarCouponInfo, newItem: JarCouponInfo): Boolean {
                return oldItem.couponCodeId == newItem.couponCodeId
            }

            override fun areContentsTheSame(
                oldItem: JarCouponInfo,
                newItem: JarCouponInfo
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JarCouponsViewholder {
        val binding = FeatureOffersCellJarCouponBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JarCouponsViewholder(binding, onCardClicked,uiScope)
    }

    override fun onBindViewHolder(holder: JarCouponsViewholder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it)
        }
    }

    inner class JarCouponsViewholder(
        private val binding: FeatureOffersCellJarCouponBinding,
        private val onCardClicked: (JarCouponInfo) -> Unit,
        private val uiScope: CoroutineScope,
    ) :
        BaseViewHolder(binding.root) {

        private var countDownJob: Job? = null
        fun onBind(jarCouponInfo: JarCouponInfo) {
            binding.tvCouponCode.text = jarCouponInfo.couponCode
            jarCouponInfo.title?.let {
                binding.tvCouponCodeTitle.text = it
            }
            binding.tvCouponCodeDescription.setHtmlText(jarCouponInfo.getCouponDescription())
            val background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor(jarCouponInfo.startColor ?: "#B85E99"),
                    Color.parseColor(jarCouponInfo.endColor ?: "#F4AC69"),
                )
            )
            background.cornerRadius = 16f.dp
            binding.clRootContainer.background = background
            binding.ivBackgroundOverlay.setImageResource(
                when (jarCouponInfo.getOverlayType()) {
                    OverlayType.CIRCLE.name -> {
                        R1.drawable.core_ui_bg_circle_overlay_single_cc
                    }

                    OverlayType.BACKWARD_INCLINE.name -> {
                        R1.drawable.core_ui_bg_backward_incline_overlay_single_cc
                    }

                    else -> {
                        R1.drawable.core_ui_bg_circle_overlay_single_cc
                    }
                }
            )
            if (jarCouponInfo.expiry != null) {
                binding.tvExpiryDate.isVisible = true
                setExpiryText(jarCouponInfo.expiry!!)
            } else {
                binding.tvExpiryDate.isVisible = false

            }
            binding.clRootContainer.setDebounceClickListener {
                onCardClicked(jarCouponInfo)
            }

            jarCouponInfo.iconLink?.let {icon->
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

        }

        private fun setExpiryText(expiryTimeStamp: Long) {
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
                    binding.tvExpiryDate.text = getCustomStringFormatted(context, MR.strings.feature_buy_gold_v2_expires_in_x_days, remainingDays)
                } else {
                    binding.tvExpiryDate.text = getCustomStringFormatted(context, MR.strings.feature_buy_gold_v2_expires_in_x_day, remainingDays)
                }
            }

        }
    }




}