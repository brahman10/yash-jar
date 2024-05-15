package com.jar.app.feature_lending_kyc.impl.ui.faq.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_lending_kyc.shared.domain.model.Faq
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycCellFaqDetailBinding

internal class LendingFaqDetailAdapter :
    ListAdapter<Faq, LendingFaqDetailViewHolder>(DIFF_UTIL) {

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Faq>() {
            override fun areItemsTheSame(
                oldItem: Faq, newItem: Faq
            ): Boolean {
                return oldItem.question == newItem.question
            }

            override fun areContentsTheSame(
                oldItem: Faq, newItem: Faq
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LendingFaqDetailViewHolder(
        FeatureLendingKycCellFaqDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: LendingFaqDetailViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setLendingFaqDetail(it)
        }
    }
}