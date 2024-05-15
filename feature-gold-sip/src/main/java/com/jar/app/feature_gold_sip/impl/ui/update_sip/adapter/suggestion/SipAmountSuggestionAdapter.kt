package com.jar.app.feature_gold_sip.impl.ui.update_sip.adapter.suggestion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellSuggestedAmountBinding
import com.jar.app.feature_user_api.domain.model.SuggestedAmount

internal class SipAmountSuggestionAdapter(
    private val onItemClick: (SuggestedAmount, Int) -> Unit
) : ListAdapter<SuggestedAmount, SipAmountSuggestionViewHolder>(DIFF_UTIL) {

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<SuggestedAmount>() {
            override fun areItemsTheSame(
                oldItem: SuggestedAmount, newItem: SuggestedAmount
            ): Boolean {
                return oldItem.amount == newItem.amount
            }

            override fun areContentsTheSame(
                oldItem: SuggestedAmount, newItem: SuggestedAmount
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SipAmountSuggestionViewHolder(
            FeatureGoldSipCellSuggestedAmountBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onItemClick
        )

    override fun onBindViewHolder(holder: SipAmountSuggestionViewHolder, position: Int) {
        getItem(position)?.let { holder.setSuggestedAmount(it) }
    }
}