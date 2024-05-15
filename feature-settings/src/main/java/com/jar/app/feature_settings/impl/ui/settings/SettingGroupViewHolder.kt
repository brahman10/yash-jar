package com.jar.app.feature_settings.impl.ui.settings

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_settings.databinding.CellSettingsHeaderBinding
import com.jar.app.feature_settings.domain.model.SettingGroup

class SettingGroupViewHolder(private val binding: CellSettingsHeaderBinding) :
    BaseViewHolder(binding.root) {

    fun setGroup(settingGroup: SettingGroup) {
        binding.tvHeader.text = settingGroup.title
    }
}