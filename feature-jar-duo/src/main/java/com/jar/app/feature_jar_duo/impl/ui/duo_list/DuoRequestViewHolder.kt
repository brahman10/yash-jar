package com.jar.app.feature_jar_duo.impl.ui.duo_list

import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoDuoRequestCellBinding
import com.jar.app.feature_jar_duo.shared.domain.model.InvitationStage
import com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData

internal class DuoRequestViewHolder(
    private val binding: FeatureDuoDuoRequestCellBinding,
    private val onClick: (contact: com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData, invitationStage: com.jar.app.feature_jar_duo.shared.domain.model.InvitationStage) -> Unit
) : BaseViewHolder(binding.root) {

    fun onBind(contact: com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData) {
        binding.tvContactInitials.text = contact.name?.asInitials()
        binding.tvContactName.text = contact.name
        val (diffInDays, diffInMonths) = contact.userOnboardedTime.orZero().convertEpochTime()
        when {
            diffInMonths >= 1 -> {
                binding.tvContactNumber.text = if (diffInMonths == 1L) String.format(
                    binding.root.resources.getString(R.string.feature_duo_saving_since_month),
                    1
                ) else String.format(
                    binding.root.resources.getString(R.string.feature_duo_saving_since_months),
                    diffInMonths
                )
            }
            else -> {
                binding.tvContactNumber.text = if (diffInDays == 1L) String.format(
                    binding.root.resources.getString(R.string.feature_duo_saving_since_day),
                    1
                ) else String.format(
                    binding.root.resources.getString(R.string.feature_duo_saving_since_days),
                    diffInDays
                )
            }
        }

        binding.tvAccept.setText(binding.root.context.getString(R.string.feature_duo_accept))
        binding.tvAccept.setDebounceClickListener {
            onClick.invoke(contact, com.jar.app.feature_jar_duo.shared.domain.model.InvitationStage.ACCEPTED)
        }
        binding.ivClose.setDebounceClickListener {
            onClick.invoke(contact, com.jar.app.feature_jar_duo.shared.domain.model.InvitationStage.REJECTED)
        }

    }

}