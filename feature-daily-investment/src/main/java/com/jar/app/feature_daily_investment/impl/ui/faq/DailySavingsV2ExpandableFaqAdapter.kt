package com.jar.app.feature_daily_investment.impl.ui.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.databinding.ExpandableFaqRvLayoutBinding
import com.jar.app.feature_daily_investment.shared.domain.model.GenericFAQs

internal class DailySavingsV2ExpandableFaqAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<GenericFAQs, DailySavingsV2ExpandableFaqViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GenericFAQs>() {
            override fun areItemsTheSame(oldItem: GenericFAQs, newItem: GenericFAQs): Boolean {
                return oldItem.question == newItem.question
            }

            override fun areContentsTheSame(oldItem: GenericFAQs, newItem: GenericFAQs): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailySavingsV2ExpandableFaqViewHolder {
        val binding =
            ExpandableFaqRvLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailySavingsV2ExpandableFaqViewHolder(binding,onItemClick)
    }

    override fun onBindViewHolder(holder: DailySavingsV2ExpandableFaqViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}