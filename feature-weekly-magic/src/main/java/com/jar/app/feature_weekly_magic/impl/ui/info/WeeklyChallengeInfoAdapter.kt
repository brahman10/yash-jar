package com.jar.app.feature_weekly_magic.impl.ui.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.feature_weekly_magic.databinding.CellWeeklyNotificationTextBinding

internal class WeeklyChallengeInfoAdapter :
    ListAdapter<String, WeeklyChallengeInfoViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeeklyChallengeInfoViewHolder {
        val binding =
            CellWeeklyNotificationTextBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return WeeklyChallengeInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyChallengeInfoViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setData(it)
        }
    }

}