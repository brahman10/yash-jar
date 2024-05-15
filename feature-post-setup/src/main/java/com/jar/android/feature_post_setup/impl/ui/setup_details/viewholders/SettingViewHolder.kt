package com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders

import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellSettingsBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.adapter.PostSetupQuickActionAdapter
import com.jar.app.base.ui.BaseResources
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_post_setup.domain.model.SettingPageItem
import com.jar.app.feature_post_setup.domain.model.setting.PostSetupQuickActionItem

internal class SettingViewHolder(
    private val binding: FeaturePostSetupCellSettingsBinding,
    private val onItemClick: (PostSetupQuickActionItem) -> Unit
) : BaseViewHolder(binding.root), BaseResources {

    private var adapter: PostSetupQuickActionAdapter? = null
    fun setSettingsData(settingPageItem: SettingPageItem) {
        binding.tvTitle.text = getCustomString(settingPageItem.titleRes)

        adapter = PostSetupQuickActionAdapter { onItemClick.invoke(it) }
        binding.rvQuickActions.adapter = adapter
        adapter?.submitList(settingPageItem.postSetupQuickActionList.postSetupQuickActionList)
    }
}