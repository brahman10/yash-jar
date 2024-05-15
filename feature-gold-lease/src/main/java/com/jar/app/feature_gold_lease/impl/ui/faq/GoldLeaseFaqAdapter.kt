package com.jar.app.feature_gold_lease.impl.ui.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseFaqBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseFaqObjects

internal class GoldLeaseFaqAdapter(
    private val onFaqClicked: (faqData: GoldLeaseFaqObjects) -> Unit
) : ListAdapter<GoldLeaseFaqObjects, GoldLeaseFaqViewHolder>(DIFF_UTIL){

    companion object {
        private val DIFF_UTIL = object: DiffUtil.ItemCallback<GoldLeaseFaqObjects>() {
            override fun areItemsTheSame(
                oldItem: GoldLeaseFaqObjects,
                newItem: GoldLeaseFaqObjects
            ): Boolean {
                return newItem == oldItem
            }

            override fun areContentsTheSame(
                oldItem: GoldLeaseFaqObjects,
                newItem: GoldLeaseFaqObjects
            ): Boolean {
                return newItem == oldItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoldLeaseFaqViewHolder {
        val binding = CellGoldLeaseFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GoldLeaseFaqViewHolder(binding, onFaqClicked)
    }

    override fun onBindViewHolder(holder: GoldLeaseFaqViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}