package com.jar.app.feature_settings.impl.ui.settings

import android.graphics.Color
import android.util.Log
import androidx.core.view.isVisible
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_settings.databinding.CellSettingsBinding
import com.jar.app.feature_settings.domain.model.Setting

internal class SettingsViewHolder(
    private val binding: CellSettingsBinding,
    private val onItemClick: (Setting) -> Unit
) : BaseViewHolder(binding.root) {

    init {
        binding.root.setDebounceClickListener {
            setting?.let {
                onItemClick.invoke(it)
            }
        }
    }
    private var setting: Setting? = null

    fun bindSetting(setting: Setting) {
        this.setting = setting
        binding.root.setPlotlineViewTag(tag = setting.viewTag)
        binding.tvTitle.setPlotlineViewTag(tag = "${setting.viewTag}_Title")
        binding.ivIconEnd.setPlotlineViewTag(tag = "${setting.viewTag}_CTA")

        binding.ivIconStart.setImageDrawable(setting.startIconRes.getDrawable(context))
        binding.tvTitle.text = setting.title

        settingDescription(setting)
    }

    private fun settingDescription(setting: Setting) {
        if (setting.descIcon == null) {
            binding.tvDesc.isVisible = true
            binding.ivDesIcon.isVisible = false
            binding.tvDesc.text = setting.desc
            binding.tvDesc.setTextColor(setting.descColor?.getColor(context) ?: Color.WHITE)
        } else {
            binding.tvDesc.isVisible = false
            binding.ivDesIcon.isVisible = true
            binding.ivDesIcon.setImageDrawable(setting.descIcon!!.getDrawable(context))
        }
    }
}