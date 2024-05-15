package com.jar.app.feature_gold_lease.impl.ui.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseSubFaqBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseFaqIndividualObject

internal class GoldLeaseSubFaqAdapter :
    ListAdapter<GoldLeaseFaqIndividualObject, GoldLeaseSubFaqAdapter.GoldLeaseSubFaqViewHolder>(
        DIFF_UTIL
    ) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<GoldLeaseFaqIndividualObject>() {
            override fun areItemsTheSame(
                oldItem: GoldLeaseFaqIndividualObject,
                newItem: GoldLeaseFaqIndividualObject
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: GoldLeaseFaqIndividualObject,
                newItem: GoldLeaseFaqIndividualObject
            ): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }

    inner class GoldLeaseSubFaqViewHolder(
        private val binding: CellGoldLeaseSubFaqBinding
    ) : BaseViewHolder(binding.root) {
        fun bind(data: GoldLeaseFaqIndividualObject) {
            binding.tvTitle.text = data.title.orEmpty()
            binding.tvDescription.text = data.description.orEmpty()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoldLeaseSubFaqViewHolder {
        val binding = CellGoldLeaseSubFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GoldLeaseSubFaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoldLeaseSubFaqViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}