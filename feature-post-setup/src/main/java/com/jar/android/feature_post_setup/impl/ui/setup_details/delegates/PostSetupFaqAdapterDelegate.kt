package com.jar.android.feature_post_setup.impl.ui.setup_details.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellFaqBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders.PostSetupFaqViewHolder
import com.jar.app.core_base.domain.model.GenericFaqItem
import com.jar.app.feature_post_setup.domain.model.PostSetupFaqPageItem
import com.jar.app.feature_post_setup.domain.model.PostSetupPageItem

internal class PostSetupFaqAdapterDelegate(private val onFaqClicked: (GenericFaqItem) -> Unit) :
    AdapterDelegate<List<PostSetupPageItem>>() {

    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is PostSetupFaqPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeaturePostSetupCellFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostSetupFaqViewHolder(binding,onFaqClicked)
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is PostSetupFaqViewHolder && item is PostSetupFaqPageItem)
            holder.setupFaqs(item)
    }
}