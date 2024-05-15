package com.jar.app.feature.notification_list.ui

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.CellNotificationBinding
import com.jar.app.feature_in_app_notification.shared.domain.model.Notification

class NotificationViewHolder(
    private val binding: CellNotificationBinding,
    private val onNotificationClicked: (Notification) -> Unit
) :
    BaseViewHolder(binding.root) {

    private var notification: Notification? = null

    init {
        binding.root.setDebounceClickListener {
            notification?.let { onNotificationClicked(it) }
        }
    }

    fun setNotification(notification: Notification) {
        this.notification = notification

        binding.clContent.setBackgroundColor(
            ContextCompat.getColor(
                binding.root.context,
                if (notification.seen.orFalse()) com.jar.app.core_ui.R.color.bgColor else com.jar.app.core_ui.R.color.lightBgColor
            )
        )

        binding.rightArrow.isVisible = notification.deepLink.isNullOrEmpty().not()

        binding.rightArrow.setColorFilter(
            ContextCompat.getColor(
                context,
                if (notification.seen.orFalse()) com.jar.app.core_ui.R.color.color_ACA1D3 else com.jar.app.core_ui.R.color.color_776E94
            )
        )

        binding.tvDescription.isSelected = notification.seen != true
        binding.clImageContainer.isSelected = notification.seen != true
        binding.unreadDotView.setBackgroundColor(
            ContextCompat.getColor(
                binding.root.context,
                if (notification.seen.orFalse()) com.jar.app.core_ui.R.color.bgColor else com.jar.app.core_ui.R.color.color_789BDE
            )
        )
        binding.tvDescription.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                if (notification.seen.orFalse()) com.jar.app.core_ui.R.color.color_ACA1D3 else com.jar.app.core_ui.R.color.color_EEEAFF
            )
        )
        binding.tvTitle.text = notification.title
        binding.tvDescription.text = notification.description

        Glide.with(binding.root)
            .load(notification.icon)
            .placeholder(com.jar.app.feature_transaction_common.R.drawable.feature_transaction_ic_gift_box_colored)
            .into(binding.sivNotification)

        binding.tvDate.text = notification.createdAt
    }
}