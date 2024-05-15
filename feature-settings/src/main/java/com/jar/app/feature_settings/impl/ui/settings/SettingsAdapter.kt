package com.jar.app.feature_settings.impl.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_settings.R
import com.jar.app.feature_settings.databinding.CellSettingSeparatorBinding
import com.jar.app.feature_settings.databinding.CellSettingsBinding
import com.jar.app.feature_settings.databinding.CellSettingsHeaderBinding
import com.jar.app.feature_settings.domain.model.Setting
import com.jar.app.feature_settings.domain.model.SettingGroup
import com.jar.app.feature_settings.domain.model.SettingSeparator
import com.jar.app.feature_settings.domain.model.Settings

internal class SettingsAdapter(private val onItemClick: (Settings) -> Unit) :
    ListAdapter<Settings, RecyclerView.ViewHolder>(DIFF_UTIL) {

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Settings>() {
            override fun areItemsTheSame(oldItem: Settings, newItem: Settings): Boolean {
                return oldItem.position == newItem.position
            }

            override fun areContentsTheSame(oldItem: Settings, newItem: Settings): Boolean {
                return if (oldItem is Setting && newItem is Setting) {
                    oldItem == newItem
                } else if (oldItem is SettingGroup && newItem is SettingGroup) {
                    oldItem == newItem
                } else if (oldItem is SettingSeparator && newItem is SettingSeparator) {
                    oldItem == newItem
                } else {
                    oldItem == newItem
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.cell_settings_header -> {
                SettingGroupViewHolder(
                    CellSettingsHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            R.layout.cell_settings -> {
                SettingsViewHolder(
                    CellSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    onItemClick
                )
            }

            else -> {
                SettingSeparatorViewHolder(
                    CellSettingSeparatorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder) {
                is SettingGroupViewHolder -> {
                    holder.setGroup(it as SettingGroup)
                }

                is SettingsViewHolder -> {
                    holder.bindSetting(it as Setting)
                }

                is SettingSeparatorViewHolder -> {
                    holder.setSeparator()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SettingGroup -> {
                R.layout.cell_settings_header
            }

            is Setting -> {
                R.layout.cell_settings
            }

            else -> {
                R.layout.cell_setting_separator
            }
        }
    }
}