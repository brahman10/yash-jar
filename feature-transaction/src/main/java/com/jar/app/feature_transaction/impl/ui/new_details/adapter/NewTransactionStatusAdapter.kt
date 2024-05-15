package com.jar.app.feature_transaction.impl.ui.new_details.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardNewTransactionStatusBinding
import com.jar.app.feature_transaction.impl.domain.model.getStatusColor
import com.jar.app.feature_transaction.impl.domain.model.getStatusIcon
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.NewTransactionDetailsCardView
import com.jar.app.feature_transactions_common.shared.NewTransactionRoutine
import com.jar.app.feature_transactions_common.shared.NewTransactionRoutineStatus
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.TransactionStatusCard

internal class NewTransactionStatusAdapter(
    private val onCtaClicked: (newTransactionRoutine: NewTransactionRoutine) -> Unit,
    private val onDownloadInvoiceClicked: (invoiceLink: String) -> Unit
) : AdapterDelegate<List<NewTransactionDetailsCardView>>() {
    override fun isForViewType(items: List<NewTransactionDetailsCardView>, position: Int): Boolean {
        return items[position] is TransactionStatusCard
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureTransactionCardNewTransactionStatusBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewTransactionStatusViewHolder(binding, onCtaClicked, onDownloadInvoiceClicked)
    }

    override fun onBindViewHolder(
        items: List<NewTransactionDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is TransactionStatusCard && holder is NewTransactionStatusViewHolder) {
            holder.bindData(item)
        }
    }

    inner class NewTransactionStatusViewHolder(
        private val binding: FeatureTransactionCardNewTransactionStatusBinding,
        private val onCtaClicked: (newTransactionRoutine: NewTransactionRoutine) -> Unit,
        private val onDownloadInvoiceClicked: (invoiceLink: String) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var invoiceLink: String? = null
        private val spaceItemDecorationVertical4 = SpaceItemDecoration(0.dp, 4.dp)

        init {
            binding.btnDownloadInvoice.setDebounceClickListener {
                invoiceLink?.let {
                    onDownloadInvoiceClicked.invoke(it)
                }
            }
        }

        fun bindData(data: TransactionStatusCard) {
            this.invoiceLink = data.invoiceLink

            binding.tvTransactionTitle.setHtmlText(data.title.orEmpty())

            val newTransactionRoutineAdapter = NewTransactionRoutineAdapter {
                onCtaClicked.invoke(it)
            }

            val timelineItemDecoration =
                com.jar.app.core_ui.item_decoration.TimelineItemDecoration(object :
                    com.jar.app.core_ui.item_decoration.TimelineItemDecoration.SectionCallback {
                    override fun isHeaderSection(position: Int): Boolean {
                        return true
                    }

                    override fun getHeaderLayoutRes(position: Int): Int {
                        return com.jar.app.core_ui.R.layout.core_ui_cell_progress_timeline
                    }

                    override fun bindHeaderData(view: View, position: Int) {
                        newTransactionRoutineAdapter.currentList.getOrNull(position)?.let {
                            val routineStatus = it.getTxnRoutineStatus()
                            routineStatus.let { status ->
                                view.findViewById<AppCompatImageView>(com.jar.app.core_ui.R.id.ivIcon)
                                    .setImageDrawable(
                                        ContextCompat.getDrawable(
                                            context, status.getStatusIcon()
                                        )
                                    )
                                val line = view.findViewById<View>(com.jar.app.core_ui.R.id.line)
                                val isLastItem =
                                    (position == (newTransactionRoutineAdapter.itemCount.orZero() - 1))
                                val nextItem = if (isLastItem.not()) newTransactionRoutineAdapter.currentList.getOrNull(position+1) else null
                                view.findViewById<AppCompatImageView>(com.jar.app.core_ui.R.id.ivDashedCircle).isInvisible = it.currentStep.orFalse().not()

                                line.setBackgroundColor(
                                    ContextCompat.getColor(
                                        binding.root.context,
                                        if (isLastItem) com.jar.app.core_ui.R.color.lightBgColor else if (nextItem?.getTxnRoutineStatus() == NewTransactionRoutineStatus.INACTIVE) com.jar.app.core_ui.R.color.color_776E94  else status.getStatusColor()
                                    )
                                )
                            }
                        }
                    }
                })
            binding.rvTransactionRoutine.adapter = newTransactionRoutineAdapter
            binding.rvTransactionRoutine.layoutManager = LinearLayoutManager(context)
            binding.rvTransactionRoutine.addItemDecorationIfNoneAdded(timelineItemDecoration, spaceItemDecorationVertical4)

            data.txnRoutineList?.let {
                newTransactionRoutineAdapter.submitList(it)
            }

            data.invoiceLink?.let {
                binding.btnDownloadInvoice.isVisible = true
            } ?: kotlin.run {
                binding.btnDownloadInvoice.isVisible = false
            }
        }
    }
}