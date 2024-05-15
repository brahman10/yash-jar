package com.jar.app.feature.notification_list.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.jar.app.databinding.CellNotificationBinding
import com.jar.app.feature_in_app_notification.shared.domain.model.Notification

class NotificationAdapter(private val onNotificationClicked: (Notification) -> Unit) :
    PagingDataAdapter<Notification, NotificationViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            CellNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding, onNotificationClicked)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setNotification(it)
        }
    }

}