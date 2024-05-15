package com.jar.app.feature_gold_lease.impl.ui.new_lease_page

import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseUspBinding
import com.jar.app.feature_gold_lease.shared.domain.model.LeaseBasicInfoTile

internal class GoldLeaseOfferingsViewHolder(
    val binding: CellGoldLeaseUspBinding,
    private val onOfferingsClicked : (leaseBasicInfoTile: LeaseBasicInfoTile, position: Int) -> Unit
) : BaseViewHolder(binding.root) {

    private var leaseBasicInfoTile: LeaseBasicInfoTile? = null

    init {
        binding.root.setDebounceClickListener {
            leaseBasicInfoTile?.let {
                onOfferingsClicked.invoke(it, bindingAdapterPosition+1)
            }
        }
    }

    fun bind(data: LeaseBasicInfoTile) {
        this.leaseBasicInfoTile = data

        binding.tvTitle.text = data.description
        Glide.with(context).load(data.iconLink).into(binding.ivLogo)
    }
}