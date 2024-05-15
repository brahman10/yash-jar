package com.jar.app.feature_gifting.impl.ui.suggested_amount

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gifting.databinding.FeatureGiftingCellSuggestedAmountBinding
import com.jar.app.feature_user_api.domain.model.SuggestedAmount

internal class SuggestedAmountAdapter(
    private val onSuggestedAmountClick: (suggestedAmount: SuggestedAmount) -> Unit
) :
    ListAdapter<SuggestedAmount, SuggestedAmountViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SuggestedAmount>() {
            override fun areItemsTheSame(
                oldItem: SuggestedAmount,
                newItem: SuggestedAmount
            ): Boolean {
                return oldItem.amount == newItem.amount
            }

            override fun areContentsTheSame(
                oldItem: SuggestedAmount,
                newItem: SuggestedAmount
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestedAmountViewHolder {
        val binding = FeatureGiftingCellSuggestedAmountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SuggestedAmountViewHolder(binding, onSuggestedAmountClick)
    }

    override fun onBindViewHolder(holder: SuggestedAmountViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setSuggestedAmount(it)
        }
    }

}