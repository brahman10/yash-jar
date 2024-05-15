package com.jar.app.feature_gold_delivery.impl.ui.saved_address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_delivery.databinding.CellNewAddressBinding
import com.jar.app.feature_user_api.domain.model.Address

class NewAddressAdapter(
    private val addNewAddress: () -> Unit,
) : ListAdapter<Address, NewAddressViewHolder>(ITEM_CALLBACK) {

    companion object {
        private val ITEM_CALLBACK = object  : DiffUtil.ItemCallback<Address>() {
            override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
                return oldItem.addressId == newItem.addressId
            }

            override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAddressViewHolder {
        val binding =
            CellNewAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewAddressViewHolder(binding, addNewAddress)
    }

    override fun onBindViewHolder(holder: NewAddressViewHolder, position: Int) {
    }
}