package com.jar.app.feature_lending.impl.ui.personal_details.address.select_address

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.CellLendingAddressBinding
import com.jar.app.feature_user_api.domain.model.Address
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc

internal class LendingAddressAdapter(
    private val onAddressClicked: (address: Address) -> Unit,
    private val onEditAddressClicked: (address: Address) -> Unit
) : ListAdapter<Address, LendingAddressAdapter.LendingAddressViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Address>() {
            override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
                return oldItem == newItem
            }
        }
    }

    internal inner class LendingAddressViewHolder(
        private val binding: CellLendingAddressBinding,
        private val onAddressClicked: (address: Address) -> Unit,
        private val onEditAddressClicked: (address: Address) -> Unit
    ) : BaseViewHolder(binding.root) {

        var address: Address? = null

        init {
            binding.ivEditAddress.setDebounceClickListener {
                address?.let {
                    onEditAddressClicked.invoke(it)
                }
            }

            binding.root.setDebounceClickListener {
                address?.let {
                    onAddressClicked.invoke(it)
                }
            }
        }

        fun bind(data: Address) {
            this.address = data
            if (data.isSelected) {
                setRadioSelected(binding.ivRadio)
                binding.clRoot.setBackgroundResource(R.drawable.feature_lending_bg_address_selected)
            } else {
                setRadioUnselected(binding.ivRadio)
                binding.clRoot.setBackgroundResource(R.drawable.feature_lending_bg_address_unselected)
            }
            val string = getCustomString(data.getAddressCategory().addressCategory)
            binding.tvAddressTag.text = string
            binding.tvAddress.text = data.address
            binding.ivEditAddress.isVisible = data.isEditable
        }

        private fun setRadioSelected(imageView: ImageView) {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.feature_lending_ic_radio_selected
                )
            )
        }

        private fun setRadioUnselected(imageView: ImageView) {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.feature_lending_ic_radio_unselected
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LendingAddressViewHolder {
        val binding = CellLendingAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LendingAddressViewHolder(binding, onAddressClicked, onEditAddressClicked)
    }

    override fun onBindViewHolder(holder: LendingAddressViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}