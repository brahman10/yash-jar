package com.jar.app.feature_settings.impl.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_settings.databinding.CellNotificationSwitchBinding
import com.jar.app.feature_settings.domain.model.NotificationSettingsSwitch

internal class NotificationSettingsAdapter(
    private val onClick: (switch: SwitchCompat, settingIdentifier: Int) -> Unit
) : ListAdapter<NotificationSettingsSwitch, NotificationSettingsAdapter.NotificationSwitchViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<NotificationSettingsSwitch>() {
            override fun areItemsTheSame(oldItem: NotificationSettingsSwitch, newItem: NotificationSettingsSwitch): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NotificationSettingsSwitch, newItem: NotificationSettingsSwitch): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationSwitchViewHolder {
        val binding =
            CellNotificationSwitchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationSwitchViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: NotificationSwitchViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setSetting(it)
        }
    }

    inner class NotificationSwitchViewHolder(
        private val binding: CellNotificationSwitchBinding,
        private val onClick: (switch: SwitchCompat, settingIdentifier: Int) -> Unit
    ) :
        BaseViewHolder(binding.root) {

        init {
            binding.root.setDebounceClickListener {
                settingSwitch?.let {
                    onClick.invoke(binding.switchEnd, it.position)
                }
            }
        }

        private var settingSwitch: NotificationSettingsSwitch? = null

        fun setSetting(settingSwitch: NotificationSettingsSwitch) {
            this.settingSwitch = settingSwitch
            binding.tvTitle.setText(settingSwitch.title.resourceId)
            binding.tvDesc.isVisible = settingSwitch.desc != null
            if (settingSwitch.desc != null) {
                binding.tvDesc.setText(settingSwitch.desc!!.resourceId)
            }
            binding.switchEnd.isChecked = settingSwitch.isEnabled
        }
    }
}