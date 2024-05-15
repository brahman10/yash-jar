package com.jar.app.feature_contacts_sync_common.impl.ui.contact_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncAllowAccessBinding
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncEachContactCellBinding
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncItemHeaderBinding
import com.jar.app.feature_contact_sync_common.shared.domain.model.AllowAccessData
import com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContact
import com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContactHeader
import com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContactList

class ContactsSyncShowContactListAdapter(
    private val onClick: (contact: ServerContact) -> Unit,
    private val userContact: String?,
    private val onAllowAccess: (() -> Unit)? = null,
    private val onContactedSelected: (
        contact: ServerContact,
        toggleUI: (newSelectedState: Boolean) -> Unit,
        position: Int
    ) -> Unit,
    private val onLongPressed: (
        contact: ServerContact,
        toggleUI: (newSelectedState: Boolean) -> Unit,
        position: Int
    ) -> Unit,
    private val isAllSelected: ()->Boolean,
    private val isMultiSelectEnabled: () -> Boolean,
    private val isMultiInviteLayoutVisible: () -> Boolean,
    private val selectedContactMap :Map<String, ServerContact>


) :
    PagingDataAdapter<ServerContactList, RecyclerView.ViewHolder>(ITEM_CALLBACK) {


    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTACT = 1
        private const val TYPE_ALLOW_ACCESS = 2

        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<ServerContactList>() {
            override fun areItemsTheSame(
                oldItem: ServerContactList,
                newItem: ServerContactList
            ): Boolean {
                return if (oldItem is ServerContact && newItem is ServerContact)
                    oldItem.id == newItem.id
                else if (oldItem is ServerContactHeader && newItem is ServerContactHeader)
                    oldItem.header == newItem.header
                else
                    false
            }

            override fun areContentsTheSame(
                oldItem: ServerContactList,
                newItem: ServerContactList
            ): Boolean {
                return if (oldItem is ServerContact && newItem is ServerContact)
                    oldItem == newItem
                else if (oldItem is ServerContactHeader && newItem is ServerContactHeader)
                    oldItem == newItem
                else
                    false
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = FeatureContactsSyncItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ContactsSyncListHeaderViewHolder(binding)
            }
            TYPE_CONTACT -> {
                val binding = FeatureContactsSyncEachContactCellBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ContactsSyncShowContactListViewHolder(
                    binding,
                    onClick,
                    onContactedSelected,
                    onLongPressed,
                    isMultiSelectEnabled,
                    isAllSelected,
                    isMultiInviteLayoutVisible,
                    selectedContactMap
                )
            }
            TYPE_ALLOW_ACCESS -> {
                val binding = FeatureContactsSyncAllowAccessBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ContactsSyncShowAllowAccessViewHolder(binding)
            }
            else -> throw Exception("Unsupported Type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_HEADER -> {
                getItem(position)?.let {
                    (holder as ContactsSyncListHeaderViewHolder).onBindHeader(it as ServerContactHeader)
                }
            }
            TYPE_CONTACT -> {
                getItem(position)?.let {
                    (holder as ContactsSyncShowContactListViewHolder).onBind(it as ServerContact)
                }
            }
            TYPE_ALLOW_ACCESS -> {
                onAllowAccess?.let { (holder as ContactsSyncShowAllowAccessViewHolder).onBind(it) }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ServerContact -> TYPE_CONTACT
            is ServerContactHeader -> TYPE_HEADER
            is AllowAccessData -> TYPE_ALLOW_ACCESS
            else -> throw Exception("Unsupported Type")
        }
    }
    }