package com.jar.app.feature_transaction.impl.ui.new_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionNewTransactionRoutineBinding
import com.jar.app.feature_transaction.impl.domain.model.getStatusBackground
import com.jar.app.feature_transaction.impl.domain.model.getStatusColor
import com.jar.app.feature_transactions_common.shared.NewTransactionRoutine
import com.jar.app.feature_transactions_common.shared.NewTransactionRoutineStatus

class NewTransactionRoutineAdapter(
    private val onCtaClicked: (newTransactionRoutine: NewTransactionRoutine) -> Unit
) : ListAdapter<NewTransactionRoutine, NewTransactionRoutineAdapter.NewTransactionRoutineViewHolder>(
    DIFF_UTIL
) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<NewTransactionRoutine>() {
            override fun areItemsTheSame(
                oldItem: NewTransactionRoutine,
                newItem: NewTransactionRoutine
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: NewTransactionRoutine,
                newItem: NewTransactionRoutine
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class NewTransactionRoutineViewHolder(
        private val binding: FeatureTransactionNewTransactionRoutineBinding,
        private val onCtaClicked: (newTransactionRoutine: NewTransactionRoutine) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var newTransactionRoutine: NewTransactionRoutine? = null

        init {
            binding.btnCta.setDebounceClickListener {
                newTransactionRoutine?.let {
                    onCtaClicked.invoke(it)
                }
            }
        }

        fun bind(data: NewTransactionRoutine) {
            this.newTransactionRoutine = data
            binding.divider.isVisible = false

            binding.tvTxnRoutineTitle.setHtmlText(data.title.orEmpty())
            binding.tvTxnRoutineDate.setHtmlText(data.value.orEmpty())
            binding.tvTxnRoutineDate.isVisible = data.value.orEmpty().isNotEmpty()

            val status = data.getTxnRoutineStatus()
            status.let {
                binding.tvTxnRoutineStatus.isVisible = data.statusText != null
                binding.tvTxnRoutineStatus.setTextColor(
                    ContextCompat.getColor(
                        context, it.getStatusColor()
                    )
                )
                binding.tvTxnRoutineDescription.setTextColor(
                    ContextCompat.getColor(
                        context, it.getStatusColor()
                    )
                )
                binding.tvTxnRoutineStatus.setBackgroundResource(
                    it.getStatusBackground()
                )
                binding.tvTxnRoutineStatus.setHtmlText(data.statusText.orEmpty())
            }

            binding.tvTxnRoutineTitle.setTextColor(
                ContextCompat.getColor(
                    context, if (status == NewTransactionRoutineStatus.INACTIVE) com.jar.app.core_ui.R.color.color_776E94 else com.jar.app.core_ui.R.color.white
                )
            )

            data.description?.let {
                binding.tvTxnRoutineDescription.isVisible = true
                binding.divider.isVisible = true
                binding.tvTxnRoutineDescription.setHtmlText(it)
            } ?: kotlin.run {
                binding.tvTxnRoutineDescription.isVisible = false
            }

            data.txnRoutineCtaDetails?.ctaButtonText?.let {
                binding.btnCta.isVisible = true
                binding.divider.isVisible = true
                binding.btnCta.setText(it)
            } ?: kotlin.run {
                binding.btnCta.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewTransactionRoutineViewHolder {
        val binding = FeatureTransactionNewTransactionRoutineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewTransactionRoutineViewHolder(binding, onCtaClicked)
    }

    override fun onBindViewHolder(holder: NewTransactionRoutineViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}