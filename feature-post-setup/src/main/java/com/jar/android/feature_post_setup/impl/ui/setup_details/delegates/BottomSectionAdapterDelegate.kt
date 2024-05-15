package com.jar.android.feature_post_setup.impl.ui.setup_details.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellBottomSectionBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders.BottomSectionViewHolder
import com.jar.app.feature_post_setup.domain.model.BottomSectionPageItem
import com.jar.app.feature_post_setup.domain.model.PostSetupPageItem

internal class BottomSectionAdapterDelegate : AdapterDelegate<List<PostSetupPageItem>>() {

    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is BottomSectionPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeaturePostSetupCellBottomSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BottomSectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is BottomSectionViewHolder && item is BottomSectionPageItem)
            holder.setView(item)
    }

}