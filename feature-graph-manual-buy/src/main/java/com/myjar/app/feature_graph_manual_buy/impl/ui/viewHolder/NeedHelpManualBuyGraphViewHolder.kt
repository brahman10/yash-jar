package com.myjar.app.feature_graph_manual_buy.impl.ui.viewHolder

import com.jar.app.base.ui.BaseResources
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.myjar.app.feature_graph_manual_buy.databinding.NeedHelpManualBuyLayoutBinding
import com.myjar.app.feature_graph_manual_buy.impl.model.NeedHelpManualBuyGraphItem

class NeedHelpManualBuyGraphViewHolder(
    private val binding: NeedHelpManualBuyLayoutBinding,
    private val onClick:(String) -> Unit
): BaseViewHolder(binding.root), BaseResources {
    fun bind(item: NeedHelpManualBuyGraphItem) {
        val quickAction = item.item?.quickActionList?.getOrNull(0)
        with(binding) {
            tvTextView.text = quickAction?.title
            binding.root.setDebounceClickListener {
                onClick.invoke(quickAction?.deepLink ?: "")
            }
        }
    }
}