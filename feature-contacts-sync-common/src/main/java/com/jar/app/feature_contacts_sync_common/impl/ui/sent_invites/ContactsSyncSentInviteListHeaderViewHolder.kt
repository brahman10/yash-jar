package com.jar.app.feature_contacts_sync_common.impl.ui.sent_invites

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncItemHeaderBinding
import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteHeader


internal class ContactsSyncSentInviteListHeaderViewHolder(private val binding: FeatureContactsSyncItemHeaderBinding) :
    BaseViewHolder(binding.root) {

    fun onBindHeader(header: com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteHeader) {
        binding.tvHeader.text = header.header
    }
}