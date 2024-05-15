package com.jar.app.feature_homepage.impl.ui.payment_prompt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellManualPaymentAmountSuggestionBinding
import com.jar.app.feature_user_api.domain.model.SuggestedAmount

internal class ManualPaymentAmountSuggestionAdapter(
    private val onSelected: (view: View, position: Int) -> Unit
) : ListAdapter<SuggestedAmount, ManualPaymentAmountSuggestionAdapter.ManualPaymentAmountSuggestionViewHolder>(
    DIFF_CALLBACK
) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SuggestedAmount>() {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManualPaymentAmountSuggestionViewHolder {
        val binding =
            FeatureHomepageCellManualPaymentAmountSuggestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ManualPaymentAmountSuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ManualPaymentAmountSuggestionViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class ManualPaymentAmountSuggestionViewHolder(
        private val binding: FeatureHomepageCellManualPaymentAmountSuggestionBinding
    ) : BaseViewHolder(binding.root) {

        init {
            binding.root.setDebounceClickListener {
                onSelected.invoke(it, bindingAdapterPosition)
            }
        }

        fun bindData(data: SuggestedAmount) {
            binding.tvAmount.text = binding.root.context.getString(
                R.string.feature_homepage_rupee_x_in_int, data.amount.toInt()
            )
        }
    }
}