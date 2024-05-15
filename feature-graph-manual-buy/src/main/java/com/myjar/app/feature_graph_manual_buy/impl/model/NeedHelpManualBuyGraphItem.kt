package com.myjar.app.feature_graph_manual_buy.impl.model

import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem
import dev.icerock.moko.resources.StringResource
import com.myjar.app.feature_graph_manual_buy.data.model.QuickActionResponse

data class NeedHelpManualBuyGraphItem(
    val order: Int,
    val title: String,
    val item: QuickActionResponse?
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}