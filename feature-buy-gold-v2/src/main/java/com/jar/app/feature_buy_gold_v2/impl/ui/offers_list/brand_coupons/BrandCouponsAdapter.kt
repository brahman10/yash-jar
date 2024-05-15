package com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.brand_coupons

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.jar.app.base.util.dp
import com.jar.app.base.util.getDateShortMonthNameAndYear
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.databinding.FeatureOffersCellBrandCouponBinding
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.BrandsCouponInfo

class BrandCouponsAdapter(private val onCardClicked: (String) -> Unit) :
    PagingDataAdapter<BrandsCouponInfo, BrandCouponsAdapter.BrandCouponViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<BrandsCouponInfo>() {
            override fun areItemsTheSame(
                oldItem: BrandsCouponInfo,
                newItem: BrandsCouponInfo
            ): Boolean {
                return oldItem.brandCouponCodeId == newItem.brandCouponCodeId
            }

            override fun areContentsTheSame(
                oldItem: BrandsCouponInfo,
                newItem: BrandsCouponInfo
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BrandCouponViewHolder(
        FeatureOffersCellBrandCouponBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: BrandCouponViewHolder, position: Int) {
        getItem(position)?.let { brandsCouponInfo ->
            holder.onBind(brandsCouponInfo,onCardClicked)
        }
    }


    inner class BrandCouponViewHolder(
        private val binding: FeatureOffersCellBrandCouponBinding,
    ) :
        BaseViewHolder(binding.root) {
        init {
            adjustCardDimensions()
        }
        private fun adjustCardDimensions() {
            val cardView = binding.clRootContainer
            val layoutParams = cardView.layoutParams

            val screenWidth = cardView.context.resources.displayMetrics.widthPixels
            val cardWidth = screenWidth / 2 - 16.dp
            layoutParams.width = cardWidth
            layoutParams.height = cardWidth
            cardView.layoutParams = layoutParams
        }

        fun onBind(brandsCouponInfo: BrandsCouponInfo, onCardClicked: (String) -> Unit) {

            brandsCouponInfo.brandIconLink.let { iconLink ->
                Glide.with(binding.root.context)
                    .load(iconLink)
                    .override(40.dp)
                    .into(binding.ivBrandIcon)
                binding.tvBrandCouponDescription.text = brandsCouponInfo.title
                binding.tvBrandTitle.text = brandsCouponInfo.brandName
                binding.tvValidTill.text =
                    if (brandsCouponInfo.couponState == com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState.INACTIVE) "Expired" else
                        String.format(getCustomString(binding.root.context, MR.strings.coupon_validity),brandsCouponInfo.expiry?.getDateShortMonthNameAndYear())

                brandsCouponInfo.couponState?.let {couponState->
                    setLayoutEnabledState(
                        isEnabled = couponState,
                        binding
                    )
                }

            }


            binding.clRootContainer.setDebounceClickListener {
                brandsCouponInfo.brandCouponCodeId?.let { couponId->
                    if (brandsCouponInfo.couponState == com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState.ACTIVE) {
                        onCardClicked(couponId)
                    }
                }
            }


        }

        private fun setLayoutEnabledState(
            isEnabled: com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState,
            binding: FeatureOffersCellBrandCouponBinding,
        ) {

            when (isEnabled) {
                com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState.ACTIVE -> {
                    binding.apply {
                        ivBrandIcon.setColorFilter(
                            ContextCompat.getColor(
                                context,
                                android.R.color.transparent
                            )
                        )
                        tvBrandTitle.setTextColor(
                            ContextCompat.getColor(
                                context,
                                com.jar.app.core_ui.R.color.color_431860
                            )
                        )
                        tvBrandCouponDescription.setTextColor(
                            ContextCompat.getColor(
                                context,
                                com.jar.app.core_ui.R.color.color_431860
                            )
                        )
                        tvValidTill.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                com.jar.app.core_ui.R.color.color_3EAC93
                            )
                        )
                    }
                }

                com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState.INACTIVE -> {
                    binding.apply {


                        ivBrandIcon.setColorFilter(
                            ContextCompat.getColor(
                                context,
                                com.jar.app.core_ui.R.color.color_1A575757
                            )
                        )
                        tvBrandTitle.setTextColor(
                            ContextCompat.getColor(
                                context,
                                com.jar.app.core_ui.R.color.color_969696
                            )
                        )
                        tvBrandCouponDescription.setTextColor(
                            ContextCompat.getColor(
                                context,
                                com.jar.app.core_ui.R.color.color_575757
                            )
                        )
                        tvValidTill.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                com.jar.app.core_ui.R.color.color_969696
                            )
                        )
                    }
                    setImageToGrayScale(binding.ivBrandIcon)
                }
            }


        }

    }

    private fun setImageToGrayScale(imageView: ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(matrix)
        imageView.colorFilter = filter
    }



}
