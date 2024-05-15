package com.jar.app.feature_gold_lease.impl.ui.my_orders

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.base.util.getFormattedTextForOneStringValue
import com.jar.app.base.util.getFormattedTextForXStringValues
import com.jar.app.base.util.setHtmlText
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.CellUserLeaseBinding
import com.jar.app.feature_gold_lease.impl.domain.model.getStatusBg
import com.jar.app.feature_gold_lease.impl.domain.model.getStatusText
import com.jar.app.feature_gold_lease.impl.domain.model.getStatusTextColor
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2UserLeaseItem
import com.jar.app.feature_gold_lease.shared.domain.model.UserLeaseCosmetics

internal class UserLeaseViewHolder(
    private val binding: CellUserLeaseBinding,
    private val onLeaseClicked: (userLease: GoldLeaseV2UserLeaseItem) -> Unit
): BaseViewHolder(binding.root) {

    private var userLease: GoldLeaseV2UserLeaseItem? = null

    init {
        binding.root.setDebounceClickListener {
            userLease?.let {
                onLeaseClicked.invoke(it)
            }
        }
    }

    fun bind(data: GoldLeaseV2UserLeaseItem) {
        this.userLease = data

        Glide.with(context)
            .load(data.jewellerIcon.orEmpty())
            .into(binding.ivJewellerIcon)
        binding.tvJewellerName.setHtmlText(data.jewellerName.orEmpty())

        val status = data.getUserLeaseStatus()
        status?.let {
            binding.tvStatus.setText(it.getStatusText())
            binding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    context, it.getStatusTextColor()
                )
            )
            binding.tvStatus.setBackgroundResource(it.getStatusBg())
        }

        binding.tvLeasedGoldTitle.setHtmlText(data.leasedGoldComponent?.title.orEmpty())
        binding.tvLeasedGoldValue.setHtmlText(data.leasedGoldComponent?.value.orEmpty())

        binding.tvEarningsTitle.setHtmlText(data.earningsPercentageComponent?.title.orEmpty())
        val earningsText = if (data.jarBonusPercentage.orZero() != 0.0f) {
            context.getFormattedTextForXStringValues(
                R.string.feature_gold_lease_x_earnings_plus_y_bonus,
                listOf(data.earningsPercentageComponent?.value.orEmpty().toFloatOrZero().toString(), data.jarBonusPercentage.orZero().toString())
            )
        } else {
            context.getFormattedTextForOneStringValue(
                R.string.feature_gold_lease_x_earnings,
                data.earningsPercentageComponent?.value.orEmpty()
            )
        }
        binding.tvEarningsValue.text = earningsText

        binding.tvGoldXEarningsTitle.setHtmlText(data.earningsTillDateComponent?.title.orEmpty())
        binding.tvGoldXEarningsValue.setHtmlText(data.earningsTillDateComponent?.value.orEmpty())

        binding.ivSubIcon.isVisible = data.extraInformationComponent?.iconLink.isNullOrEmpty().not()
        Glide.with(context)
            .load(data.extraInformationComponent?.iconLink.orEmpty())
            .into(binding.ivSubIcon)
        binding.tvSubTitle.setHtmlText(data.extraInformationComponent?.description.orEmpty())

        binding.viewDimmer.alpha = if (data.getUserLeaseCosmetics() == UserLeaseCosmetics.TOP_HALF_BLUR) 0.5f else 0f
    }
}