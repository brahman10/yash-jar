package com.jar.app.feature_lending.impl.ui.agreement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.databinding.CellChargesBinding
import com.jar.app.feature_lending.shared.domain.model.v2.ReadyCashChargesDescription

internal class LoanBreakdownInfoAdapter : ListAdapter<ReadyCashChargesDescription, LoanBreakdownInfoAdapter.LendingBreakdownInfoViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<ReadyCashChargesDescription>() {
            override fun areItemsTheSame(oldItem: ReadyCashChargesDescription, newItem: ReadyCashChargesDescription): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ReadyCashChargesDescription, newItem: ReadyCashChargesDescription): Boolean {
                return oldItem == newItem
            }
        }
    }

    internal inner class LendingBreakdownInfoViewHolder(
        private val binding: CellChargesBinding
    ) : BaseViewHolder(binding.root) {

        fun bind(data: ReadyCashChargesDescription) {
            binding.tvDesc.text = HtmlCompat.fromHtml(data.description.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.tvTitle.text = HtmlCompat.fromHtml(data.heading.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.tvSubTitle.text = HtmlCompat.fromHtml(data.subHeading.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LendingBreakdownInfoViewHolder {
        val binding = CellChargesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LendingBreakdownInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LendingBreakdownInfoViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}