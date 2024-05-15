package com.jar.app.feature_transaction.impl.ui.gold.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellTransactionWinningBinding
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_transactions_common.shared.CommonTransactionValueType

class TransactionListAdapter(
    private val onTransactionSelected: (transactionData: TransactionData) -> Unit
) : PagingDataAdapter<TransactionData, TransactionListAdapter.NewTransactionVH>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionData>() {
            override fun areItemsTheSame(
                oldItem: TransactionData,
                newItem: TransactionData
            ): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(
                oldItem: TransactionData,
                newItem: TransactionData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewTransactionVH {
        val binding =
            FeatureTransactionCellTransactionWinningBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NewTransactionVH(binding)
    }

    override fun onBindViewHolder(holder: NewTransactionVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class NewTransactionVH(
        private val binding: FeatureTransactionCellTransactionWinningBinding
    ) : BaseViewHolder(binding.root) {

        private var transactionData: TransactionData? = null

        init {
            binding.root.setDebounceClickListener {
                transactionData?.let {
                    onTransactionSelected(it)
                }
            }
        }

        fun bindData(data: TransactionData) {
            transactionData = data
            Glide.with(itemView.context)
                .load(data.iconLink)
                .into((binding.ivTransaction))
            binding.tvTitle.text = data.title
            binding.tvQuantity.text = data.subTitle

            val value = when (data.getValueType()) {
                CommonTransactionValueType.AMOUNT -> {
                    itemView.context.getString(
                        R.string.feature_transaction_rs_value,
                        data.amount.orZero()
                    )
                }
                CommonTransactionValueType.VOLUME -> {
                    getCustomStringFormatted(
                        itemView.context,
                        MR.strings.feature_buy_gold_v2_x_gm,
                        data.volume.orZero()
                    )
                }
                CommonTransactionValueType.AMOUNT_AND_VOLUME -> {
                    itemView.context.getString(
                        R.string.feature_transaction_f_amount_and_f_volume,
                        data.amount.orZero(), data.volume.orZero()
                    )
                }
            }
            binding.tvAmount.text = value
            binding.tvStatus.text = data.currentStatus
            binding.tvStatus.setTextColor(data.getColorForStatus().getColor(context))
            binding.tvDate.text = data.date
        }
    }
}