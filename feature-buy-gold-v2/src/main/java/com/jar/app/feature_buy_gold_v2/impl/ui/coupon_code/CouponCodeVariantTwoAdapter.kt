package com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_buy_gold_v2.databinding.CellCouponCodeVariant2Binding
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import kotlinx.coroutines.CoroutineScope
internal class CouponCodeVariantTwoAdapter(
    private val uiScope: CoroutineScope,
    private val onApplyClick: (couponCode: CouponCode, position: Int,screenName:String) -> Unit,
    private val onRemoveCouponClick: (couponCode: CouponCode, position: Int) -> Unit,
    private val onCouponExpired: (couponCode: CouponCode) -> Unit,
    private val getCurrentAmount: () -> Float
): ListAdapter<CouponCode, CouponCodeVariantTwoVH>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CouponCode>() {
            override fun areItemsTheSame(oldItem: CouponCode, newItem: CouponCode): Boolean {
                return oldItem.couponCode == newItem.couponCode
            }

            override fun areContentsTheSame(oldItem: CouponCode, newItem: CouponCode): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponCodeVariantTwoVH {
        val binding = CellCouponCodeVariant2Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CouponCodeVariantTwoVH(
            binding,
            uiScope,
            onApplyClick,
            onRemoveCouponClick,
            onCouponExpired,
            getCurrentAmount
        )
    }

    override fun onBindViewHolder(holder: CouponCodeVariantTwoVH, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}