package com.jar.app.feature_lending.impl.ui.repayments.overview

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingCellRepaymentCardBinding
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentCardType
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentDetail
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentStatus
import com.jar.app.feature_lending.impl.ui.common.KeyValueAdapter
import com.jar.app.feature_lending.shared.MR

internal class RepaymentEmiCardAdapter(
    private val onInitiatePayment: (amount: Float, title: String) -> Unit,
    private val onRefreshStatus: (orderId: String) -> Unit,
    private val onContactUs: (msg: String) -> Unit,
    private val loanId: String
) : ListAdapter<RepaymentDetail, RepaymentEmiCardAdapter.RepaymentCardViewHolder>(DIFF_UTIL) {
    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<RepaymentDetail>() {
            override fun areItemsTheSame(oldItem: RepaymentDetail, newItem: RepaymentDetail): Boolean {
                return oldItem.type == newItem.type
            }

            override fun areContentsTheSame(oldItem: RepaymentDetail, newItem: RepaymentDetail): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RepaymentCardViewHolder(
        FeatureLendingCellRepaymentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onInitiatePayment,
        onRefreshStatus,
        onContactUs,
        loanId
    )

    override fun onBindViewHolder(holder: RepaymentCardViewHolder, position: Int) {
        getItem(position)?.let { holder.setData(it) }
    }

    internal class RepaymentCardViewHolder(
        private val binding: FeatureLendingCellRepaymentCardBinding,
        private val onInitiatePayment: (amount: Float, title: String) -> Unit,
        private val onRefreshStatus: (orderId: String) -> Unit,
        private val onContactUs: (msg: String) -> Unit,
        private val loanId: String
    ) : BaseViewHolder(binding.root) {

        private var keyValueAdapter: KeyValueAdapter? = null
        private var repaymentData: RepaymentDetail? = null

        init {
            binding.btnAction.setDebounceClickListener {
                repaymentData?.emiSummary?.first()?.value?.toFloatOrNull()?.let { amount ->
                    onInitiatePayment(amount, repaymentData?.emiCount ?: "")
                }
            }

            keyValueAdapter = KeyValueAdapter(true)
            keyValueAdapter?.setColor(com.jar.app.core_ui.R.color.white, com.jar.app.core_ui.R.color.white)
            keyValueAdapter?.setFontSize(14f)
            binding.rvKeyValue.adapter = keyValueAdapter
        }

        fun setData(data: RepaymentDetail) {
            this.repaymentData = data
            var statusBgRes: Int? = null
            var statusTextColor: Int? = null
            var statusDrawable: Int? = null
            var statusDrawableTint: Int? = null
            var messageTextColor: Int? = null

            when (data.type) {
                RepaymentCardType.UPCOMING.name -> {
                    binding.btnAction.isVisible = false
                    binding.tvAutoDebitTitle.isVisible = true
                    binding.llAutoDebit.isVisible = true
                    statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1aebb46a_bg_4dp)
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EBB46A))
                    statusDrawable = com.jar.app.core_ui.R.drawable.core_ui_ic_calender_white
                    statusDrawableTint = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EBB46A))
                    messageTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_ACA1D3))
                }

                RepaymentCardType.FAILED.name -> {
                    binding.btnAction.isVisible = true
                    binding.tvAutoDebitTitle.isVisible = false
                    binding.llAutoDebit.isVisible = false
                    binding.tvMsg.setDebounceClickListener {
                        val msg = getCustomStringFormatted(MR.strings.feature_lending_repayment_contact_us_prefill_msg, repaymentData?.emiCount.orEmpty(), loanId)
                        onContactUs.invoke(msg)
                    }
                    when (data.paymentStatus) {
                        RepaymentStatus.PAYMENT_OVERDUE.name -> {
                            binding.ivStatus.updateLayoutParams {
                                height = 20.dp
                                width = 20.dp
                            }
                            binding.btnAction.setDisabled(false)
                            binding.btnAction.setText(getCustomStringFormatted(MR.strings.feature_lending_make_payment))
                            statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1aeb6a6e_bg_4dp_stroked)
                            statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EB6A6E))
                            statusDrawable = R.drawable.feature_lending_ic_alert_red_triangle
                        }
                        RepaymentStatus.PAYMENT_FAILED.name -> {
                            binding.ivStatus.updateLayoutParams {
                                height = 16.dp
                                width = 16.dp
                            }
                            binding.btnAction.setDisabled(false)
                            binding.btnAction.setText(context.getString(com.jar.app.core_ui.R.string.try_again))
                            statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1aeb6a6e_bg_4dp_stroked)
                            statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EB6A6E))
                            statusDrawable = com.jar.app.core_ui.R.drawable.core_ui_ic_red_cross
                        }
                        RepaymentStatus.PAYMENT_PENDING.name -> {
                            binding.ivStatus.updateLayoutParams {
                                height = 20.dp
                                width = 20.dp
                            }
                            binding.btnAction.setDisabled(true)
                            binding.btnAction.setText(context.getString(com.jar.app.core_ui.R.string.try_again))
                            statusBgRes = com.jar.app.core_ui.R.drawable.core_ui_round_1aebb46a_bg_4dp_stroked
                            statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_FFDA2D))
                            statusDrawable = R.drawable.feature_lending_ic_alert_yellow
                        }
                    }
                    binding.tvStatus.setTypeface(binding.tvStatus.typeface, Typeface.BOLD)
                    messageTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EEEAFF))
                }

                RepaymentCardType.PAID.name -> {
                    binding.ivStatus.updateLayoutParams {
                        height = 16.dp
                        width = 16.dp
                    }
                    binding.tvStatus.setTypeface(binding.tvStatus.typeface, Typeface.BOLD)
                    binding.btnAction.isVisible = false
                    binding.tvAutoDebitTitle.isVisible = false
                    binding.llAutoDebit.isVisible = false
                    statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1a1ea787_bg_4dp_stroked)
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_58DDC8))
                    statusDrawable = com.jar.app.core_ui.R.drawable.ic_tick_green
                    messageTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_ACA1D3))
                }

                RepaymentCardType.SCHEDULED.name -> {
                    binding.ivStatus.updateLayoutParams {
                        height = 12.dp
                        width = 12.dp
                    }
                    binding.tvStatus.setTypeface(binding.tvStatus.typeface, Typeface.NORMAL)
                    binding.btnAction.isVisible = false
                    binding.tvAutoDebitTitle.isVisible = true
                    binding.llAutoDebit.isVisible = true
                    statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1a1ea787_bg_4dp)
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_1ea787))
                    statusDrawable = com.jar.app.core_ui.R.drawable.core_ui_ic_calender_white
                    statusDrawableTint = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_1ea787))
                    messageTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EBB46A))
                }

                RepaymentCardType.PENDING.name -> {
                    binding.btnAction.isVisible = true
                    binding.ivStatus.updateLayoutParams {
                        height = 20.dp
                        width = 20.dp
                    }

                    binding.tvMsg.setDebounceClickListener {
                        val msg = getCustomStringFormatted(MR.strings.feature_lending_repayment_contact_us_prefill_msg_foreclsoure, loanId)
                        onContactUs.invoke(msg)
                    }

                    binding.btnAction.setButtonTextAllCaps(true)
                    binding.btnAction.setDebounceClickListener {
                        data.foreclosureOrderId?.let {
                            onRefreshStatus.invoke(it)
                        }
                    }
                    binding.btnAction.setText(context.getString(com.jar.app.core_ui.R.string.refresh))
                    statusBgRes = com.jar.app.core_ui.R.drawable.core_ui_round_1aebb46a_bg_4dp_stroked
                    statusTextColor = (ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_FFDA2D))
                    statusDrawable = R.drawable.feature_lending_ic_alert_yellow
                }
            }

            keyValueAdapter?.submitList(data.emiSummary)

            binding.tvEmi.text = data.emiCount
            binding.tvStatus.text = data.status

            statusBgRes?.let { binding.llStatus.setBackgroundResource(it) }
            statusTextColor?.let { binding.tvStatus.setTextColor(it) }
            statusDrawable?.let { binding.ivStatus.setImageResource(it) }
            statusDrawableTint?.let { binding.ivStatus.setColorFilter(it, android.graphics.PorterDuff.Mode.SRC_IN) }
            messageTextColor?.let { binding.tvMsg.setTextColor(it) }

            data.statusText?.let {
                binding.tvMsg.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } ?: kotlin.run {
                binding.tvMsg.isVisible = false
            }

            if (data.isCardDisabled.orFalse()) {
                binding.disableMaskView.isVisible = true
                binding.btnAction.setDebounceClickListener {
                    /**
                     * Another way to disable click
                     * If I disable button, it will have alpha, which looks odd, as entire card is already disabled in this case
                     */
                }
                binding.tvMsg.setDebounceClickListener {
                  //Do nothing
                }
            }
        }
    }
}