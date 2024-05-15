package com.jar.app.feature_gold_delivery.impl.ui.saved_address

import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_delivery.databinding.CellNewAddressBinding
import com.jar.app.feature_user_api.domain.model.Address

class NewAddressViewHolder(
    private val binding: CellNewAddressBinding,
    private val newAddressAdded: () -> Unit
) :
    BaseViewHolder(binding.root) {


    private var address: Address? = null

    init {
        binding.root.setDebounceClickListener {
            newAddressAdded()
        }
        binding.image.setDebounceClickListener {
            newAddressAdded()
        }
    }
}