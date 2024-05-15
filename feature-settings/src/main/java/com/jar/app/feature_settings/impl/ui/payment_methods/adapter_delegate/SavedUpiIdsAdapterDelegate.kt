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
import com.jar.app.feature_settings.databinding.CellSavedUpiIdsViewBinding
import com.jar.app.feature_settings.domain.model.PaymentMethod
import com.jar.app.feature_settings.domain.model.SavedUpiIdsPaymentMethod
import com.jar.app.feature_settings.impl.ui.payment_methods.adapter_delegate.adapters.SavedUpiIdAdapter
import com.jar.app.feature_user_api.domain.model.SavedVPA

internal class SavedUpiIdsAdapterDelegate(
    private val onDeleteClick: (savedVPA: SavedVPA, position: Int) -> Unit,
    private val onAddCLick: () -> Unit
) : AdapterDelegate<List<PaymentMethod>>() {

    override fun isForViewType(items: List<PaymentMethod>, position: Int): Boolean {
        return items[position] is SavedUpiIdsPaymentMethod
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellSavedUpiIdsViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedUpiIdsPaymentMethodViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PaymentMethod>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is SavedUpiIdsPaymentMethod && holder is SavedUpiIdsPaymentMethodViewHolder)
            holder.bind(item.savedUpiIds)
    }

    inner class SavedUpiIdsPaymentMethodViewHolder(private val binding: CellSavedUpiIdsViewBinding) :
        BaseViewHolder(binding.root) {

        private var adapter: SavedUpiIdAdapter? = null

        private val dividerDecorator =
            DividerItemDecoration(binding.root.context, LinearLayoutManager.VERTICAL)
        private val spaceItemDecoration = SpaceItemDecoration(0.dp, 16.dp)

        init {
            binding.ivAdd.setDebounceClickListener {
                onAddCLick.invoke()
            }
        }

        fun bind(savedVpas: List<SavedVPA>) {
            adapter = SavedUpiIdAdapter(
                onDeleteClick = { savedVpa, pos ->
                    onDeleteClick(savedVpa, pos)
                }
            )
            binding.tvSavedUpiIdsLabel.isVisible = savedVpas.isNotEmpty()
            binding.rvSavedUpiIds.layoutManager = LinearLayoutManager(context)
            ContextCompat.getDrawable(binding.root.context, com.jar.app.core_ui.R.drawable.core_ui_line_separator)?.let {
                dividerDecorator.setDrawable(it)
            }
            binding.rvSavedUpiIds.addItemDecorationIfNoneAdded(
                dividerDecorator,
                spaceItemDecoration
            )
            binding.rvSavedUpiIds.adapter = adapter
            adapter?.submitList(savedVpas)
        }

    }
}