package com.jar.app.feature_user_api.impl.ui.saved_address

import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_user_api.databinding.CellUserSavedAddressBinding
import com.jar.app.feature_user_api.domain.model.Address

internal class UserSavedAddressViewHolder(
    private val binding: CellUserSavedAddressBinding,
    private val onSavedAddressClick: (address: Address) -> Unit,
    private val onDeleteClick: (address: Address) -> Unit,
    private val onEditClick: (address: Address) -> Unit
) :
    BaseViewHolder(binding.root) {


    private var address: Address? = null

    init {
        binding.root.setDebounceClickListener {
            address?.let(onSavedAddressClick)
        }
        binding.btnDelete.setDebounceClickListener {
            address?.let(onDeleteClick)
            binding.root.alpha = 0.5f
            binding.root.isEnabled = false
        }
        binding.btnEdit.setDebounceClickListener {
            address?.let(onEditClick)
        }
    }

    fun setAddress(address: Address) {
        this.address = address
        binding.tvAddress.text = address.address
    }
}