package com.jar.app.feature_spends_tracker.impl.ui.main_page

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_spends_tracker.databinding.FeatureSpendsTransactionCellBinding
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_transaction_data.SpendsTransactionData

internal class SpendsTransactionsAdapter(
    private val onReportClicked: (SpendsTransactionData) -> Unit,
    private val onTransactionClicked: () -> Unit,
    private val onItemBind: (position: Int) -> Unit
) :
    PagingDataAdapter<SpendsTransactionData, SpendsTransactionsAdapter.SpendsTransactionViewHolder>(
        DIFF_UTIL
    ) {
    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<SpendsTransactionData>() {
            override fun areItemsTheSame(
                oldItem: SpendsTransactionData,
                newItem: SpendsTransactionData
            ): Boolean {
                return oldItem.txnId == newItem.txnId
            }

            override fun areContentsTheSame(
                oldItem: SpendsTransactionData,
                newItem: SpendsTransactionData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SpendsTransactionViewHolder(
        FeatureSpendsTransactionCellBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SpendsTransactionViewHolder, position: Int) {
        getItem(position)?.let { spendsTransactionData ->
            onItemBind.invoke(position)
            holder.onBind(spendsTransactionData, onReportClicked, onTransactionClicked)
        }
    }


    inner class SpendsTransactionViewHolder(
        private val binding: FeatureSpendsTransactionCellBinding,
    ) :
        BaseViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(
            spendsTransactionData: SpendsTransactionData,
            onReportClicked: (SpendsTransactionData) -> Unit,
            onTransactionClicked: () -> Unit
        ) {
            binding.root.setDebounceClickListener {
                onTransactionClicked()
            }
            Glide.with(binding.root.context)
                .load(spendsTransactionData.spendsIcon)
                .override(46.dp, 51.dp)
                .into(binding.ivTransactionIcon)

            Glide.with(binding.root.context)
                .load(spendsTransactionData.reportFlagIcon)
                .override(25.dp)
                .into(binding.ivReportFlag)
            binding.apply {
                tvSpendsTodayLabel.text = spendsTransactionData.header
                tvTransactionDateTime.text =
                    "${spendsTransactionData.txnDate} | ${spendsTransactionData.txnTime}"
                tvTransactionBeneficiary.text =
                    "${spendsTransactionData.paidToText} : ${spendsTransactionData.beneDetails}"
                tvAmountSpent.text = "-${spendsTransactionData.amount}"

                ivReportFlag.setDebounceClickListener {
                    onReportClicked(spendsTransactionData)
                }

            }
        }
    }
}