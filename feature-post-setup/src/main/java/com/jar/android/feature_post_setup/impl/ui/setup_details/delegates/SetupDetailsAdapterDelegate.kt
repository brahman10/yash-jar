package com.jar.android.feature_post_setup.impl.ui.setup_details.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellSetupDetailsBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders.SetupDetailsViewHolder
import com.jar.app.feature_post_setup.domain.model.PostSetupPageItem
import com.jar.app.feature_post_setup.domain.model.SetupDetailsPageItem

internal class SetupDetailsAdapterDelegate : AdapterDelegate<List<PostSetupPageItem>>() {

    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is SetupDetailsPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeaturePostSetupCellSetupDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SetupDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is SetupDetailsViewHolder && item is SetupDetailsPageItem)
            holder.setDetails(item.userPostSetupData)
    }


}