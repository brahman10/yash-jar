package com.jar.app.feature_gold_lease.impl.ui.post_order

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.util.*
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.databinding.CellGoldLeasePostOrderDetailsBinding
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePostOrderDetailsItemList
import com.jar.app.feature_gold_lease.impl.ui.jeweller_details.GoldLeaseV2TitleValuePairAdapter

internal class GoldLeaseOrderDetailsViewHolder(
    private val binding: CellGoldLeasePostOrderDetailsBinding
) : BaseViewHolder(binding.root) {

    private var goldLeaseV2TitleValuePairAdapter: GoldLeaseV2TitleValuePairAdapter? = null
    private val spaceItemDecorationVertical = SpaceItemDecoration(0.dp, 4.dp)

    fun bind(data: LeasePostOrderDetailsItemList) {
        binding.tvPostOrderDetailsTitle.isVisible = data.title != null
        binding.tvPostOrderDetailsTitle.setHtmlText(data.title.orEmpty())
        goldLeaseV2TitleValuePairAdapter = GoldLeaseV2TitleValuePairAdapter(
            onClickedCopyTransactionId = {
                context.copyToClipboard(it.value.orEmpty())
            },
            onWebsiteClicked = {
                openUrlInChromeTabOrExternalBrowser(context, it)
            }
        )
        binding.rvPostOrderDetailsList.adapter = goldLeaseV2TitleValuePairAdapter
        binding.rvPostOrderDetailsList.layoutManager = LinearLayoutManager(context)
        binding.rvPostOrderDetailsList.addItemDecorationIfNoneAdded(spaceItemDecorationVertical)
        goldLeaseV2TitleValuePairAdapter?.submitList(data.rowsList)
    }
}