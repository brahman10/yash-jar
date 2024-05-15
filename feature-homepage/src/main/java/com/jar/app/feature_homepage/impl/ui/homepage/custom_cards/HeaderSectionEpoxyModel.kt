package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import androidx.core.view.isVisible
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellHeaderItemDecorationBinding
import com.jar.app.feature_homepage.shared.domain.model.HeaderSection
import java.lang.ref.WeakReference

class HeaderSectionEpoxyModel(
    private val headerSection: HeaderSection,
) : CustomViewBindingEpoxyModel<FeatureHomepageCellHeaderItemDecorationBinding>(
    R.layout.feature_homepage_cell_header_item_decoration
) {

    override fun getBinding(view: View): FeatureHomepageCellHeaderItemDecorationBinding {
        return FeatureHomepageCellHeaderItemDecorationBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageCellHeaderItemDecorationBinding) {
        binding.root.setPlotlineViewTag(headerSection.title.toString())
        binding.tvHeader.text = headerSection.title?.convertToString(WeakReference(binding.root.context))
        val description = headerSection.description?.convertToString(WeakReference(binding.root.context))
        binding.tvDescription.text = description
        binding.tvDescription.isVisible = description.isNullOrBlank().not()
    }
}