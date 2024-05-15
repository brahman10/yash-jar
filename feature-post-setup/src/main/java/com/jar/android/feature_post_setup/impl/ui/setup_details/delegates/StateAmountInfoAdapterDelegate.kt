package com.jar.android.feature_post_setup.impl.ui.setup_details.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellStateAmountInfoBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders.StateAmountInfoViewHolder
import com.jar.app.feature_post_setup.domain.model.PostSetupPageItem
import com.jar.app.feature_post_setup.domain.model.StateAmountInfoPageItem
import com.jar.app.feature_post_setup.domain.model.calendar.AmountInfo

internal class StateAmountInfoAdapterDelegate(
    private val onPaymentClick: (AmountInfo) -> Unit,
) : AdapterDelegate<List<PostSetupPageItem>>() {

    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is StateAmountInfoPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeaturePostSetupCellStateAmountInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StateAmountInfoViewHolder(binding, onPaymentClick)
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is StateAmountInfoViewHolder && item is StateAmountInfoPageItem)
            holder.setData(item.stateInfoDetails)
    }


}