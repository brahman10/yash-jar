package com.jar.app.feature.invoice.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.jar.app.databinding.CellInvoiceBinding
import com.jar.app.feature.invoice.domain.model.Invoice

internal class InvoiceAdapter(
    private val onViewClick: (invoice: Invoice) -> Unit
) : PagingDataAdapter<Invoice, InvoiceViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Invoice>() {
            override fun areItemsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
                return oldItem.invoiceId == newItem.invoiceId
            }

            override fun areContentsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = InvoiceViewHolder(
        CellInvoiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),onViewClick
    )

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setInvoice(it)
        }
    }

}