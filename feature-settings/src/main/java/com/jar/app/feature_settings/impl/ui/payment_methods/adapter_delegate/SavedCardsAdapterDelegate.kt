package com.jar.app.feature_settings.impl.ui.payment_methods.adapter_delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_settings.databinding.CellSavedCardsViewBinding
import com.jar.app.feature_settings.domain.model.PaymentMethod
import com.jar.app.feature_settings.domain.model.SavedCard
import com.jar.app.feature_settings.domain.model.SavedCardPaymentMethod
import com.jar.app.feature_settings.impl.ui.payment_methods.adapter_delegate.adapters.SavedCardAdapter

internal class SavedCardsAdapterDelegate(
    private val onDeleteClick: (savedCard: SavedCard, position: Int) -> Unit
) : AdapterDelegate<List<PaymentMethod>>() {

    override fun isForViewType(items: List<PaymentMethod>, position: Int): Boolean {
        return items[position] is SavedCardPaymentMethod
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellSavedCardsViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedCardSectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PaymentMethod>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is SavedCardPaymentMethod && holder is SavedCardSectionViewHolder)
            holder.bind(item.cards)
    }

    inner class SavedCardSectionViewHolder(private val binding: CellSavedCardsViewBinding) :
        BaseViewHolder(binding.root) {

        private var adapter: SavedCardAdapter? = null
        private val dividerDecorator =
            DividerItemDecoration(binding.root.context, LinearLayoutManager.VERTICAL)
        private val spaceItemDecoration = SpaceItemDecoration(0.dp, 16.dp)

        fun bind(cards: List<SavedCard>) {
            adapter = SavedCardAdapter(
                onDeleteClick = { savedCard, pos ->
                    onDeleteClick.invoke(savedCard, pos)
                }
            )
            binding.tvSavedCardsLabel.isVisible = cards.isNotEmpty()
            binding.rvSavedCards.layoutManager = LinearLayoutManager(context)
            binding.rvSavedCards.adapter = adapter
            ContextCompat.getDrawable(binding.root.context, com.jar.app.core_ui.R.drawable.core_ui_line_separator)?.let {
                dividerDecorator.setDrawable(it)
            }
            binding.rvSavedCards.addItemDecorationIfNoneAdded(dividerDecorator, spaceItemDecoration)
            adapter?.submitList(cards)
        }

    }
}