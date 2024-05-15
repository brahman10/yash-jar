package com.jar.app.feature_vasooli.impl.ui.add_repayment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.databinding.CellPaymentModeBinding
import com.jar.app.feature_vasooli.impl.domain.model.PaymentMode

internal class PaymentModeAdapter(
    private val onPaymentModeSelected: (paymentMode: PaymentMode) -> Unit
) : ListAdapter<PaymentMode, PaymentModeAdapter.PaymentModeViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PaymentMode>() {
            override fun areItemsTheSame(oldItem: PaymentMode, newItem: PaymentMode): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PaymentMode, newItem: PaymentMode): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentModeViewHolder {
        val binding = CellPaymentModeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentModeViewHolder(binding, onPaymentModeSelected)
    }

    override fun onBindViewHolder(holder: PaymentModeViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class PaymentModeViewHolder(
        private val binding: CellPaymentModeBinding,
        private val onPaymentModeSelected: (paymentMode: PaymentMode) -> Unit
    ) : BaseViewHolder(binding.root) {

        var paymentMode: PaymentMode? = null

        init {
            binding.clRoot.setDebounceClickListener {
                paymentMode?.let {
                    onPaymentModeSelected.invoke(it)
                }
            }
        }

        fun bind(paymentMode: PaymentMode) {
            this.paymentMode = paymentMode

            if (paymentMode.isSelected) {
                binding.clRoot.background = ContextCompat.getDrawable(
                    context, R.drawable.feature_vasooli_bg_payment_mode_selected
                )
            } else {
                binding.clRoot.background = ContextCompat.getDrawable(
                    context, R.drawable.feature_vasooli_bg_payment_mode_not_selected
                )
            }

            binding.ivTick.isVisible = paymentMode.isSelected
            binding.tvTitle.setText(paymentMode.title)
        }
    }

}