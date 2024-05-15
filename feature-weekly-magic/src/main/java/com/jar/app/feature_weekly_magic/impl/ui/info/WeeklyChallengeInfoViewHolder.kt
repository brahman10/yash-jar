package com.jar.app.feature_weekly_magic.impl.ui.info

import com.example.feature_weekly_magic.databinding.CellWeeklyNotificationTextBinding
import com.jar.app.core_ui.view_holder.BaseViewHolder

class WeeklyChallengeInfoViewHolder(private val binding: CellWeeklyNotificationTextBinding) :
    BaseViewHolder(binding.root) {

    fun setData(message: String) {
        binding.tvMessage.text = message
    }
}