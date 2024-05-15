package com.myjar.app.feature_graph_manual_buy.impl.model

import com.jar.app.core_base.domain.model.GenericFaqItem
import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem
import com.myjar.app.feature_graph_manual_buy.data.model.GraphManualBuyPriceGraphModel
import dev.icerock.moko.resources.StringResource

data class ManualBuyGraphItem(
    val order: Int,
    val graphItem: GraphManualBuyPriceGraphModel?
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}