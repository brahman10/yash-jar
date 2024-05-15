package com.jar.app.feature.invoice.ui

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.jar.app.R
import com.jar.app.base.util.epochToDate
import com.jar.app.base.util.getShortMonth
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.CellInvoiceBinding
import com.jar.app.feature.invoice.domain.model.Invoice
import com.jar.app.feature.invoice.domain.model.InvoiceType
import com.jar.app.core_ui.extension.setDebounceClickListener

internal class InvoiceViewHolder(
    private val binding: CellInvoiceBinding,
    private val onViewClick: (invoice: Invoice) -> Unit
) :
    BaseViewHolder(binding.root) {

    private lateinit var invoice: Invoice

    init {
        binding.tvView.setDebounceClickListener {
            if (::invoice.isInitialized)
                onViewClick.invoke(invoice)
        }
    }

    fun setInvoice(invoice: Invoice) {
        this.invoice = invoice
        binding.clPlaceHolder.isVisible = false
        binding.clContent.isVisible = true
        binding.tvInvoiceName.text = invoice.invoiceId
        binding.tvDate.text = invoice.date.epochToDate().getShortMonth()
        binding.tvAmount.text =
            context.getString(R.string.rupee_x_in_string, invoice.amount.toString())

        binding.tvAmount.setTextColor(invoice.getColorForInvoiceType(context))
        when (invoice.transactionType) {
            InvoiceType.BUY.name -> {
                binding.tvAmount.text =
                    context.getString(R.string.buy_x, invoice.amount)
            }
            InvoiceType.SELL.name -> {
                binding.tvAmount.text =
                    context.getString(R.string.sell_x, invoice.amount)
            }
            InvoiceType.DELIVERY.name -> {
                binding.tvAmount.text =
                    context.getString(R.string.delivery_x, invoice.amount)
            }
            else -> {
                binding.tvAmount.text =
                    context.getString(
                        R.string.transaction_type_amount,
                        invoice.transactionType,
                        invoice.amount
                    )
                binding.tvAmount.setTextColor(
                    ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_EEEAFF)
                )
            }
        }
    }
}