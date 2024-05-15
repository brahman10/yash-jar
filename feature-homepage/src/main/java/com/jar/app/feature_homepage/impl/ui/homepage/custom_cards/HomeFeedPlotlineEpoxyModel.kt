package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepagePlotlineWidgetBinding
import kotlinx.coroutines.Job

internal class HomeFeedPlotlineEpoxyModel(
    override var cardShownEventJob: Job? = null,
    private val featureType: String
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepagePlotlineWidgetBinding>(R.layout.feature_homepage_plotline_widget) {

    override fun getBinding(view: View): FeatureHomepagePlotlineWidgetBinding {
        return FeatureHomepagePlotlineWidgetBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepagePlotlineWidgetBinding) {
        binding.root.setPlotlineViewTag(featureType)
    }
}