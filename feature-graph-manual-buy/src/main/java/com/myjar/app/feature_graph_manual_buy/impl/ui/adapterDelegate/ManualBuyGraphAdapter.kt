package com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.jar.app.core_ui.calendarView.viewholder.CalenderViewPageItem
import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem
import com.myjar.app.feature_graph_manual_buy.impl.model.ManualBuyGraphFaqsItem
import com.myjar.app.feature_graph_manual_buy.impl.model.ManualBuyGraphItem
import com.myjar.app.feature_graph_manual_buy.impl.model.NeedHelpManualBuyGraphItem

class ManualBuyGraphAdapter (delegates: List<AdapterDelegate<List<PostSetupPageItem>>>):
    AsyncListDifferDelegationAdapter<PostSetupPageItem>(DIFF_UTIL){

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<PostSetupPageItem>() {
            override fun areItemsTheSame(
                oldItem: PostSetupPageItem,
                newItem: PostSetupPageItem
            ): Boolean {
                return oldItem.getSortKey() == newItem.getSortKey()
            }

            override fun areContentsTheSame(
                oldItem: PostSetupPageItem,
                newItem: PostSetupPageItem
            ): Boolean {
                return if (oldItem is ManualBuyGraphItem && newItem is ManualBuyGraphItem)
                    oldItem == newItem
                else if (oldItem is CalenderViewPageItem && newItem is CalenderViewPageItem)
                    oldItem == newItem
                else if (oldItem is ManualBuyGraphFaqsItem && newItem is ManualBuyGraphFaqsItem)
                    oldItem == newItem
                else if (oldItem is NeedHelpManualBuyGraphItem && newItem is NeedHelpManualBuyGraphItem)
                    oldItem == newItem
                else false
            }
        }
    }

    init {
        delegates.forEach {
            delegatesManager.addDelegate(it)
        }
    }

}