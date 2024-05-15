package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.item_decoration.TimelineItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardNewTxnRoutineBinding
import com.jar.app.feature_transaction.impl.ui.common.TransactionRoutineAdapterV2
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView

class TxnRoutineCardAdapter(
    private val onViewInvoiceClick: (url: String, title: String, showToolbar: Boolean) -> Unit,
    private val onRetryClicked: () -> Unit,
    private val onSendGiftReminderClick: () -> Unit,
) :
    AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.TxnRoutineDetails
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardNewTxnRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TxnRoutineCardVH(binding, onViewInvoiceClick, onSendGiftReminderClick)
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.TxnRoutineDetails && holder is TxnRoutineCardVH) {
            holder.bindData(item)
        }
    }

    inner class TxnRoutineCardVH(
        private val binding: FeatureTransactionCardNewTxnRoutineBinding,
        private val onViewInvoiceClick: (url: String, title: String, showToolbar: Boolean) -> Unit,
        private val onSendGiftReminderClick: () -> Unit
    ) : BaseViewHolder(binding.root) {

        private var data: com.jar.app.feature_transaction.shared.domain.model.TxnRoutineDetails? = null

        init {
            binding.btnRetry.setDebounceClickListener {
                onRetryClicked()
            }

            binding.btnViewInvoice.setDebounceClickListener {
                data?.invoiceLink?.let { link ->
                    onViewInvoiceClick(
                        link,
                        context.getString(R.string.feature_transaction_invoices),
                        true
                    )
                }
            }

            binding.btnSendReminder.setDebounceClickListener {
                onSendGiftReminderClick.invoke()
            }
        }

        fun bindData(data: com.jar.app.feature_transaction.shared.domain.model.TxnRoutineDetails) {
            this.data = data
            val adapter = TransactionRoutineAdapterV2()
            val spaceItemDecoration = SpaceItemDecoration(0.dp, 10.dp)
            val timelineItemDecoration =
                TimelineItemDecoration(object : TimelineItemDecoration.SectionCallback {
                    override fun isHeaderSection(position: Int): Boolean {
                        return true
                    }

                    override fun getHeaderLayoutRes(position: Int): Int {
                        return R.layout.feature_transaction_cell_progress_timeline
                    }

                    override fun bindHeaderData(view: View, position: Int) {
                        adapter.currentList.getOrNull(position)?.let {
                            view.findViewById<AppCompatImageView>(R.id.ivIcon)
                                .setImageDrawable(it.getLogoForStatus().getDrawable(context))
                            val line = view.findViewById<View>(R.id.line)
                            val isLastItem = (position == (adapter.itemCount.orZero() - 1))
                            line.setBackgroundColor(
                                if (isLastItem) {
                                    ContextCompat.getColor(
                                        context,
                                        com.jar.app.core_ui.R.color.lightBgColor
                                    )
                                } else {
                                    it.getColorForStatus().getColor(context)
                                }
                            )
                        }
                    }
                })
            binding.rvTransactionRoutine.addItemDecorationIfNoneAdded(
                spaceItemDecoration,
                timelineItemDecoration
            )
            binding.rvTransactionRoutine.adapter = adapter

            binding.btnViewInvoice.isVisible = data.invoiceAvailable.orFalse()
            binding.btnSendReminder.isVisible = data.showGiftingReminder.orFalse()
            binding.btnRetry.isVisible = data.retryAllowed.orFalse()
            binding.btnRetry.text =
                if (data.retryButtonTxt.isNullOrBlank()) context.getString(com.jar.app.core_ui.R.string.try_again) else data.retryButtonTxt
            binding.tvError.isVisible = !data.failureReason.isNullOrBlank()
            binding.tvError.text = data.failureReason
            adapter.submitList(data.txnRoutine)
        }
    }
}