package com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders

import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.view.isVisible
import com.jar.android.feature_post_setup.R
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellStateAmountInfoBinding
import com.jar.android.feature_post_setup.impl.model.CalendarDayStatus
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.feature_post_setup.domain.model.calendar.StateInfoDetails
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_post_setup.domain.model.calendar.AmountInfo
import com.jar.app.feature_post_setup.shared.PostSetupMR

class StateAmountInfoViewHolder(
    private val binding: FeaturePostSetupCellStateAmountInfoBinding,
    private val onPaymentClick: (AmountInfo) -> Unit,
) : BaseViewHolder(binding.root) {

    fun setData(stateInfoDetails: StateInfoDetails) {
        val summaryDate = stateInfoDetails.yearAndMonthText.split(" ")
        if (summaryDate.size > 1)
            binding.tvSavingSummaryTitle.text = getCustomStringFormatted(
                PostSetupMR.strings.feature_post_setup_transaction_summary_s,
                (summaryDate[0] + "'" + summaryDate[1].drop(2))
            )
        else
            binding.tvSavingSummaryTitle.text = getCustomStringFormatted(
                PostSetupMR.strings.feature_post_setup_transaction_summary_s,
                stateInfoDetails.yearAndMonthText
            )
        binding.clSuccessDetailsContainer.isVisible =
            stateInfoDetails.successInfo != null
        binding.clPendingDetailsContainer.isVisible =
            stateInfoDetails.pendingInfo != null
        binding.clFailureDetailsContainer.isVisible =
            stateInfoDetails.failureInfo != null

        binding.tvSavingSummaryTitle.isVisible =
            binding.clSuccessDetailsContainer.isVisible || binding.clPendingDetailsContainer.isVisible || binding.clFailureDetailsContainer.isVisible

        stateInfoDetails.successInfo?.let { setupSuccessSummary(it) }
        stateInfoDetails.failureInfo?.let { setupFailureSummary(it) }
        stateInfoDetails.pendingInfo?.let { setupPendingSummary(it) }
    }

    private fun setupSuccessSummary(amountInfo: AmountInfo) {
        toggleSuccessBackground(amountInfo.shouldShowShimmer)
        if (amountInfo.shouldShowShimmer) {
            binding.shimmerSuccess.startShimmer()
            amountInfo.shouldShowShimmer = false
        } else {
            binding.shimmerSuccess.stopShimmer()
        }
        val successString = SpannableStringBuilder()
            .append(getCustomString(CalendarDayStatus.SUCCESS.stringRes!!))
            .append(" ")
            .bold {
                append(
                    getCustomPluralFormatted(
                        binding.root.context,
                        PostSetupMR.plurals.feature_post_setup_n_days,
                        amountInfo.noOfDays
                    )
                )
            }
        binding.tvSuccessTitle.text = successString
        binding.tvSuccessAmount.text =
            binding.root.context.getString(com.jar.app.core_ui.R.string.core_ui_rs_symbol) + amountInfo.amount.toInt()
                .getFormattedAmount()
    }

    private fun setupFailureSummary(amountInfo: AmountInfo) {
        toggleFailureBackground(amountInfo.shouldShowShimmer)
        if (amountInfo.shouldShowShimmer) {
            binding.shimmerFailure.startShimmer()
            amountInfo.shouldShowShimmer = false
        } else {
            binding.shimmerFailure.stopShimmer()
        }
        val failureString = SpannableStringBuilder()
            .append(getCustomString(CalendarDayStatus.FAILED.stringRes!!))
            .append(" ")
            .bold {
                append(
                    getCustomPluralFormatted(
                        binding.root.context,
                        PostSetupMR.plurals.feature_post_setup_n_days,
                        amountInfo.noOfDays
                    )
                )
            }
        binding.tvFailureTitle.text = failureString
        binding.tvFailureAmount.text =
            binding.root.context.getString(com.jar.app.core_ui.R.string.core_ui_rs_symbol) + amountInfo.amount.toInt()
                .getFormattedAmount()
        binding.btnSaveNow.setDebounceClickListener {
            onPaymentClick.invoke(amountInfo)
        }
    }

    private fun setupPendingSummary(amountInfo: AmountInfo) {
        togglePendingBackground(amountInfo.shouldShowShimmer)
        if (amountInfo.shouldShowShimmer) {
            binding.shimmerPending.startShimmer()
            amountInfo.shouldShowShimmer = false
        } else {
            binding.shimmerPending.stopShimmer()
        }
        val pendingString = SpannableStringBuilder()
            .append(getCustomString(CalendarDayStatus.PENDING.stringRes!!))
            .append(" ")
            .bold {
                append(
                    getCustomPluralFormatted(
                        binding.root.context,
                        PostSetupMR.plurals.feature_post_setup_n_days,
                        amountInfo.noOfDays
                    )
                )
            }
        binding.tvPendingTitle.text = pendingString
        binding.tvPendingAmount.text =
            binding.root.context.getString(com.jar.app.core_ui.R.string.core_ui_rs_symbol) + amountInfo.amount.toInt()
                .getFormattedAmount()
    }

    private fun toggleSuccessBackground(isSelected: Boolean) {
        binding.clSuccessDetailsContainer.background = ContextCompat.getDrawable(
            binding.root.context,
            if (isSelected) R.drawable.feature_post_setup_bg_amount_info_selected else R.drawable.feature_post_setup_bg_filled_4dp_2e2942
        )
    }

    private fun togglePendingBackground(isSelected: Boolean) {
        binding.clPendingDetailsContainer.background = ContextCompat.getDrawable(
            binding.root.context,
            if (isSelected) R.drawable.feature_post_setup_bg_amount_info_selected else R.drawable.feature_post_setup_bg_filled_4dp_2e2942
        )
    }

    private fun toggleFailureBackground(isSelected: Boolean) {
        binding.clFailureDetailsContainer.background = ContextCompat.getDrawable(
            binding.root.context,
            if (isSelected) R.drawable.feature_post_setup_bg_amount_info_selected else R.drawable.feature_post_setup_bg_filled_4dp_2e2942
        )
    }
}