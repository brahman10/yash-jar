package com.jar.app.feature_user_api.impl.ui.saved_address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_user_api.databinding.CellUserSavedAddressBinding
import com.jar.app.feature_user_api.domain.model.Address

internal class UserSavedAddressAdapter(
    private val onSavedAddressClick: (address: Address) -> Unit,
    private val onDeleteClick: (address: Address) -> Unit,
    private val onEditClick: (address: Address) -> Unit
) : ListAdapter<Address, UserSavedAddressViewHolder>(ITEM_CALLBACK) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSavedAddressViewHolder {
        val binding =
            CellUserSavedAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserSavedAddressViewHolder(binding, onSavedAddressClick, onDeleteClick, onEditClick)
    }

    override fun onBindViewHolder(holder: UserSavedAddressViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setAddress(it)
        }
    }
}