package com.jar.app.feature_contacts_sync_common.impl.ui.sent_invites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncAllowAccessBinding
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncEachContactCellBinding
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncItemHeaderBinding
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject
import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteHeader
import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList
import com.jar.app.feature_contacts_sync_common.impl.ui.contact_list.ContactsSyncShowAllowAccessViewHolder

internal class ContactsSyncSentListAdapter(val onClick: (contact: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject) -> Unit) :
    PagingDataAdapter<com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList, RecyclerView.ViewHolder>(ITEM_CALLBACK) {


    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTACT = 1
        private const val TYPE_ALLOW_ACCESS = 2

        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList>() {
            override fun areItemsTheSame(
                oldItem: com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList,
                newItem: com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList
            ): Boolean {
                return if (oldItem is com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject && newItem is com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject)
                    oldItem.inviteeId == newItem.inviteeId
                else if (oldItem is com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteHeader && newItem is com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteHeader)
                    oldItem.header == newItem.header
                else
                    false
            }

            override fun areContentsTheSame(
                oldItem: com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList,
                newItem: com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList
            ): Boolean {
                return oldItem == newItem
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
                ContactsSyncSentInviteListHeaderViewHolder(binding)
            }
            TYPE_CONTACT -> {
                val binding = FeatureContactsSyncEachContactCellBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ContactsSyncSentInviteListViewHolder(
                    binding,
                    onClick,
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
                    (holder as ContactsSyncSentInviteListHeaderViewHolder).onBindHeader(it as com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteHeader)
                }
            }
            TYPE_CONTACT -> {
                getItem(position)?.let {
                    (holder as ContactsSyncSentInviteListViewHolder).onBind(it as com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject)
                }
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject -> TYPE_CONTACT
            is com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteHeader -> TYPE_HEADER
            // is AllowAccessData -> TYPE_ALLOW_ACCESS
            else -> throw Exception("Unsupported Type")
        }
    }
}