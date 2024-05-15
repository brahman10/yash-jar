package com.jar.app.feature_gold_lease.impl.ui.new_lease_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseUspBinding
import com.jar.app.feature_gold_lease.shared.domain.model.LeaseBasicInfoTile

internal class GoldLeaseOfferingsAdapter(
    private val onOfferingsClicked : (leaseBasicInfoTile: LeaseBasicInfoTile, position: Int) -> Unit
) : ListAdapter<LeaseBasicInfoTile, GoldLeaseOfferingsViewHolder>(
    DIFF_UTIL
) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LeaseBasicInfoTile>() {
            override fun areItemsTheSame(
                oldItem: LeaseBasicInfoTile,
                newItem: LeaseBasicInfoTile
            ): Boolean {
                return oldItem.iconLink == newItem.iconLink
            }

            override fun areContentsTheSame(
                oldItem: LeaseBasicInfoTile,
                newItem: LeaseBasicInfoTile
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoldLeaseOfferingsViewHolder {
        val binding = CellGoldLeaseUspBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GoldLeaseOfferingsViewHolder(binding, onOfferingsClicked)
    }

    override fun onBindViewHolder(holder: GoldLeaseOfferingsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}