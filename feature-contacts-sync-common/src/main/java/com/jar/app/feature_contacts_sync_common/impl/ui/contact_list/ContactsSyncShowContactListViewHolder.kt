package com.jar.app.feature_contacts_sync_common.impl.ui.contact_list


import android.view.View
import androidx.core.content.ContextCompat
import com.jar.app.base.util.*
import com.jar.app.core_ui.R.color
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_contacts_sync_common.R
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncEachContactCellBinding
import com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContact

internal class ContactsSyncShowContactListViewHolder(
    private val binding: FeatureContactsSyncEachContactCellBinding,
    private val onClick: (contact: ServerContact) -> Unit,
    private val onContactedSelected: (contact: ServerContact, toggleUI: (newSelectedState: Boolean) -> Unit, position: Int) -> Unit,
    private val onLongPressed: (contact: ServerContact, toggleUI: (newSelectedState: Boolean) -> Unit, position: Int) -> Unit,
    private val isMultiSelectEnabled: () -> Boolean,
    private val isAllSelected: () -> Boolean,
    private val isMultiInviteLayoutVisible: () -> Boolean,
    private val selectedContactMap: Map<String, ServerContact>
) :
    BaseViewHolder(binding.root) {

    fun onBind(contact: ServerContact) {
        if (contact.hasNoInitial()) {
            binding.tvContactInitials.background = ContextCompat.getDrawable(
                this.binding.root.context,
                R.drawable.feature_contacts_sync_common_avatar
            )
            binding.tvContactInitials.text = ""
        } else {
            binding.tvContactInitials.background = ContextCompat.getDrawable(
                this.binding.root.context,
                R.drawable.feature_contacts_sync_common_rounded_text_view
            )
            binding.tvContactInitials.text = contact.friendName.asInitials(skipSpecialChars = true)
        }
        binding.tvContactName.text = contact.friendName
        binding.tvContactNumber.text = contact.description
        binding.tvInvite.text =
            binding.root.context.getString(R.string.feature_contacts_sync_common_invite)
        binding.tvInvited.alpha = 1f

        if (isMultiSelectEnabled() || isAllSelected()) {
            binding.clRequestStatus.visibility = View.GONE
        } else {
            binding.clRequestStatus.visibility = View.VISIBLE
        }
        if (isMultiInviteLayoutVisible()) {
            hideInviteButton()
        } else {
            showInviteButton()
        }


        binding.tvInvited.text =
            binding.root.context.getString(R.string.feature_contacts_sync_common_invite)
        toggleSelectedState(contact)

        val toggleUI = { _: Boolean ->
            toggleSelectedState(contact)
        }


        binding.tvInvited.setDebounceClickListener {
            binding.tvInvited.text =
                binding.root.resources.getString(R.string.feature_contacts_sync_common_invited)
            binding.tvInvited.alpha = 0.4f
            onClick.invoke(contact)
        }

        binding.root.setOnLongClickListener {
            onLongPressed.invoke(contact, toggleUI, bindingAdapterPosition)
            return@setOnLongClickListener true
        }

        binding.root.setOnClickListener {

            if (isMultiSelectEnabled() || isAllSelected()) {
                onContactedSelected.invoke(contact, toggleUI, bindingAdapterPosition)
            }
        }
        if (isAllSelected().not()) {
            if (selectedContactMap.containsKey(contact.id)) {
                showSelectedState()
            } else {
                showUnSelectedState()
            }
        }
    }

    private fun addToSelectedList(
        selectedContactMap: MutableMap<String, ServerContact>,
        contact: ServerContact
    ) {
        if (selectedContactMap.containsKey(contact.id)) {
            selectedContactMap.remove(contact.id)
        } else {
            selectedContactMap[contact.id] = contact
        }
    }

    private fun toggleSelectedState(contact: ServerContact) {
        if (contact.isSelected.orFalse()) {
            showSelectedState()
        } else {
            showUnSelectedState()
        }
    }

    private fun showUnSelectedState() {
        binding.ivContactInitials.visibility = View.GONE
        binding.tvContactInitials.visibility = View.VISIBLE
        binding.root.setBackgroundColor(
            ContextCompat.getColor(
                binding.root.context,
                color.transparent
            )
        )
    }

    private fun showSelectedState() {
        binding.ivContactInitials.visibility = View.VISIBLE
        binding.tvContactInitials.visibility = View.GONE
        binding.root.setBackgroundColor(
            ContextCompat.getColor(
                binding.root.context,
                color.color_1A1EA787
            )
        )
    }

    fun hideInviteButton() {
        binding.clRequestStatus.visibility = View.GONE
    }

    fun showInviteButton() {
        binding.clRequestStatus.visibility = View.VISIBLE
    }
}
