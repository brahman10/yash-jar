package com.jar.app.feature_transaction.impl.ui.new_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellTitleValuePairBinding
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.NewTransactionTitleValuePair
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.OrderDetailsCardRowCosmetics

internal class TransactionTitleValuePairAdapter(
    private val onClickedCopyTransactionId : (transactionTitleValuePair: NewTransactionTitleValuePair) -> Unit
) :
    ListAdapter<NewTransactionTitleValuePair, TransactionTitleValuePairAdapter.TransactionTitleValuePairViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<NewTransactionTitleValuePair>() {
            override fun areItemsTheSame(
                oldItem: NewTransactionTitleValuePair,
                newItem: NewTransactionTitleValuePair
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: NewTransactionTitleValuePair,
                newItem: NewTransactionTitleValuePair
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class TransactionTitleValuePairViewHolder(
        private val binding: FeatureTransactionCellTitleValuePairBinding,
        private val onClickedCopyTransactionId : (transactionTitleValuePair: NewTransactionTitleValuePair) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var transactionTitleValuePair: NewTransactionTitleValuePair? = null

        init {
            binding.root.setDebounceClickListener {
                transactionTitleValuePair?.let {
                    if (it.getRowCosmetics() == OrderDetailsCardRowCosmetics.TXN_ID) {
                        onClickedCopyTransactionId.invoke(it)
                    }
                }
            }
        }

        fun bind(data: NewTransactionTitleValuePair) {
            this.transactionTitleValuePair = data

            binding.tvTitle.setHtmlText(data.title.orEmpty())
            binding.tvValue.setHtmlText(data.value.orEmpty())

            when(data.getRowCosmetics()) {
                OrderDetailsCardRowCosmetics.HIGHLIGHTED -> {
                    binding.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.color_58DDC8
                        )
                    )
                    binding.tvValue.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.color_58DDC8
                        )
                    )
                    binding.root.setBackgroundResource(R.drawable.feature_transaction_bg_rounded_8_3c3357)
                    binding.root.setPadding(8.dp)
                    binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                }
                OrderDetailsCardRowCosmetics.TXN_ID -> {
                    binding.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.color_D5CDF2
                        )
                    )
                    binding.tvValue.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.white
                        )
                    )
                    binding.root.setBackgroundColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.transparent
                        )
                    )
                    binding.root.setPadding(0.dp)
                    binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0,0, com.jar.app.core_ui.R.drawable.ic_copy_small,0)
                    binding.tvValue.text = data.maskTransactionId(data.value.orEmpty())
                }
                OrderDetailsCardRowCosmetics.WEBSITE -> {
                    binding.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.color_D5CDF2
                        )
                    )
                    binding.tvValue.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.white
                        )
                    )
                    binding.root.setBackgroundColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.transparent
                        )
                    )
                    binding.root.setPadding(0.dp)
                    binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                    binding.tvTitle.setHtmlText("<u>${data.value.orEmpty()}</u>")
                }
                else -> {
                    binding.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.color_D5CDF2
                        )
                    )
                    binding.tvValue.setTextColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.white
                        )
                    )
                    binding.root.setBackgroundColor(
                        ContextCompat.getColor(
                            context, com.jar.app.core_ui.R.color.transparent
                        )
                    )
                    binding.root.setPadding(0.dp)
                    binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionTitleValuePairViewHolder {
        val binding = FeatureTransactionCellTitleValuePairBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionTitleValuePairViewHolder(binding, onClickedCopyTransactionId)
    }

    override fun onBindViewHolder(holder: TransactionTitleValuePairViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}