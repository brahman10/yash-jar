package com.jar.app.feature_contacts_sync_common.impl.ui.sent_invites

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.jar.app.base.util.asInitials
import com.jar.app.base.util.checkIfEpochTimeIsYesterday
import com.jar.app.base.util.getElapsedTimeInDays
import com.jar.app.base.util.getElapsedTimeInMonths
import com.jar.app.base.util.isInLastHour
import com.jar.app.base.util.millisToHoursMinutesSeconds
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_contacts_sync_common.R
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncEachContactCellBinding
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject
import java.util.concurrent.TimeUnit

internal class ContactsSyncSentInviteListViewHolder(
    val binding: FeatureContactsSyncEachContactCellBinding,
    private val onClick: (contact: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject) -> Unit,
) :
    BaseViewHolder(binding.root) {


    fun onBind(contact: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject) {
        if (contact.name.startsWith("+") || contact.name.getOrNull(0)?.isLetter() == false) {
            binding.tvContactInitials.background = ContextCompat.getDrawable(this.binding.root.context, R.drawable.feature_contacts_sync_common_avatar)
            binding.tvContactInitials.text = ""
        } else {
            binding.tvContactInitials.background = ContextCompat.getDrawable(this.binding.root.context, R.drawable.feature_contacts_sync_common_rounded_text_view)
            binding.tvContactInitials.text = contact.name.asInitials(skipSpecialChars = true)
        }
        binding.tvContactName.text = contact.name
        binding.tvInvite.text = binding.root.context.getString(R.string.feature_contacts_sync_common_invite)
        binding.tvInvited.text = binding.root.context.getString(R.string.feature_contacts_sync_common_invite)
        binding.tvCreateInvite.text = binding.root.context.getString(R.string.feature_contacts_sync_common_create_duo)
        binding.tvRequestSent.text = binding.root.context.getString(R.string.feature_contacts_sync_common_request_sent)

        val elapsedTime = contact.createdAt.getElapsedTimeInMonths()
        if (elapsedTime > 1) {
            binding.tvContactNumber.text = String.format(
                binding.root.resources.getString(R.string.feature_contacts_sync_common_invited_since_months),
                elapsedTime.toString()
            )
        } else {
            val elapsedTimeInDays = contact.createdAt.getElapsedTimeInDays().toInt()
            if (elapsedTimeInDays > 0) {
                binding.tvContactNumber.text = String.format(
                    binding.root.resources.getQuantityString(R.plurals.feature_contacts_sync_common_invited_since_days, elapsedTimeInDays, elapsedTimeInDays),
                    elapsedTimeInDays
                )
            } else if (checkIfEpochTimeIsYesterday(contact.createdAt)) {
                binding.tvContactNumber.text = binding.root.resources.getString(R.string.feature_contacts_sync_common_sent_yesterday)
            } else if (isInLastHour(contact.createdAt)) {
                binding.tvContactNumber.text = binding.root.resources.getString(R.string.feature_contacts_sync_common_invited_justnow)
            } else {
                binding.tvContactNumber.text = binding.root.resources.getString(R.string.feature_contacts_sync_common_invited_today)
            }
        }

        if (contact.remindedAt <= 0) {
            showRemindButton(contact)
        } else {
            setRemindTime(contact)
        }
    }

    private fun showRemindButton(contact: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject) {
        binding.tvRemind.isVisible = false
        binding.tvInvited.isVisible = true
        binding.tvInvited.text = binding.root.resources.getString(R.string.feature_contacts_sync_common_sent_invite_remind)
        binding.tvInvited.setDebounceClickListener {
            onClick.invoke(contact)
        }
    }

    private fun setRemindTime(contact: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject) {
        val currentTime = System.currentTimeMillis()
        val lastRemindedTime =
            contact.remindedAt // retrieve the last reminded time from SharedPreferences or any other storage
        val elapsedTime = currentTime - lastRemindedTime
        val timeInterval = TimeUnit.DAYS.toMillis(1)
        val remainingTime = timeInterval - elapsedTime
        if (remainingTime > 0) {
            binding.tvRemind.isVisible = true
            binding.tvInvited.isVisible = false
            val(hours,minutes) =  remainingTime.millisToHoursMinutesSeconds()
            val remindTime = "%02dh %02dm".format(hours, minutes)
            binding.tvRemind.text = String.format(
                binding.root.resources.getString(R.string.feature_contacts_sync_common_sent_invite_remaining_time),
                remindTime
            )
        } else {
            showRemindButton(contact)
        }
    }

}