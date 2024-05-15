package com.jar.app.feature_gold_lease.impl.ui.faq

import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseFaqBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseFaqObjects

internal class GoldLeaseFaqViewHolder(
    private val binding: CellGoldLeaseFaqBinding,
    private val onFaqClicked: (faqData: GoldLeaseFaqObjects) -> Unit
): BaseViewHolder(binding.root) {

    private var faqData: GoldLeaseFaqObjects? = null
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)

    init {
        binding.root.setDebounceClickListener {
            faqData?.let(onFaqClicked)
            binding.expandableLayout.setExpanded(binding.expandableLayout.isExpanded.not(), true)
            binding.ivExpand.animate()
                .rotation(if (binding.expandableLayout.isExpanded) 180f else 0f).start()
        }
    }

    fun bind(data: GoldLeaseFaqObjects) {
        faqData = data
        binding.tvHeader.text = data.header.orEmpty()
        val adapter = GoldLeaseSubFaqAdapter()
        binding.rvSubFaqs.adapter = adapter
        binding.rvSubFaqs.layoutManager = LinearLayoutManager(context)
        binding.rvSubFaqs.addItemDecorationIfNoneAdded(spaceItemDecoration)
        adapter.submitList(data.leaseFaqIndividualObjects)
    }
}