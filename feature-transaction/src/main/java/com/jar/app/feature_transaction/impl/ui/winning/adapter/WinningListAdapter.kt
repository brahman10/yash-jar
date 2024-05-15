package com.jar.app.feature_transaction.impl.ui.winning.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.jar.app.base.util.orTrue
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellTransactionWinningBinding
import com.jar.app.feature_transaction.shared.domain.model.WinningData

class WinningListAdapter(
    private val onClickListener: (winningData: WinningData) -> Unit
) : PagingDataAdapter<WinningData, WinningListAdapter.WinningListVH>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WinningData>() {
            override fun areItemsTheSame(oldItem: WinningData, newItem: WinningData): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(oldItem: WinningData, newItem: WinningData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WinningListVH {
        val binding =
            FeatureTransactionCellTransactionWinningBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return WinningListVH(binding)
    }

    override fun onBindViewHolder(holder: WinningListVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class WinningListVH(
        private val binding: FeatureTransactionCellTransactionWinningBinding
    ) : BaseViewHolder(binding.root) {

        private var winningData: WinningData? = null

        init {
            binding.root.setDebounceClickListener {
                winningData?.let {
                    if (it.isEnabled.orTrue()) {
                        onClickListener(it)
                    }
                }
            }
        }

        fun bindData(data: WinningData) {
            winningData = data
            /*Glide.with(itemView.context)
                .load(data.iconLink)
                .into(())*/
            binding.ivTransaction.setImageResource(R.drawable.feature_transaction_ic_winning)
            binding.tvTitle.text = data.title
            binding.tvQuantity.text = data.subTitle
            binding.tvAmount.text =
                itemView.context.getString(
                    R.string.feature_transaction_rs_value,
                    data.amount.orZero()
                )
            binding.tvStatus.text = data.status
            binding.tvStatus.setTextColor(data.getColorForStatus().getColor(context))
            binding.tvDate.text = data.date
            binding.root.alpha = if (data.isEnabled.orTrue()) 1f else 0.5f
        }
    }
}