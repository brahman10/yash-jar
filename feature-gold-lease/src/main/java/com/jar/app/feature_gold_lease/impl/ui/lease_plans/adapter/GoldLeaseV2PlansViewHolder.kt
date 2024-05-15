package com.jar.app.feature_gold_lease.impl.ui.lease_plans.adapter

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.base.util.getFormattedTextForOneStringValue
import com.jar.app.base.util.getFormattedTextForXStringValues
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.CellGoldLeasePlanBinding
import com.jar.app.feature_gold_lease.impl.domain.model.getStatusColor
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanCapacity
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanList
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanState
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey

internal class GoldLeaseV2PlansViewHolder(
    private val binding: CellGoldLeasePlanBinding,
    private val onInfoClicked: (leasePlanList: LeasePlanList) -> Unit,
    private val onSelectClicked: (leasePlanList: LeasePlanList) -> Unit,
    private val onRandomElementClicked: (elementName: String, data: String) -> Unit
): BaseViewHolder(binding.root) {
    private var leasePlanList: LeasePlanList? = null

    init {
        binding.btnSelect.setDebounceClickListener {
            leasePlanList?.let {
                if (it.getLeasePlanState() == LeasePlanState.ACTIVE) {
                    onSelectClicked.invoke(it)
                }
            }
        }

        binding.ivJewellerInfo.setDebounceClickListener {
            leasePlanList?.let {
                if (it.getLeasePlanState() == LeasePlanState.ACTIVE) {
                    onInfoClicked.invoke(it)
                }
            }
        }

        binding.ivJewellerIcon.setDebounceClickListener {
            leasePlanList?.let {
                onRandomElementClicked.invoke(GoldLeaseEventKey.Values.JEWELLER_LOGO, it.jewellerName.orEmpty())
            }
        }

        binding.tvJarBonusTag.setDebounceClickListener {
            leasePlanList?.let {
                onRandomElementClicked.invoke(GoldLeaseEventKey.Values.JAR_BONUS_TAG, it.bonusPercentage.orZero().toString())
            }
        }

        binding.tvSocialProofText.setDebounceClickListener {
            leasePlanList?.let {
                onRandomElementClicked.invoke(GoldLeaseEventKey.Values.GUARANTEED_TAG, it.socialProofComponent?.description.orEmpty())
            }
        }

        binding.tvLockInValue.setDebounceClickListener {
            leasePlanList?.let {
                onRandomElementClicked.invoke(GoldLeaseEventKey.Values.LOCK_IN_PERIOD, it.lockInComponent?.value.orEmpty())
            }
        }

        binding.tvMinimumValue.setDebounceClickListener {
            leasePlanList?.let {
                onRandomElementClicked.invoke(GoldLeaseEventKey.Values.MINIMUM_QUANTITY, it.minimumQuantityComponent?.value.orEmpty())
            }
        }

        binding.tvJewellerName.setDebounceClickListener {
            leasePlanList?.let {
                onRandomElementClicked.invoke(GoldLeaseEventKey.Values.JEWELLER_TITLE, it.jewellerName.orEmpty())
            }
        }

        binding.root.setDebounceClickListener {
            leasePlanList?.let {
                if (it.getLeasePlanState() == LeasePlanState.INACTIVE) {
                    onRandomElementClicked.invoke(GoldLeaseEventKey.Values.CLOSED_CARD, "")
                }
            }
        }
    }

    fun bind(data: LeasePlanList) {
        this.leasePlanList = data

        binding.clContent.alpha = if (data.getLeasePlanState() == LeasePlanState.ACTIVE) 1f else 0.5f
        binding.btnSelect.isInvisible = data.getLeasePlanState() != LeasePlanState.ACTIVE
        binding.ivFlash.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                if (data.getLeasePlanState() == LeasePlanState.INACTIVE || data.getLeasePlanCapacityStatus() == LeasePlanCapacity.CLOSED) R.drawable.feature_gold_lease_ic_closed else R.drawable.feature_gold_lease_ic_flash
            )
        )
        Glide.with(context).load(data.jewellerIcon.orEmpty()).into(binding.ivJewellerIcon)
        binding.tvJewellerName.setHtmlText(data.jewellerName.orEmpty())
        binding.tvJewellerEst.setHtmlText(data.jewellerEstablishedText.orEmpty())
        val earningsText = if (data.bonusPercentage.orZero() != 0.0f) {
            context.getFormattedTextForXStringValues(
                R.string.feature_gold_lease_x_earnings_plus_y_bonus,
                listOf(data.earningsPercentage.orZero().toString(), data.bonusPercentage.orZero().toString())
            )
        } else {
            context.getFormattedTextForOneStringValue(
                R.string.feature_gold_lease_x_earnings,
                data.earningsPercentage.orZero().toString()
            )
        }
        binding.tvEarningsPercent.text = earningsText
        binding.tvJarBonusTag.isVisible = data.bonusPercentage.orZero() != 0.0f
        binding.tvSocialProofText.isVisible = data.socialProofComponent != null
        binding.ivSocialProofIcon.isVisible = data.socialProofComponent != null
        Glide.with(context).load(data.socialProofComponent?.iconLink.orEmpty()).into(binding.ivSocialProofIcon)
        binding.tvSocialProofText.setHtmlText(data.socialProofComponent?.description.orEmpty())
        binding.tvEarningsTitle.setHtmlText(data.earningsTitle.orEmpty())
        binding.tvMinimumTitle.setHtmlText(data.minimumQuantityComponent?.title.orEmpty())
        binding.tvMinimumValue.text = context.getString(
            R.string.feature_gold_lease_x_gm_string,
            data.minimumQuantityComponent?.value.orEmpty()
        )
        binding.tvLockInTitle.setHtmlText(data.lockInComponent?.title.orEmpty())
        binding.tvLockInValue.text = context.getString(
            R.string.feature_gold_lease_x_days_string,
            data.lockInComponent?.value.orEmpty()
        )
        val capacityEnum = data.getLeasePlanCapacityStatus()
        binding.ivFlash.isVisible = capacityEnum != LeasePlanCapacity.NO_TAG
        binding.tvCapacity.isVisible = capacityEnum != LeasePlanCapacity.NO_TAG
        binding.ivFlash.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context, capacityEnum.getStatusColor()
            )
        )
        binding.tvCapacity.setTextColor(
            ContextCompat.getColor(
                context, capacityEnum.getStatusColor()
            )
        )
        binding.tvCapacity.setHtmlText(data.leasePlanCapacityDescription.orEmpty())
        binding.btnSelect.setText(data.ctaText.orEmpty())
        binding.clSelectStrip.isVisible =
            data.getLeasePlanState() == LeasePlanState.ACTIVE
                    || (data.getLeasePlanState() == LeasePlanState.INACTIVE && capacityEnum != LeasePlanCapacity.NO_TAG)
    }
}