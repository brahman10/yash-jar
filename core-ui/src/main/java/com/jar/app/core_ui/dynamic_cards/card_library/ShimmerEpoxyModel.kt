package com.jar.app.core_ui.dynamic_cards.card_library

import android.view.View
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.DynamicCardsCellShimmerBinding
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel

class ShimmerEpoxyModel :
    CustomViewBindingEpoxyModel<DynamicCardsCellShimmerBinding>(R.layout.dynamic_cards_cell_shimmer) {

    override fun getBinding(view: View): DynamicCardsCellShimmerBinding {
        return DynamicCardsCellShimmerBinding.bind(view)
    }

    override fun bindItem(binding: DynamicCardsCellShimmerBinding) {

    }


}