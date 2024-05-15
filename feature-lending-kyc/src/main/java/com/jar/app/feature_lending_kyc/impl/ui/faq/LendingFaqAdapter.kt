package com.jar.app.feature_lending_kyc.impl.ui.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycCellFaqBinding
import com.jar.app.feature_lending_kyc.shared.domain.model.FaqTitleAndType

internal class LendingFaqAdapter(
    private val onClick: (faqTitleAndType: FaqTitleAndType) -> Unit
) : ListAdapter<FaqTitleAndType, LendingFaqViewHolder>(DIFF_UTIL) {

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<FaqTitleAndType>() {
            override fun areItemsTheSame(oldItem: FaqTitleAndType, newItem: FaqTitleAndType): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: FaqTitleAndType, newItem: FaqTitleAndType): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LendingFaqViewHolder(
        FeatureLendingKycCellFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun onBindViewHolder(holder: LendingFaqViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setFaq(it)
        }
    }
}