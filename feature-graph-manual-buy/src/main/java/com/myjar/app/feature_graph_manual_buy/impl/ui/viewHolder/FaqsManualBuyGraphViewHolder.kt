package com.myjar.app.feature_graph_manual_buy.impl.ui.viewHolder

import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.BaseResources
import com.jar.app.core_base.domain.model.ExpandableDataItem
import com.jar.app.core_ui.expandable_rv.ExpandableItemRVAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.myjar.app.feature_graph_manual_buy.databinding.FaqsGraphManualBuyLayoutBinding
import com.myjar.app.feature_graph_manual_buy.impl.model.ManualBuyGraphFaqsItem

class FaqsManualBuyGraphViewHolder(
    private val binding: FaqsGraphManualBuyLayoutBinding,
    private val onClick:() -> Unit
): BaseViewHolder(binding.root), BaseResources {

    private val faqsAdapter: ExpandableItemRVAdapter by lazy {
        ExpandableItemRVAdapter {
            onClick.invoke()
        }
    }



    fun bind(item: ManualBuyGraphFaqsItem) {
        binding.tvFaqs.text = item.title
        binding.rvFaq.adapter = faqsAdapter
        binding.rvFaq.layoutManager = LinearLayoutManager(binding.root.context)

        binding.tvFaqs.text = item.faq.faqsList?.getOrNull(0)?.type
        faqsAdapter.submitList(
            item.faq.faqsList?.get(0)?.faqs?.map {
                ExpandableDataItem.CardHeaderIsExpandedDataType(
                    question = it.question,
                    answer = it.answer,
                    isExpanded = false
                )
            }
        )
    }
}