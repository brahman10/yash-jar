package com.jar.app.feature_homepage.impl.ui.detected_spends_info.partial_payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellPartialPaymentBinding
import com.jar.app.feature_user_api.domain.model.PartPaymentOption

internal class PartialPaymentAdapter(
    private val totalAmount: Float,
    private val onClick: (partPaymentOption: PartPaymentOption) -> Unit
) : ListAdapter<PartPaymentOption, PartialPaymentOptionViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PartPaymentOption>() {
            override fun areItemsTheSame(oldItem: PartPaymentOption, newItem: PartPaymentOption): Boolean {
                return oldItem.amount == newItem.amount
            }

            override fun areContentsTheSame(oldItem: PartPaymentOption, newItem: PartPaymentOption): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartialPaymentOptionViewHolder {
        val binding = FeatureHomepageCellPartialPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PartialPaymentOptionViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: PartialPaymentOptionViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setOption(totalAmount, it)
        }
    }

}