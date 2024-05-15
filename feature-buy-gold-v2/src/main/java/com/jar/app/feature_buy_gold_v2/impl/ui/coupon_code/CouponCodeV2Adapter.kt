package com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_buy_gold_v2.databinding.CellCouponCodeBuyGoldV2Binding
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import kotlinx.coroutines.CoroutineScope

internal class CouponCodeV2Adapter(
    private val uiScope: CoroutineScope,
    private val onApplyClick: (couponCode: CouponCode, position: Int) -> Unit,
    private val onRemoveCouponClick: (couponCode: CouponCode, position: Int) -> Unit,
    private val onCouponExpired: (couponCode: CouponCode) -> Unit
): ListAdapter<CouponCode, CouponCodeV2ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CouponCode>() {
            override fun areItemsTheSame(oldItem: CouponCode, newItem: CouponCode): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CouponCode, newItem: CouponCode): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponCodeV2ViewHolder {
        val binding = CellCouponCodeBuyGoldV2Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CouponCodeV2ViewHolder(binding, uiScope, onApplyClick, onRemoveCouponClick, onCouponExpired)
    }

    override fun onBindViewHolder(holder: CouponCodeV2ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}