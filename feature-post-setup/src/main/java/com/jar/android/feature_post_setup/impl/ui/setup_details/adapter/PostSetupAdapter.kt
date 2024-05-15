package com.jar.android.feature_post_setup.impl.ui.setup_details.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.jar.app.feature_post_setup.domain.model.BottomSectionPageItem
import com.jar.app.feature_post_setup.domain.model.PostSetupFaqPageItem
import com.jar.app.feature_post_setup.domain.model.PostSetupPageItem
import com.jar.app.feature_post_setup.domain.model.SettingPageItem
import com.jar.app.feature_post_setup.domain.model.SetupDetailsPageItem
import com.jar.app.feature_post_setup.domain.model.CalenderViewPageItem
import com.jar.app.feature_post_setup.domain.model.StateAmountInfoPageItem

internal class PostSetupAdapter(delegates: List<AdapterDelegate<List<PostSetupPageItem>>>) :
    AsyncListDifferDelegationAdapter<PostSetupPageItem>(DIFF_UTIL) {

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
                return if (oldItem is SetupDetailsPageItem && newItem is SetupDetailsPageItem)
                    oldItem == newItem
                else if (oldItem is CalenderViewPageItem && newItem is CalenderViewPageItem)
                    oldItem == newItem
                else if (oldItem is StateAmountInfoPageItem && newItem is StateAmountInfoPageItem)
                    oldItem == newItem
                else if (oldItem is SettingPageItem && newItem is SettingPageItem)
                    oldItem == newItem
                else if (oldItem is PostSetupFaqPageItem && newItem is PostSetupFaqPageItem)
                    oldItem == newItem
                else if (oldItem is BottomSectionPageItem && newItem is BottomSectionPageItem)
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