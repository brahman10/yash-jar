package com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders

import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellFaqBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.adapter.PostSetupFaqAdapter
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_base.domain.model.GenericFaqItem
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_post_setup.domain.model.PostSetupFaqPageItem

internal class PostSetupFaqViewHolder(
    private val binding: FeaturePostSetupCellFaqBinding,
    private val onFaqClicked: (GenericFaqItem) -> Unit
) : BaseViewHolder(binding.root), BaseResources{

    private var adapter: PostSetupFaqAdapter? = null
    private var spaceItemDecorator = SpaceItemDecoration(0.dp, 6.dp)
    fun setupFaqs(faqPageItem: PostSetupFaqPageItem) {
        binding.tvFaq.text = getCustomString(binding.root.context, faqPageItem.titleRes)
        binding.rvFaqs.layoutManager = LinearLayoutManager(binding.root.context)
        binding.rvFaqs.addItemDecorationIfNoneAdded(spaceItemDecorator)
        adapter = PostSetupFaqAdapter { pos, faqItem ->
            onFaqClicked.invoke(faqItem)
        }
        binding.rvFaqs.adapter = adapter
        adapter?.submitList(faqPageItem.faq)
    }
}