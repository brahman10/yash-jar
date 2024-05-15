package com.jar.app.feature_gold_delivery.impl.ui.saved_address

import androidx.core.view.isVisible
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_delivery.databinding.CellSavedAddressBinding
import com.jar.app.feature_user_api.domain.model.Address

class SavedAddressViewHolder(
    private val binding: CellSavedAddressBinding,
    private val onSavedAddressClick: (index: Int) -> Unit,
    private val onEditClick: (address: Address) -> Unit,
    private val isRadioChecked: (index: Int) -> Boolean
) :
    BaseViewHolder(binding.root) {


    private var address: Address? = null

    init {
        binding.root.setDebounceClickListener {
            onSavedAddressClick(absoluteAdapterPosition)
        }
        binding.radioCheck.setDebounceClickListener {
            onSavedAddressClick(absoluteAdapterPosition)
        }
        binding.btnEdit.setDebounceClickListener {
            address?.let(onEditClick)
        }
    }

    fun setAddress(address: Address) {
        this.address = address
        binding.nameTv.text = address.name
        binding.tvNameAddress.text = address.phoneNumber
        address.addressCategory?.let {
            binding.categoryTv.text = it
            binding.categoryTv.isVisible = true
        } ?: run {
            binding.categoryTv.isVisible = false
        }
        binding.tvAddress.text = address.address
        binding.radioCheck.isChecked = isRadioChecked(absoluteAdapterPosition)
    }
}