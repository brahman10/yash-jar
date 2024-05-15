package com.jar.app.feature_buy_gold_v2.impl.ui.payment_option_bottom_sheet

import android.content.pm.PackageManager
import android.view.LayoutInflater
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.databinding.CellBuyGoldPaymentMethodBinding
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldUpiApp

internal class BuyGoldPaymentOptionAdapter(
    private val onPaymentMethodSelected: (buyGoldUpiApp: BuyGoldUpiApp) -> Unit
): ListAdapter<BuyGoldUpiApp, BuyGoldPaymentOptionAdapter.BuyGoldPaymentOptionViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object: DiffUtil.ItemCallback<BuyGoldUpiApp>() {
            override fun areItemsTheSame(oldItem: BuyGoldUpiApp, newItem: BuyGoldUpiApp): Boolean {
                return oldItem.payerApp == newItem.payerApp
            }
            override fun areContentsTheSame(
                oldItem: BuyGoldUpiApp,
                newItem: BuyGoldUpiApp
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class BuyGoldPaymentOptionViewHolder(
        private val binding: CellBuyGoldPaymentMethodBinding,
        private val onPaymentMethodSelected: (buyGoldUpiApp: BuyGoldUpiApp) -> Unit
    ): BaseViewHolder(binding.root) {
        private var buyGoldUpiApp: BuyGoldUpiApp? = null

        init {
            binding.root.setDebounceClickListener {
                buyGoldUpiApp?.let {
                    onPaymentMethodSelected.invoke(it)
                }
            }
        }

        fun bind(data: BuyGoldUpiApp) {
            this.buyGoldUpiApp = data
            binding.ivSelected.setImageResource(
                if (data.isSelected) com.jar.app.core_ui.R.drawable.core_ui_bg_radio_selected_v2
                else com.jar.app.core_ui.R.drawable.core_ui_bg_radio_unselected_v2
            )
            val packageManager = context.applicationContext.packageManager
            val icon =
                packageManager.getApplicationIcon(data.payerApp)
            Glide.with(itemView)
                .load(icon)
                .transform(RoundedCorners(12.dp))
                .into(binding.ivIcon)
            binding.tvPaymentType.text = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    data.payerApp,
                    PackageManager.GET_META_DATA
                )
            )
        }

        fun updateSelectedPaymentMethod(isSelected: Boolean) {
            binding.ivSelected.setImageResource(
                if (isSelected) com.jar.app.core_ui.R.drawable.core_ui_bg_radio_selected_v2
                else com.jar.app.core_ui.R.drawable.core_ui_bg_radio_unselected_v2
            )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BuyGoldPaymentOptionViewHolder {
        val binding = CellBuyGoldPaymentMethodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BuyGoldPaymentOptionViewHolder(binding, onPaymentMethodSelected)
    }

    override fun onBindViewHolder(
        holder: BuyGoldPaymentOptionViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            for (selectedPaymentMethods in payloads) {
                if ((selectedPaymentMethods as BuyGoldUpiApp).isSelected) {
                    holder.updateSelectedPaymentMethod(true)
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: BuyGoldPaymentOptionViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}