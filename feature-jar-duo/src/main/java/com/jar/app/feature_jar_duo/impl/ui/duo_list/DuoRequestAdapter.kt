package com.jar.app.feature_jar_duo.impl.ui.duo_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_jar_duo.databinding.FeatureDuoDuoRequestCellBinding
import com.jar.app.feature_jar_duo.shared.domain.model.InvitationStage
import com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData

internal class DuoRequestAdapter(
    val fromScreen: String? = null,
    private val onViewAllClicked: () -> Unit = { },
    private val onClick: (contact: com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData, invitationStage: com.jar.app.feature_jar_duo.shared.domain.model.InvitationStage) -> Unit
) :
    ListAdapter<com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData, DuoRequestViewHolder>(ITEM_CALLBACK) {

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData>() {
            override fun areItemsTheSame(
                oldItem: com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData,
                newItem: com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData
            ): Boolean {
                return oldItem.inviterId == newItem.inviterId
            }

            override fun areContentsTheSame(
                oldItem: com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData,
                newItem: com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuoRequestViewHolder {
        val binding = FeatureDuoDuoRequestCellBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DuoRequestViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: DuoRequestViewHolder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it)
        }
    }
}