package com.jar.android.feature_post_setup.impl.ui.setup_details.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellSettingsBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders.SettingViewHolder
import com.jar.app.feature_post_setup.domain.model.PostSetupPageItem
import com.jar.app.feature_post_setup.domain.model.SettingPageItem
import com.jar.app.feature_post_setup.domain.model.setting.PostSetupQuickActionItem

internal class SettingsAdapterDelegate(
    private val onItemClick: (PostSetupQuickActionItem) -> Unit
) : AdapterDelegate<List<PostSetupPageItem>>() {

    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is SettingPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeaturePostSetupCellSettingsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SettingViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is SettingViewHolder && item is SettingPageItem)
            holder.setSettingsData(item)
    }
}