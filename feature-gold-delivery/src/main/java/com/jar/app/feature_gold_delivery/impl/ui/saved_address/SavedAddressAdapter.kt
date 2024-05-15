package com.jar.app.feature_gold_delivery.impl.ui.saved_address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_delivery.databinding.CellSavedAddressBinding
import com.jar.app.feature_user_api.domain.model.Address

class SavedAddressAdapter(
    private val onSavedAddressClick: (position: Int) -> Unit,
    private val onEditClick: (address: Address) -> Unit,
    private val isRadioChecked: (int: Int) -> Boolean,
) : ListAdapter<Address, SavedAddressViewHolder>(ITEM_CALLBACK) {

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<Address>() {
            override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
                return oldItem.addressId == newItem.addressId
            }

            override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedAddressViewHolder {
        val binding =
            CellSavedAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedAddressViewHolder(binding, onSavedAddressClick, onEditClick, isRadioChecked)
    }

    override fun onBindViewHolder(holder: SavedAddressViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setAddress(it)
        }
    }
}