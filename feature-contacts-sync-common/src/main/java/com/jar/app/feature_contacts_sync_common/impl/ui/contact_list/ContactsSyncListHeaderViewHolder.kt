package com.jar.app.feature_contacts_sync_common.impl.ui.contact_list

import androidx.recyclerview.widget.RecyclerView
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncAllowAccessBinding
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncItemHeaderBinding
import com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContactHeader

internal class ContactsSyncListHeaderViewHolder(private val binding: FeatureContactsSyncItemHeaderBinding) :
    BaseViewHolder(binding.root) {

    fun onBindHeader(header: com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContactHeader) {
        binding.tvHeader.text = header.header
    }
}

class ContactsSyncShowAllowAccessViewHolder(private val binding: FeatureContactsSyncAllowAccessBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(onClick: () -> Unit) {
        binding.btnAllowAccess.setDebounceClickListener {
            onClick()
        }
    }
}