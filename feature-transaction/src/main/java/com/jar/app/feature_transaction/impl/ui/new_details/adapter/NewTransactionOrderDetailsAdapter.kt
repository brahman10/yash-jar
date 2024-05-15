package com.jar.app.feature_transaction.impl.ui.new_details.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardOrderDetailsBinding
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.NewTransactionDetailsCardView
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.TransactionOrderDetailsComponent

class NewTransactionOrderDetailsAdapter : AdapterDelegate<List<NewTransactionDetailsCardView>>() {
    override fun isForViewType(items: List<NewTransactionDetailsCardView>, position: Int): Boolean {
        return items[position] is TransactionOrderDetailsComponent
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureTransactionCardOrderDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NewTransactionOrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<NewTransactionDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is TransactionOrderDetailsComponent && holder is NewTransactionOrderDetailsViewHolder) {
            holder.bind(item)
        }
    }

    inner class NewTransactionOrderDetailsViewHolder(
        private val binding: FeatureTransactionCardOrderDetailsBinding
    ): BaseViewHolder(binding.root) {

        private val spaceItemDecorationVertical12 = SpaceItemDecoration(0.dp, 12.dp)

        init {
            binding.ivExpandOrderDetails.setDebounceClickListener {
                expandOrderDetails()
            }

            binding.tvOrderTitle.setDebounceClickListener {
                expandOrderDetails()
            }
        }

        private fun expandOrderDetails() {
            binding.ivExpandOrderDetails.animate()
                .rotation(if (binding.elOrderDetails.isExpanded) 0f else 180f).start()
            binding.elOrderDetails.toggle()
        }

        fun bind(data: TransactionOrderDetailsComponent) {
             binding.tvOrderTitle.setHtmlText(data.title.orEmpty())

             val orderDetailsRowAdapter = NewTransactionOrderDetailsRowAdapter()
             binding.rvOrderDetails.adapter = orderDetailsRowAdapter
             binding.rvOrderDetails.layoutManager = LinearLayoutManager(context)

             val dividerDecorator =
                 object : DividerItemDecoration(context, LinearLayoutManager.VERTICAL) {
                     override fun getItemOffsets(
                         outRect: Rect,
                         view: View,
                         parent: RecyclerView,
                         state: RecyclerView.State
                     ) {
                         val position = parent.getChildAdapterPosition(view)
                         val current = orderDetailsRowAdapter.currentList.getOrNull(position)?.title
                         val prev = orderDetailsRowAdapter.currentList.getOrNull(position + 1)?.title
                         if (current != prev)
                             outRect.setEmpty()
                         else
                             super.getItemOffsets(outRect, view, parent, state)
                     }
                 }
             ContextCompat.getDrawable(
                 context,
                 com.jar.app.core_ui.R.drawable.core_ui_line_separator
             )?.let {
                 dividerDecorator.setDrawable(it)
             }

             binding.rvOrderDetails.addItemDecorationIfNoneAdded(spaceItemDecorationVertical12, dividerDecorator)

             data.orderDetailsCardList?.let {
                 orderDetailsRowAdapter.submitList(it)
             }
         }
    }
}